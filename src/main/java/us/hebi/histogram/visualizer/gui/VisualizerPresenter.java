package us.hebi.histogram.visualizer.gui;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.MoreExecutors;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.HdrHistogram.HistogramLogProcessor;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import us.hebi.histogram.visualizer.parser.HistogramTag;
import us.hebi.histogram.visualizer.parser.HistogramTagReader;
import us.hebi.histogram.visualizer.parser.HistogramProcessorArgs;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.google.common.base.Preconditions.*;
import static java.lang.Double.*;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 04 Jul 2015
 */
public class VisualizerPresenter {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button inputSelectButton;

    @FXML
    private TextField outputFileName;

    @FXML
    private TextField seriesLabel;

    @FXML
    private Button saveImageButton;

    @FXML
    private Button exportButton;

    @FXML
    private Button loadButton;

    @FXML
    private TextField inputFileName;

    @FXML
    private TextField percentilesOutputTicksPerHalf;

    @FXML
    private TextField aggregateIntervalSamples;

    @FXML
    private TextField outputValueUnitRatio;

    @FXML
    private CheckBox csvFormatCheckbox;

    @FXML
    private CheckBox clearChartCheckbox;

    @FXML
    private TextField rangeEndTimeSec;

    @FXML
    private TextField rangeStartTimeSec;

    @FXML
    private TextField tagSelector;

    @FXML
    private LineChart<Number, Number> percentileChart;

    @FXML
    private LineChart<Number, Number> intervalChart;

    @FXML
    private ChoiceBox<IntervalTickFormatter> intervalXTickLabel;

    @FXML
    private SplitPane chartPane;

    @FXML
    private TextField percentileChartTitle;

    @FXML
    private TextField percentileChartX;

    @FXML
    private TextField percentileChartY;

    @FXML
    private TextField intervalChartTitle;

    @FXML
    private TextField intervalChartX;

    @FXML
    private TextField intervalChartY;

    @FXML
    private Accordion menuAccordion;

    @FXML
    private TitledPane dataSelectionPane;

    @FXML
    void selectInputFile(ActionEvent event) {
        File selectedFile = inputFileChooser.showOpenDialog(inputSelectButton.getScene().getWindow());
        if (selectedFile == null)
            return;
        inputFileName.setText(selectedFile.getPath());
        inputFileChooser.setInitialDirectory(selectedFile.getParentFile());
    }

    @FXML
    void saveImage(ActionEvent event) {
        // Suggest name based on input file
        if (!inputFileName.getText().isEmpty()) {
            String suggestedName = Files.getNameWithoutExtension(inputFileName.getText());
            outputFileChooser.setInitialFileName(suggestedName);
        }

        // Show dialog
        File outputFile = imageOutputFileChooser.showSaveDialog(saveImageButton.getScene().getWindow());
        if (outputFile == null)
            return;
        imageOutputFileChooser.setInitialDirectory(outputFile.getParentFile());

        // Create image from current chart content
        WritableImage image = new WritableImage((int) chartPane.getWidth(), (int) chartPane.getHeight());
        chartPane.snapshot(null, image);

        // Write to disk
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
        } catch (Exception e) {
            Alert dialog = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
            dialog.showAndWait();
        }
    }

    @FXML
    void loadLog(ActionEvent event) {

        if (clearChartCheckbox.isSelected()) {
            intervalChart.getData().clear();
            percentileChart.getData().clear();
        }

        try {

            HistogramProcessorArgs config = getCurrentConfiguration();
            HistogramTagReader reader = new HistogramTagReader(config);

            String logLabel = !seriesLabel.getText().isEmpty() ? seriesLabel.getText() : config.inputFile().getName();

            // TODO: run this properly in the background to avoid halting the GUI
            MoreExecutors.directExecutor().execute(reader);
            for (HistogramTag tag : reader.get()) {

                String seriesLabel = tag.getTagId().isEmpty() ? logLabel : logLabel + "-" + tag.getTagId();

                XYChart.Series<Number, Number> intervalSeries = new XYChart.Series<Number, Number>();
                intervalSeries.setName(seriesLabel);
                intervalSeries.getData().setAll(tag.getIntervalData(config.aggregateIntervalSamples()));

                XYChart.Series<Number, Number> percentileSeries = new XYChart.Series<Number, Number>();
                percentileSeries.setName(seriesLabel);
                percentileSeries.getData().setAll(tag.getPercentileData(config.percentilesOutputTicksPerHalf()));

                intervalChart.getData().add(intervalSeries);
                percentileChart.getData().add(percentileSeries);

            }

        } catch (Exception e) {
            Alert dialog = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
            dialog.showAndWait();
        }
    }

    @FXML
    void exportLog(ActionEvent event) {

        // Let user select file (suggest name based on input file without extension)
        checkState(!inputFileName.getText().isEmpty(), "Input file must not be empty");
        String suggestedName = Files.getNameWithoutExtension(inputFileName.getText());
        outputFileChooser.setInitialFileName(suggestedName);
        outputFileChooser.setInitialDirectory(new File(inputFileName.getText()).getAbsoluteFile().getParentFile());

        // Show dialog
        File outputFile = outputFileChooser.showSaveDialog(exportButton.getScene().getWindow());
        if (outputFile == null)
            return;

        // Call HistogramLogProcessor
        try {
            String[] args = getCurrentConfiguration().toCommandlineArgs(outputFile);
            HistogramLogProcessor.main(args);
        } catch (Exception e) {
            Alert dialog = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
            dialog.showAndWait();
        }
    }

    private HistogramProcessorArgs getCurrentConfiguration() {
        // This method assumes that the input file name is guaranteed to be set, and that
        // all inputs are properly formatted. If preconditions are not met, the button should
        // be disabled.
        checkState(!inputFileName.getText().isEmpty(), "Input file must not be empty");
        HistogramProcessorArgs.Builder config = HistogramProcessorArgs.builder();

        // Required Parameters
        config.setInputFile(new File(inputFileName.getText()));

        // Optional Parameters
        if (!outputValueUnitRatio.getText().isEmpty())
            config.setOutputValueUnitRatio(parseDouble(outputValueUnitRatio.getText()));

        if (!rangeStartTimeSec.getText().isEmpty())
            config.setStartTimeSec(parseDouble(rangeStartTimeSec.getText()));

        if (!rangeEndTimeSec.getText().isEmpty())
            config.setEndTimeSec(parseDouble(rangeEndTimeSec.getText()));

        if (!percentilesOutputTicksPerHalf.getText().isEmpty())
            config.setPercentilesOutputTicksPerHalf(Integer.parseInt(percentilesOutputTicksPerHalf.getText()));

        if (!aggregateIntervalSamples.getText().isEmpty())
            config.setAggregateIntervalSamples(Integer.parseInt(aggregateIntervalSamples.getText()));

        if (!tagSelector.getText().isEmpty()) {
            config.setSelectedTags(tagSelector.getText());
        }

        config.setLogFormatCsv(csvFormatCheckbox.isSelected());

        // Combine
        return config.build();
    }

    @FXML
    void initialize() {

        // Add input validation
        Validator<String> optionalDoubleValidator = (c, str) -> ValidationResult.fromErrorIf(c, "Expected double",
                str != null && !str.isEmpty() && Doubles.tryParse(str) == null);
        Validator<String> optionalIntValidator = (c, str) -> ValidationResult.fromErrorIf(c, "Expected integer",
                str != null && !str.isEmpty() && Ints.tryParse(str) == null);
        Validator<String> requiredFileValidator = (c, str) -> ValidationResult.fromErrorIf(c, "Expected file",
                str == null || str.isEmpty()); // TODO: check whether file is well formed and exists?
        Validator<String> optionalRegexValidator = (c, str) -> {
            boolean valid = Strings.isNullOrEmpty(str);
            if (!valid) {
                try {
                    Pattern.compile(str);
                    valid = true;
                } catch (PatternSyntaxException pse) {
                    valid = false;
                }
            }
            return ValidationResult.fromErrorIf(c, "Expected regex", !valid);
        };

        validationSupport.registerValidator(inputFileName, false, requiredFileValidator); // required = true renders odd
        validationSupport.registerValidator(rangeStartTimeSec, false, optionalDoubleValidator);
        validationSupport.registerValidator(rangeEndTimeSec, false, optionalDoubleValidator);
        validationSupport.registerValidator(outputValueUnitRatio, false, optionalDoubleValidator);
        validationSupport.registerValidator(percentilesOutputTicksPerHalf, false, optionalIntValidator);
        validationSupport.registerValidator(aggregateIntervalSamples, false, optionalIntValidator);
        validationSupport.registerValidator(tagSelector, false, optionalRegexValidator);

        // Block buttons if inputs are incorrect
        loadButton.disableProperty().bind(validationSupport.invalidProperty());
        exportButton.disableProperty().bind(validationSupport.invalidProperty());

        // Block save image button if there is no displayed data
        saveImageButton.setDisable(true);
        percentileChart.getData().addListener((Observable observable) -> {
            saveImageButton.setDisable(percentileChart.getData().isEmpty());
        });

        // Initialize charts
        bindChartAxisLabels();
        initializeIntervalChartAxes();
        initializePercentileChartAxes();

        // Show main pane on startup
        menuAccordion.setExpandedPane(dataSelectionPane);

    }

    void bindChartAxisLabels() {
        bindChartAxisLabelWithDefault(intervalChart.titleProperty(), intervalChartTitle.textProperty());
        bindChartAxisLabelWithDefault(intervalChart.getXAxis().labelProperty(), intervalChartX.textProperty());
        bindChartAxisLabelWithDefault(intervalChart.getYAxis().labelProperty(), intervalChartY.textProperty());
        bindChartAxisLabelWithDefault(percentileChart.titleProperty(), percentileChartTitle.textProperty());
        bindChartAxisLabelWithDefault(percentileChart.getXAxis().labelProperty(), percentileChartX.textProperty());
        bindChartAxisLabelWithDefault(percentileChart.getYAxis().labelProperty(), percentileChartY.textProperty());
    }

    void bindChartAxisLabelWithDefault(final Property<String> property, final StringProperty textField) {
        // Default to the text that has been loaded from resources. Alternatively we could use
        // resources.getString("<name>"), but that would unnecessarily duplicate the resource strings.
        final String defaultText = property.getValue();
        checkState(defaultText != null && !defaultText.isEmpty(), "Expected non-empty default");
        property.bind(Bindings.createStringBinding(
                () -> !textField.get().isEmpty() ? textField.get() : defaultText,
                textField
        ));
    }

    void initializeIntervalChartAxes() {
        final NumberAxis xAxis = (NumberAxis) intervalChart.getXAxis();
        xAxis.setForceZeroInRange(false);

        // Bind X Tick label formatter to choice-box
        intervalXTickLabel.getItems().addAll(IntervalTickFormatter.values());
        intervalXTickLabel.getSelectionModel().select(0);
        ObjectBinding<StringConverter<Number>> intervalXLabelConverter = Bindings.createObjectBinding(
                () -> intervalXTickLabel.getSelectionModel().getSelectedItem().getConverter(),
                intervalXTickLabel.getSelectionModel().selectedItemProperty()
        );
        xAxis.tickLabelFormatterProperty().bind(intervalXLabelConverter);

    }

    void initializePercentileChartAxes() {
        checkNotNull(percentileChart);
        final NumberAxis xAxis = (NumberAxis) percentileChart.getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setTickUnit(1); // log axis -> 10^x steps
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(0);

        // Limit X range to max value to avoid empty space
        percentileChart.getData().addListener((ListChangeListener<XYChart.Series<Number, Number>>) c -> {
            double maxX = percentileChart.getData().stream()
                    .flatMap(series -> series.getData().stream())
                    .mapToDouble(point -> point.getXValue().doubleValue())
                    .max()
                    .orElse(0);
            xAxis.setUpperBound(maxX);
        });

        // Format labels such that e.g. 10^6 is shown as 6 nines
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                int intValue = object.intValue();
                if (object.doubleValue() > intValue)
                    return ""; // Only label full 10^x steps
                switch (intValue) {
                    case 0:
                        return "0%";
                    case 1:
                        return "90%";
                    case 2:
                        return "99%";
                    default:
                        String percentile = "99.";
                        for (int i = 2; i < intValue; i++) {
                            percentile += "9";
                        }
                        return percentile + "%";
                }

            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
    }

    @PostConstruct
    public void setup() {
        inputFileChooser = new FileChooser();
        inputFileChooser.setTitle("Select Input Log");
        inputFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Histogram Log", "*.hlog"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        inputFileChooser.setInitialDirectory(new File("."));

        outputFileChooser = new FileChooser();
        outputFileChooser.setTitle("Select Output File");
        outputFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        outputFileChooser.setInitialDirectory(new File("."));

        imageOutputFileChooser = new FileChooser();
        imageOutputFileChooser.setTitle("Select Output File");
        imageOutputFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("png", "*.png"));
        imageOutputFileChooser.setInitialDirectory(new File("."));
    }

    FileChooser inputFileChooser;
    FileChooser outputFileChooser;
    FileChooser imageOutputFileChooser;

    final ValidationSupport validationSupport = new ValidationSupport();

}