package us.hebi.histogram.visualizer.parser;

import com.google.common.base.Strings;
import javafx.concurrent.Task;
import org.HdrHistogram.EncodableHistogram;
import org.HdrHistogram.HistogramLogReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.*;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 18 Sep 2016
 */
public class LoadLogTask extends Task<Iterable<HistogramAccumulator>> {

    public LoadLogTask(LoaderArgs configuration) {
        this.config = checkNotNull(configuration);
    }

    @Override
    protected Iterable<HistogramAccumulator> call() throws IOException {

        final TreeMap<String, HistogramAccumulator> tags = new TreeMap<>(String::compareToIgnoreCase);
        final Pattern tagPattern = Pattern.compile(config.selectedTags());

        try (InputStream inputStream = new FileInputStream(config.inputFile())) {
            HistogramLogReader logReader = new HistogramLogReader(inputStream);

            while (true) {

                if (isCancelled()) {
                    return Collections.emptyList();
                }

                // Advance in log file
                final EncodableHistogram interval =
                        logReader.nextIntervalHistogram(config.startTimeSec(), config.endTimeSec());

                // Stop when there are no more intervals
                if (interval == null) {
                    return tags.values();
                }

                // Make sure we have 1 accumulator per tag.
                final String tag = Strings.isNullOrEmpty(interval.getTag()) ? "" : interval.getTag();
                if (!tags.containsKey(tag)) {

                    // Ignore non-selected tags
                    if (!tagPattern.matcher(tag).matches())
                        continue;

                    // Initialize empty
                    HistogramAccumulator accumulator = HistogramAccumulator.createEmptyForType(
                            tag,
                            logReader.getStartTimeSec(),
                            config.outputValueUnitRatio(),
                            interval);
                    tags.put(tag, accumulator);

                }

                // Add intervals
                HistogramAccumulator accumulator = tags.get(tag);
                accumulator.appendHistogram(interval);

            }

        }

    }

    final LoaderArgs config;

}
