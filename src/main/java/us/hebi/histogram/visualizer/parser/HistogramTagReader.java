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
 * Task that can read multiple tags from a single Hdr log. If the
 * log does not contain any tags, the whole log returns as one
 * tag with an empty tagId.
 *
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 18 Sep 2016
 */
public class HistogramTagReader extends Task<Iterable<HistogramTag>> {

    public HistogramTagReader(LoaderArgs configuration) {
        this.config = checkNotNull(configuration);
    }

    @Override
    protected Iterable<HistogramTag> call() throws IOException {

        final TreeMap<String, HistogramTag> tags = new TreeMap<>(String::compareToIgnoreCase);
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
                final String tagId = Strings.isNullOrEmpty(interval.getTag()) ? "" : interval.getTag();
                if (!tags.containsKey(tagId)) {

                    // Ignore non-selected tags
                    if (!tagPattern.matcher(tagId).matches())
                        continue;

                    // Initialize empty
                    HistogramTag accumulator = HistogramTag.createEmptyForType(
                            tagId,
                            logReader.getStartTimeSec(),
                            config.outputValueUnitRatio(),
                            interval);
                    tags.put(tagId, accumulator);

                }

                // Add intervals
                HistogramTag accumulator = tags.get(tagId);
                accumulator.appendHistogram(interval);

            }

        }

    }

    final LoaderArgs config;

}
