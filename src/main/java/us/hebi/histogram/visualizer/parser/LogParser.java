package us.hebi.histogram.visualizer.parser;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import org.HdrHistogram.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.*;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 04 Jul 2015
 */
public class LogParser {

    public void parseLog(final ParserConfiguration config) throws IOException {
        checkNotNull(config);

        try (InputStream inputStream = new FileInputStream(config.getInputFile())) {
            HistogramLogReader logReader = new HistogramLogReader(inputStream);

            intervalData.clear();
            percentileData.clear();
            EncodableHistogram intervalHistogram = null;
            EncodableHistogram accumulatedHistogram = null;
            final List<Data<Number, Number>> rawIntervalData = new ArrayList<>(INITIAL_LIST_SIZE);

            while (true) {

                // Read interval
                intervalHistogram = logReader.nextIntervalHistogram(config.getStartTimeSec(), config.getEndTimeSec());
                if (intervalHistogram == null)
                    break;

                // Initialize accumulated once
                if (accumulatedHistogram == null) {
                    accumulatedHistogram = copyAsAccumulatorHistogram(intervalHistogram);
                }

                // Make sure histogram versions didn't change within a single log
                checkState(intervalHistogram.getClass().equals(accumulatedHistogram.getClass()),
                        "Encountered different Histogram versions in a single log");

                // Accumulate
                addToHistogram(accumulatedHistogram, intervalHistogram);

                // Add to interval data
                final double x = intervalHistogram.getStartTimeStamp() * 1E-3 - logReader.getStartTimeSec();
                final double y = intervalHistogram.getMaxValueAsDouble() / config.getOutputValueUnitRatio();
                rawIntervalData.add(new Data<>(x, y));

            }

            // Aggregate intervals for faster plotting
            storeAggregatedIntervals(rawIntervalData, config.getAggregateIntervalSamples());

            // Skip if bounds have no data, e.g., when start and end = 0
            if (accumulatedHistogram == null)
                return;
            addPercentileData(accumulatedHistogram, config);

        }

    }

    private void storeAggregatedIntervals(List<Data<Number, Number>> rawIntervals, int samples) {
        checkArgument(samples > 0, "Can't aggregate negative intervals.");

        // Resize aggregated list
        final int rawSize = rawIntervals.size();
        intervalData.clear();
        intervalData.ensureCapacity(rawSize / samples + 1);

        // Fast-track default case
        if (samples == 1) {
            intervalData.addAll(rawIntervals);
            return;
        }

        // Stride through raw data and store the maxima within a time interval
        for (int i = 0; i < rawSize; i += samples) {

            // Use lowest x value as time interval as that results in the same
            // behavior as if the histograms would have been recorded in
            // larger intervals from the beginning.
            Number x = rawIntervals.get(i).getXValue();
            Number y = rawIntervals.get(i).getYValue();

            // Search for highest y value as we are interested in the maxima
            // of each interval.
            for (int j = 1; j < samples && (i + j) < rawSize; j++) {
                Number yNext = rawIntervals.get(i + j).getYValue();
                if (yNext.doubleValue() > y.doubleValue()) {
                    y = yNext;
                }
            }

            intervalData.add(new Data<>(x, y));

        }
    }

    static void outputHistogramValuesAsCsv(AbstractHistogram histogram, PrintStream out) {

        out.print("valueIteratedTo,");
        out.print("countAtValueIteratedTo,");
        out.print("totalCountToThisValue,");
        out.print("totalValueToThisValue\n");

        AllValuesIterator percentileIterator = new AllValuesIterator(histogram);
        while (percentileIterator.hasNext()) {
            HistogramIterationValue next = percentileIterator.next();

            out.print(next.getValueIteratedTo());
            out.print(",");
            out.print(next.getCountAtValueIteratedTo());
            out.print(",");
            out.print(next.getTotalCountToThisValue());
            out.print(",");
            out.print(next.getTotalValueToThisValue());
            out.print(",\n");

        }

    }

    private void addPercentileData(EncodableHistogram accumulatedHistogram, ParserConfiguration config) {
        if (accumulatedHistogram instanceof DoubleHistogram) {
            DoublePercentileIterator percentileIterator = new DoublePercentileIterator(
                    (DoubleHistogram) accumulatedHistogram,
                    config.getPercentilesOutputTicksPerHalf());

            while (percentileIterator.hasNext()) {
                DoubleHistogramIterationValue iterationValue = percentileIterator.next();
                addPercentileDatapoint(
                        iterationValue.getPercentileLevelIteratedTo(),
                        iterationValue.getValueIteratedTo(),
                        config.getOutputValueUnitRatio());
            }
        } else {
            PercentileIterator percentileIterator = new PercentileIterator(
                    (Histogram) accumulatedHistogram,
                    config.getPercentilesOutputTicksPerHalf());

            while (percentileIterator.hasNext()) {
                HistogramIterationValue iterationValue = percentileIterator.next();
                addPercentileDatapoint(
                        iterationValue.getPercentileLevelIteratedTo(),
                        iterationValue.getValueIteratedTo(),
                        config.getOutputValueUnitRatio());
            }
        }
    }

    private EncodableHistogram copyAsAccumulatorHistogram(EncodableHistogram interval) {
        if (interval instanceof DoubleHistogram) {
            DoubleHistogram accumulated = ((DoubleHistogram) interval).copy();
            accumulated.reset();
            accumulated.setAutoResize(true);
            return accumulated;
        } else {
            Histogram accumulatedHistogram = ((Histogram) interval).copy();
            accumulatedHistogram.reset();
            accumulatedHistogram.setAutoResize(true);
            return accumulatedHistogram;
        }
    }

    private void addToHistogram(EncodableHistogram accumulated, EncodableHistogram interval) {
        if (accumulated instanceof DoubleHistogram) {
            ((DoubleHistogram) accumulated).add((DoubleHistogram) interval);
        } else {
            ((Histogram) accumulated).add((Histogram) interval);
        }
    }

    public void getIntervalData(final ObservableList<Data<Number, Number>> targetList) {
        targetList.setAll(intervalData);
    }

    public void getPercentileData(final ObservableList<Data<Number, Number>> targetList) {
        targetList.setAll(percentileData);
    }

    private void addPercentileDatapoint(double percentileLevelIteratedTo, double valueIteratedTo, double unitRatio) {
        // x = 1 / (1 - percentage)
        final double x = 1 / (1.0D - (percentileLevelIteratedTo / 100.0D));
        final double y = valueIteratedTo / unitRatio;

        // Remove last data point so that inf doesn't mess up auto scaling
        if (Double.isInfinite(x))
            return;

        // Add x on log axis
        percentileData.add(new Data<>(Math.log10(x), y));
    }

    private static final int INITIAL_LIST_SIZE = 512;
    private final ArrayList<Data<Number, Number>> intervalData = new ArrayList<>(INITIAL_LIST_SIZE);
    private final List<Data<Number, Number>> percentileData = new ArrayList<>(INITIAL_LIST_SIZE);

}
