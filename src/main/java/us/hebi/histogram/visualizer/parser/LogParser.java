package us.hebi.histogram.visualizer.parser;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import org.HdrHistogram.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

            while (true) {
                // Read interval
                intervalHistogram = logReader.nextIntervalHistogram(config.getStartTimeSec(), config.getEndTimeSec());
                if (intervalHistogram == null)
                    break;

                // Initialize accumulated once
                if (accumulatedHistogram == null) {
                    if (intervalHistogram instanceof DoubleHistogram) {
                        DoubleHistogram tmpHistogram = ((DoubleHistogram) intervalHistogram).copy();
                        tmpHistogram.reset();
                        tmpHistogram.setAutoResize(true);
                        accumulatedHistogram = tmpHistogram;
                    } else {
                        Histogram tmpHistogram = ((Histogram) intervalHistogram).copy();
                        tmpHistogram.reset();
                        tmpHistogram.setAutoResize(true);
                        accumulatedHistogram = tmpHistogram;
                    }
                }

                // Make sure histogram versions remain the same
                checkState(intervalHistogram.getClass().equals(accumulatedHistogram.getClass()),
                        "Encountered different Histogram versions in a single log");

                // Accumulate
                if (accumulatedHistogram instanceof DoubleHistogram) {
                    ((DoubleHistogram) accumulatedHistogram).add((DoubleHistogram) intervalHistogram);
                } else {
                    ((Histogram) accumulatedHistogram).add((Histogram) intervalHistogram);
                }

                // Add to interval data
                final double x = intervalHistogram.getEndTimeStamp() * 1E-3 - logReader.getStartTimeSec();
                final double y = intervalHistogram.getMaxValueAsDouble() / config.getOutputValueUnitRatio();
                intervalData.add(new Data<>(x, y));

            }

            // Skip if bounds have no data, e.g., when start and end = 0
            if (accumulatedHistogram == null)
                return;

            // Add percentile data
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

    private final List<Data<Number, Number>> intervalData = new ArrayList<>(512);
    private final List<Data<Number, Number>> percentileData = new ArrayList<>(512);

}
