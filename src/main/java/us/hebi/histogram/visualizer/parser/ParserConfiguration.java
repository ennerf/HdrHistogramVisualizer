package us.hebi.histogram.visualizer.parser;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 04 Jul 2015
 */
@Builder
@Getter
public class ParserConfiguration {

    @NonNull
    final File inputFile;
    final double startTimeSec;
    final double endTimeSec;
    final double outputValueUnitRatio;
    final int percentilesOutputTicksPerHalf;
    final boolean logFormatCsv;

    /**
     * @return arguments as expected by HistogramLogProcessor.main()
     */
    public String[] toArgsArray(File outputFile) {

        List<String> args = new ArrayList<>(16);
        args.add("-i");
        args.add(inputFile.getPath());
        args.add("-o");
        args.add(outputFile.getPath());
        args.add("-start");
        args.add(String.valueOf(startTimeSec));
        args.add("-end");
        args.add(String.valueOf(endTimeSec));
        args.add("-percentilesOutputTicksPerHalf");
        args.add(String.valueOf(percentilesOutputTicksPerHalf));
        args.add("-outputValueUnitRatio");
        args.add(String.valueOf(outputValueUnitRatio));
        if (logFormatCsv) args.add("-csv");
        return args.toArray(new String[args.size()]);

    }

    public static final double DEFAULT_START_TIME = 0;
    public static final double DEFAULT_END_TIME = Double.MAX_VALUE;
    public static final double DEFAULT_OUTPUT_UNIT_RATIO = 1E6;
    public static final int DEFAULT_PERCENTILES_TICKS_PER_HALF = 5;

}
