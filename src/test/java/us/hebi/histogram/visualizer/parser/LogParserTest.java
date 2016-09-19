package us.hebi.histogram.visualizer.parser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 18 Sep 2016
 */
public class LogParserTest {

    ObservableList<XYChart.Data<Number, Number>> data;

    @Before
    public void setUp() throws Exception {
        data = FXCollections.observableArrayList();
    }

    @Test
    public void parseSingleLog_win() throws Exception {
        ParserConfiguration config = ParserConfiguration.builder()
                .inputFile(getTestLog("jHiccup-win.hlog"))
                .outputValueUnitRatio(1E6)
                .percentilesOutputTicksPerHalf(5)
                .build();

        LogParser parser = new LogParser();
        parser.parseLog(config);

        parser.getIntervalData(data);
        assertEquals(253, data.size());
        assertEqualsXY(0.08100, 1.524735, data.get(0));
        assertEqualsXY(23.0980, 1.508351, data.get(23));
        assertEqualsXY(252.098, 1.491967, data.get(data.size() - 1));

        parser.getPercentileData(data);
        assertEquals(91, data.size());
        assertEqualsXY(0, 0, data.get(0));
        assertEqualsXY(1.359022, 0.524799, data.get(23));
        assertEqualsXY(5.418539, 17.055743, data.get(data.size() - 1));

    }

    @Test
    public void parseSingleLog_aggregated_win() throws Exception {
        ParserConfiguration config = ParserConfiguration.builder()
                .inputFile(getTestLog("jHiccup-win.hlog"))
                .outputValueUnitRatio(1E6)
                .percentilesOutputTicksPerHalf(5)
                .aggregateMaximaSamples(7)
                .build();

        LogParser parser = new LogParser();
        parser.parseLog(config);

        parser.getIntervalData(data);
        assertEquals(37, data.size());
        assertEqualsXY(0.08100, 1.639423, data.get(0));
        assertEqualsXY(161.098, 1.508351, data.get(23));
        assertEqualsXY(252.098, 1.491967, data.get(data.size() - 1));

    }

    @Test
    public void parseSingleLog_osx() throws Exception {
        ParserConfiguration config = ParserConfiguration.builder()
                .inputFile(getTestLog("jHiccup-osx.hlog"))
                .outputValueUnitRatio(1E6)
                .percentilesOutputTicksPerHalf(5)
                .build();

        LogParser parser = new LogParser();
        parser.parseLog(config);

        parser.getIntervalData(data);
        assertEquals(187, data.size());
        assertEqualsXY(66.986, 2.656255, data.get(0));
        assertEqualsXY(89.993, 0.459007, data.get(23));
        assertEqualsXY(252.993, 0.245887, data.get(data.size() - 1));

        parser.getPercentileData(data);
        assertEquals(88, data.size());
        assertEqualsXY(0, 0, data.get(0));
        assertEqualsXY(1.3590219426416683, 0.180351, data.get(23));
        assertEqualsXY(5.214419939294157, 3.000319, data.get(data.size() - 1));
    }

    @Test
    public void parseTaggedLog_2tags() throws Exception {
        ParserConfiguration config = ParserConfiguration.builder()
                .inputFile(getTestLog("jHiccup-2tags.hlog"))
                .outputValueUnitRatio(1E6)
                .percentilesOutputTicksPerHalf(5)
                .build();
    }

    private void assertEqualsXY(double expectedX, double expectedY, XYChart.Data<Number, Number> actual) {
        assertEquals("x value", expectedX, actual.getXValue().doubleValue(), 1E-6);
        assertEquals("y value", expectedY, actual.getYValue().doubleValue(), 1E-6);
    }

    private File getTestLog(String name) {
        URL url = LogParserTest.class.getResource(name);
        assertNotNull("Test log file not found", url);
        return new File(url.getPath());
    }

}