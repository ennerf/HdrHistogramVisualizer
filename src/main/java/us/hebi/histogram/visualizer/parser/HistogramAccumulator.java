package us.hebi.histogram.visualizer.parser;

import gnu.trove.list.array.TDoubleArrayList;
import javafx.scene.chart.XYChart.Data;
import org.HdrHistogram.*;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.*;

/**
 * Class that accumulates multiple interval histograms into a series of the
 * interval maxima, and percentile data of all combined histograms.
 *
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 18 Sep 2016
 */
public abstract class HistogramAccumulator {

    public static HistogramAccumulator createEmptyForType(String tag,
                                                          double logStartTimeSec,
                                                          double valueUnitRatio,
                                                          EncodableHistogram expectedHistogramType) {
        checkNotNull(tag);
        HistogramAccumulator accumulator = null;
        if (expectedHistogramType instanceof DoubleHistogram) {
            accumulator = new DoubleHistogramAccumulator((DoubleHistogram) expectedHistogramType);
        } else if (expectedHistogramType instanceof AbstractHistogram) {
            accumulator = new AbstractHistogramAccumulator((AbstractHistogram) expectedHistogramType);
        } else {
            throw new IllegalArgumentException("Unknown histogram type: " + expectedHistogramType.getClass().getSimpleName());
        }
        accumulator.tag = tag;
        accumulator.logStartTimeSec = logStartTimeSec;
        accumulator.valueUnitRatio = valueUnitRatio;
        return accumulator;
    }

    private HistogramAccumulator() {
    }

    public String getTag() {
        return tag;
    }

    public List<Data<Number, Number>> getIntervalData(int samples) {
        final int rawSize = intervalX.size();
        checkState(intervalY.size() == rawSize, "x/y sizes do not match");
        List<Data<Number, Number>> intervalData = new ArrayList<>((rawSize / samples) + 1);

        // Stride through raw data and store the maxima within a time interval
        for (int i = 0; i < rawSize; i += samples) {

            // Use lowest x value as time interval as that results in the same
            // behavior as if the histograms would have been recorded in
            // larger intervals from the beginning.
            double x = intervalX.get(i);
            double y = intervalY.get(i);

            // Search for highest y value as we are interested in the maxima
            // of each interval.
            for (int j = 1; j < samples && (i + j) < rawSize; j++) {
                double yNext = intervalY.get(i + j);
                if (yNext > y) {
                    y = yNext;
                }
            }

            intervalData.add(new Data<Number, Number>(x, convertValueUnits(y)));
        }

        return intervalData;

    }

    protected void appendHistogram(EncodableHistogram histogram) {
        intervalX.add(histogram.getStartTimeStamp() * 1E-3 - logStartTimeSec);
        intervalY.add(histogram.getMaxValueAsDouble());
        addHistogram(histogram);
    }

    protected abstract void addHistogram(EncodableHistogram histogram);

    public abstract List<Data<Number, Number>> getPercentileData(int percentileOutputTicksPerHalf);

    private String tag;
    private double logStartTimeSec = 0;
    protected double valueUnitRatio = 1;

    private final TDoubleArrayList intervalX = new TDoubleArrayList(1024);
    private final TDoubleArrayList intervalY = new TDoubleArrayList(1024);

    /**
     * x = 1 / (1 - percentage)
     */
    protected static double convertPercentileToX(double percentileLevelIteratedTo) {
        double x = 1 / (1.0D - (percentileLevelIteratedTo / 100.0D));
        return Math.log10(x);
    }

    protected double convertValueUnits(double rawValue) {
        return rawValue / valueUnitRatio;
    }


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
        public List<Data<Number, Number>> getPercentileData(int percentileOutputTicksPerHalf) {
            DoublePercentileIterator percentileIterator = new DoublePercentileIterator(
                    accumulatedHistogram,
                    percentileOutputTicksPerHalf);

            List<Data<Number, Number>> percentileData = new ArrayList<>(512);

            while (percentileIterator.hasNext()) {
                DoubleHistogramIterationValue value = percentileIterator.next();

                final double x = convertPercentileToX(value.getPercentileLevelIteratedTo());
                final double y = convertValueUnits(value.getValueIteratedTo());

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
        public List<Data<Number, Number>> getPercentileData(int percentileOutputTicksPerHalf) {
            PercentileIterator percentileIterator = new PercentileIterator(
                    accumulatedHistogram,
                    percentileOutputTicksPerHalf);

            List<Data<Number, Number>> percentileData = new ArrayList<>(512);

            while (percentileIterator.hasNext()) {
                HistogramIterationValue value = percentileIterator.next();

                final double x = convertPercentileToX(value.getPercentileLevelIteratedTo());
                final double y = convertValueUnits(value.getValueIteratedTo());

                if (Double.isInfinite(x))
                    break;

                percentileData.add(new Data<>(x, y));
            }

            return percentileData;
        }

        final AbstractHistogram accumulatedHistogram;

    }

}
