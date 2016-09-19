package us.hebi.histogram.visualizer.parser;

import com.google.common.base.Strings;
import javafx.concurrent.Task;
import org.HdrHistogram.EncodableHistogram;
import org.HdrHistogram.HistogramLogReader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.*;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 18 Sep 2016
 */
public class LoadLogTask extends Task<Iterable<HistogramAccumulator>> {

    public LoadLogTask(ParserConfiguration configuration) {
        this.config = checkNotNull(configuration);
    }

    @Override
    protected Collection<HistogramAccumulator> call() throws Exception {

        final TreeMap<String, HistogramAccumulator> tags = new TreeMap<>(String::compareToIgnoreCase);
        final Pattern tagPattern = Pattern.compile(config.getSelectedTags());

        try (InputStream inputStream = new FileInputStream(config.getInputFile())) {
            HistogramLogReader logReader = new HistogramLogReader(inputStream);

            while (true) {

                if (isCancelled()) {
                    return Collections.emptyList();
                }

                // Advance in log file
                final EncodableHistogram interval =
                        logReader.nextIntervalHistogram(config.getStartTimeSec(), config.getEndTimeSec());

                // Stop when there are no more intervals
                if (interval == null) {
                    return tags.values();
                }

                // Make sure we have 1 accumulator per tag.
                final String tag = Strings.isNullOrEmpty(interval.getTag()) ? "" : interval.getTag();
                if (!tags.containsKey(tag)) {
                    if (tagPattern.matcher(tag).matches()) {

                        // Interval gets added on initialization, so no need to add again
                        tags.put(tag, HistogramAccumulator.createInitialized(
                                tag,
                                logReader.getStartTimeSec(),
                                config.getOutputValueUnitRatio(),
                                interval));
                    }

                } else {

                    // Add subsequent intervals
                    HistogramAccumulator accumulator = tags.get(tag);
                    accumulator.appendHistogram(interval);

                }

            }

        }

    }

    final ParserConfiguration config;

}
