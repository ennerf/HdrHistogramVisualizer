package us.hebi.histogram.visualizer.parser;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import org.HdrHistogram.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;

import static com.google.common.base.Preconditions.*;
import static us.hebi.histogram.visualizer.parser.HistogramTag.convertPercentileToX;

/**
 * Class that sums multiple histograms for creating percentile data.
 *
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 19 Sep 2016
 */
abstract class HistogramAccumulator {

    static HistogramAccumulator forTypeOf(EncodableHistogram expectedHistogram) {
        checkNotNull(expectedHistogram);
        if (expectedHistogram instanceof DoubleHistogram) {
            return new DoubleHistogramAccumulator((DoubleHistogram) expectedHistogram);
        }
        if (expectedHistogram instanceof AbstractHistogram) {
            return new AbstractHistogramAccumulator((AbstractHistogram) expectedHistogram);
        }
        throw new IllegalArgumentException("Unknown histogram type: " + expectedHistogram.getClass().getSimpleName());
    }

    abstract void addHistogram(EncodableHistogram histogram);

    abstract List<Data<Number, Number>> getPercentileData(
            int percentileOutputTicksPerHalf,
            DoubleUnaryOperator xConversion,
            DoubleUnaryOperator yConversion);

    /**
     * Accumulator for DoubleHistogram intervals
     */
    private static class DoubleHistogramAccumulator extends HistogramAccumulator {

        private DoubleHistogramAccumulator(DoubleHistogram interval) {
            this.accumulatedHistogram = interval.copy();
            accumulatedHistogram.reset();
            interval.setAutoResize(true);
        }

        @Override
        protected void addHistogram(EncodableHistogram histogram) {
            checkArgument(histogram instanceof DoubleHistogram, "Unexpected type");
            accumulatedHistogram.add((DoubleHistogram) histogram);
        }

        @Override
        List<Data<Number, Number>> getPercentileData(int percentileOutputTicksPerHalf,
                                                     DoubleUnaryOperator xConversion,
                                                     DoubleUnaryOperator yConversion) {

            DoublePercentileIterator percentileIterator = new DoublePercentileIterator(
                    accumulatedHistogram,
                    percentileOutputTicksPerHalf);

            List<Data<Number, Number>> percentileData = new ArrayList<>(512);

            while (percentileIterator.hasNext()) {
                DoubleHistogramIterationValue value = percentileIterator.next();

                final double x = xConversion.applyAsDouble(value.getPercentileLevelIteratedTo());
                final double y = yConversion.applyAsDouble(value.getValueIteratedTo());

                if (Double.isInfinite(x))
                    break;

                percentileData.add(new Data<>(x, y));
            }

            return percentileData;
        }

        final DoubleHistogram accumulatedHistogram;

    }

    /**
     * Accumulator for histograms derived from AbstractHistogram, e.g.,
     * - Histogram
     * - IntCountsHistogram
     * - ShortCountsHistogram
     */
    private static class AbstractHistogramAccumulator extends HistogramAccumulator {

        private AbstractHistogramAccumulator(AbstractHistogram interval) {
            accumulatedHistogram = interval.copy();
            accumulatedHistogram.reset();
            accumulatedHistogram.setAutoResize(true);
        }

        @Override
        protected void addHistogram(EncodableHistogram histogram) {
            // NOTE: can a short histogram be added to e.g. an int histogram? Should there be type equality check?
            checkArgument(histogram instanceof AbstractHistogram, "Unexpected type");
            accumulatedHistogram.add((AbstractHistogram) histogram);
        }

        @Override
        List<Data<Number, Number>> getPercentileData(int percentileOutputTicksPerHalf,
                                                     DoubleUnaryOperator xConversion,
                                                     DoubleUnaryOperator yConversion) {
            PercentileIterator percentileIterator = new PercentileIterator(
                    accumulatedHistogram,
                    percentileOutputTicksPerHalf);

            List<Data<Number, Number>> percentileData = new ArrayList<>(512);

            while (percentileIterator.hasNext()) {
                HistogramIterationValue value = percentileIterator.next();

                final double x = xConversion.applyAsDouble(value.getPercentileLevelIteratedTo());
                final double y = yConversion.applyAsDouble(value.getValueIteratedTo());

                if (Double.isInfinite(x))
                    break;

                percentileData.add(new Data<>(x, y));
            }

            return percentileData;
        }

        final AbstractHistogram accumulatedHistogram;

    }


}
