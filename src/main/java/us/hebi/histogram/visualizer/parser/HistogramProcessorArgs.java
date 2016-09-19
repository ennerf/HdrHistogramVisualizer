package us.hebi.histogram.visualizer.parser;

import com.google.auto.value.AutoValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.google.common.base.Preconditions.*;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 19 Sep 2016
 */
@AutoValue
public abstract class HistogramProcessorArgs {

    public static Builder builder() {
        return new AutoValue_HistogramProcessorArgs.Builder()
                .setStartTimeSec(0)
                .setEndTimeSec(Double.MAX_VALUE)
                .setOutputValueUnitRatio(1E6)
                .setPercentilesOutputTicksPerHalf(5)
                .setAggregateIntervalSamples(1)
                .setLogFormatCsv(false)
                .setSelectedTags(".*");
    }

    private static void checkArguments(HistogramProcessorArgs obj) {
        checkArgument(obj.inputFile() != null && obj.inputFile().isFile(), "Invalid input file");
        checkArgument(obj.startTimeSec() >= 0, "start time can't be negative");
        checkArgument(obj.endTimeSec() >= 0, "end time can't be negative");
        checkArgument(obj.aggregateIntervalSamples() >= 1, "can't aggregate less than 1 interval");
        try {
            Pattern.compile(obj.selectedTags());
        } catch (PatternSyntaxException ex) {
            throw new IllegalArgumentException("'" + obj.selectedTags() + "' is not a valid regex pattern");
        }
    }

    public String[] toCommandlineArgs(File outputFile) {
        List<String> args = new ArrayList<>(16);
        args.add("-i");
        args.add(inputFile().getPath());
        args.add("-o");
        args.add(outputFile.getPath());
        args.add("-start");
        args.add(String.valueOf(startTimeSec()));
        args.add("-end");
        args.add(String.valueOf(endTimeSec()));
        args.add("-percentilesOutputTicksPerHalf");
        args.add(String.valueOf(percentilesOutputTicksPerHalf()));
        args.add("-outputValueUnitRatio");
        args.add(String.valueOf(outputValueUnitRatio()));
        if (logFormatCsv()) args.add("-csv");
        return args.toArray(new String[args.size()]);
    }

    public abstract File inputFile();

    public abstract double startTimeSec();

    public abstract double endTimeSec();

    public abstract double outputValueUnitRatio();

    public abstract int percentilesOutputTicksPerHalf();

    public abstract int aggregateIntervalSamples();

    public abstract boolean logFormatCsv();

    public abstract String selectedTags();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder setInputFile(File value);

        public abstract Builder setStartTimeSec(double value);

        public abstract Builder setEndTimeSec(double value);

        public abstract Builder setOutputValueUnitRatio(double value);

        public abstract Builder setPercentilesOutputTicksPerHalf(int value);

        public abstract Builder setAggregateIntervalSamples(int value);

        public abstract Builder setLogFormatCsv(boolean value);

        public abstract Builder setSelectedTags(String value);

        public abstract HistogramProcessorArgs autoBuild();

        public HistogramProcessorArgs build() {
            HistogramProcessorArgs args = autoBuild();
            checkArguments(args);
            return args;
        }

    }

}
