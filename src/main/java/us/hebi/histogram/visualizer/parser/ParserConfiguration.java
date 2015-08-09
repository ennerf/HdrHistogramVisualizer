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

        // default values
        private double startTimeSec = 0;
        private double endTimeSec = Double.MAX_VALUE;
        private double outputValueUnitRatio = 1E6;
        private int percentilesOutputTicksPerHalf = 5;
        private boolean logFormatCsv = false;

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
