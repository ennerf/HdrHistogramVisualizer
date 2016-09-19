package us.hebi.histogram.visualizer.parser;

import gnu.trove.list.array.TDoubleArrayList;
import javafx.scene.chart.XYChart.Data;
import org.HdrHistogram.*;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.*;

/**
 * Class that represents interval and percentile data corresponding to one
 * tag inside a histogram log.
 *
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 18 Sep 2016
 */
public class HistogramTag {

    public static HistogramTag createEmptyForType(String tag,
                                                  double logStartTimeSec,
                                                  double valueUnitRatio,
                                                  EncodableHistogram expectedHistogramType) {
        HistogramAccumulator accumulator = HistogramAccumulator.forTypeOf(expectedHistogramType);
        return new HistogramTag(tag, logStartTimeSec, valueUnitRatio, accumulator);
    }

    private HistogramTag(String tagId, double logStartTimeSec, double valueUnitRatio, HistogramAccumulator accumulator) {
        this.tagId = tagId;
        this.logStartTimeSec = logStartTimeSec;
        this.valueUnitRatio = valueUnitRatio;
        this.accumulator = accumulator;
    }

    public String getTagId() {
        return tagId;
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

    public List<Data<Number, Number>> getPercentileData(int percentileOutputTicksPerHalf) {
        return accumulator.getPercentileData(percentileOutputTicksPerHalf,
                HistogramTag::convertPercentileToX,
                this::convertValueUnits);
    }

    void appendHistogram(EncodableHistogram histogram) {
        intervalX.add(histogram.getStartTimeStamp() * 1E-3 - logStartTimeSec);
        intervalY.add(histogram.getMaxValueAsDouble());
        accumulator.addHistogram(histogram);
    }

    final private String tagId;
    final private double logStartTimeSec;
    final double valueUnitRatio;
    final HistogramAccumulator accumulator;

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

}
