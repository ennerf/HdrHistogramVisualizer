package us.hebi.histogram.visualizer.parser;

import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.HdrHistogram.Histogram;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 18 Sep 2016
 */
public class LoadLogTaskTest {

    ObservableList<XYChart.Data<Number, Number>> data;

    @Before
    public void setUp() throws Exception {
        data = FXCollections.observableArrayList();
    }

    @Test
    public void parseSingleLog_win() throws Exception {
        LoaderArgs config = LoaderArgs.builder()
                .setInputFile(getTestLog("jHiccup-win.hlog"))
                .setOutputValueUnitRatio(1E6)
                .setPercentilesOutputTicksPerHalf(5)
                .build();

        LoadLogTask loadLogTask = new LoadLogTask(config);
        List<HistogramAccumulator> accumulators = loadLog(config);
        assertEquals(1, accumulators.size());
        HistogramAccumulator accumulator = accumulators.get(0);

        data.setAll(accumulator.getIntervalData(1));
        checkValues_win_intervals(1);

        data.setAll(accumulator.getIntervalData(7));
        checkValues_win_intervals(7);

        data.setAll(accumulator.getPercentileData(config.percentilesOutputTicksPerHalf()));
        checkValues_win_percentiles();

    }

    private void checkValues_win_intervals(int intervalSamples) {

        switch (intervalSamples) {
            case 7:
                assertEquals(37, data.size());
                assertEqualsXY(0.08100, 1.639423, data.get(0));
                assertEqualsXY(161.098, 1.508351, data.get(23));
                assertEqualsXY(252.098, 1.491967, data.get(data.size() - 1));
                break;

            case 1:
                assertEquals(253, data.size());
                assertEqualsXY(0.08100, 1.524735, data.get(0));
                assertEqualsXY(23.0980, 1.508351, data.get(23));
                assertEqualsXY(252.098, 1.491967, data.get(data.size() - 1));
                break;

            default:
                fail("unknown sample size");
        }

    }

    private void checkValues_win_percentiles() {
        assertEquals(91, data.size());
        assertEqualsXY(0, 0, data.get(0));
        assertEqualsXY(1.359022, 0.524799, data.get(23));
        assertEqualsXY(5.418539, 17.055743, data.get(data.size() - 1));
    }

    @Test
    public void parseSingleLog_osx() throws Exception {
        LoaderArgs config = LoaderArgs.builder()
                .setInputFile(getTestLog("jHiccup-osx.hlog"))
                .setOutputValueUnitRatio(1E6)
                .setPercentilesOutputTicksPerHalf(5)
                .build();

        LoadLogTask loadLogTask = new LoadLogTask(config);
        List<HistogramAccumulator> accumulators = loadLog(config);
        assertEquals(1, accumulators.size());
        HistogramAccumulator accumulator = accumulators.get(0);

        data.setAll(accumulator.getIntervalData(config.aggregateIntervalSamples()));
        checkValues_osx_intervals();

        data.setAll(accumulator.getPercentileData(config.percentilesOutputTicksPerHalf()));
        checkValues_osx_percentiles();
    }

    private void checkValues_osx_intervals() {
        assertEquals(187, data.size());
        assertEqualsXY(66.986, 2.656255, data.get(0));
        assertEqualsXY(89.993, 0.459007, data.get(23));
        assertEqualsXY(252.993, 0.245887, data.get(data.size() - 1));
    }

    private void checkValues_osx_percentiles() {
        assertEquals(88, data.size());
        assertEqualsXY(0, 0, data.get(0));
        assertEqualsXY(1.3590219426416683, 0.180351, data.get(23));
        assertEqualsXY(5.214419939294157, 3.000319, data.get(data.size() - 1));
    }

    @Test
    public void parseTaggedLog_2tags() throws Exception {
        LoaderArgs config = LoaderArgs.builder()
                .setInputFile(getTestLog("jHiccup-2tags.hlog"))
                .setOutputValueUnitRatio(1E6)
                .setPercentilesOutputTicksPerHalf(5)
                .build();

        LoadLogTask loadLogTask = new LoadLogTask(config);
        List<HistogramAccumulator> accumulators = loadLog(config);
        assertEquals(2, accumulators.size());

        HistogramAccumulator osx = accumulators.get(0);
        assertEquals("osx", osx.getTag());
        data.setAll(osx.getIntervalData(config.aggregateIntervalSamples()));
        checkValues_osx_intervals();
        data.setAll(osx.getPercentileData(config.percentilesOutputTicksPerHalf()));
        checkValues_osx_percentiles();

        HistogramAccumulator win = accumulators.get(1);
        assertEquals("win", win.getTag());
        data.setAll(win.getIntervalData(config.aggregateIntervalSamples()));
        checkValues_win_intervals(config.aggregateIntervalSamples());
        data.setAll(win.getPercentileData(config.percentilesOutputTicksPerHalf()));
        checkValues_win_percentiles();

    }

    @Test
    public void parseTaggedLog_selector() throws Exception {

        LoaderArgs config = LoaderArgs.builder()
                .setInputFile(getTestLog("jHiccup-2tags.hlog"))
                .setOutputValueUnitRatio(1E6)
                .setPercentilesOutputTicksPerHalf(5)
                .setSelectedTags("w.*n")
                .build();

        List<HistogramAccumulator> accumulators = loadLog(config);
        assertEquals(1, accumulators.size());
        assertEquals("win", accumulators.get(0).getTag());

    }

    private List<HistogramAccumulator> loadLog(LoaderArgs config) throws IOException {
        LoadLogTask loadLogTask = new LoadLogTask(config);
        return Lists.newArrayList(loadLogTask.call());
    }

    private void assertEqualsXY(double expectedX, double expectedY, XYChart.Data<Number, Number> actual) {
        assertEquals("x value", expectedX, actual.getXValue().doubleValue(), 1E-6);
        assertEquals("y value", expectedY, actual.getYValue().doubleValue(), 1E-6);
    }

    private File getTestLog(String name) {
        URL url = LoadLogTaskTest.class.getResource(name);
        assertNotNull("Test log file not found", url);
        return new File(url.getPath());
    }

}