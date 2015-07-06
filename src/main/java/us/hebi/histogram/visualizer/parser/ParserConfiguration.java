package us.hebi.histogram.visualizer.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.*;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 04 Jul 2015
 */
public class ParserConfiguration {

    final File inputFile;
    final double startTimeSec;
    final double endTimeSec;
    final double outputValueUnitRatio;
    final int percentilesOutputTicksPerHalf;
    final boolean logFormatCsv;

    ParserConfiguration(File inputFile, double startTimeSec, double endTimeSec, double outputValueUnitRatio, int percentilesOutputTicksPerHalf, boolean logFormatCsv) {
        this.inputFile = checkNotNull(inputFile);
        this.startTimeSec = startTimeSec;
        this.endTimeSec = endTimeSec;
        this.outputValueUnitRatio = outputValueUnitRatio;
        this.percentilesOutputTicksPerHalf = percentilesOutputTicksPerHalf;
        this.logFormatCsv = logFormatCsv;
    }

    public static ParserConfigurationBuilder builder() {
        return new ParserConfigurationBuilder();
    }

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
    public static final boolean DEFAULT_CSV_FORMAT = false;

    public File getInputFile() {
        return this.inputFile;
    }

    public double getStartTimeSec() {
        return this.startTimeSec;
    }

    public double getEndTimeSec() {
        return this.endTimeSec;
    }

    public double getOutputValueUnitRatio() {
        return this.outputValueUnitRatio;
    }

    public int getPercentilesOutputTicksPerHalf() {
        return this.percentilesOutputTicksPerHalf;
    }

    public boolean isLogFormatCsv() {
        return this.logFormatCsv;
    }

    public static class ParserConfigurationBuilder {
        private File inputFile;
        private double startTimeSec = DEFAULT_START_TIME;
        private double endTimeSec = DEFAULT_END_TIME;
        private double outputValueUnitRatio = DEFAULT_OUTPUT_UNIT_RATIO;
        private int percentilesOutputTicksPerHalf = DEFAULT_PERCENTILES_TICKS_PER_HALF;
        private boolean logFormatCsv = DEFAULT_CSV_FORMAT;

        ParserConfigurationBuilder() {
        }

        public ParserConfiguration.ParserConfigurationBuilder inputFile(File inputFile) {
            this.inputFile = inputFile;
            return this;
        }

        public ParserConfiguration.ParserConfigurationBuilder startTimeSec(double startTimeSec) {
            this.startTimeSec = startTimeSec;
            return this;
        }

        public ParserConfiguration.ParserConfigurationBuilder endTimeSec(double endTimeSec) {
            this.endTimeSec = endTimeSec;
            return this;
        }

        public ParserConfiguration.ParserConfigurationBuilder outputValueUnitRatio(double outputValueUnitRatio) {
            this.outputValueUnitRatio = outputValueUnitRatio;
            return this;
        }

        public ParserConfiguration.ParserConfigurationBuilder percentilesOutputTicksPerHalf(int percentilesOutputTicksPerHalf) {
            this.percentilesOutputTicksPerHalf = percentilesOutputTicksPerHalf;
            return this;
        }

        public ParserConfiguration.ParserConfigurationBuilder logFormatCsv(boolean logFormatCsv) {
            this.logFormatCsv = logFormatCsv;
            return this;
        }

        public ParserConfiguration build() {
            return new ParserConfiguration(inputFile, startTimeSec, endTimeSec, outputValueUnitRatio, percentilesOutputTicksPerHalf, logFormatCsv);
        }

        public String toString() {
            return "ParserConfigurationBuilder(inputFile=" + this.inputFile + ", startTimeSec=" + this.startTimeSec + ", endTimeSec=" + this.endTimeSec + ", outputValueUnitRatio=" + this.outputValueUnitRatio + ", percentilesOutputTicksPerHalf=" + this.percentilesOutputTicksPerHalf + ", logFormatCsv=" + this.logFormatCsv + ")";
        }
    }
}
