package us.hebi.histogram.visualizer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import us.hebi.histogram.visualizer.gui.VisualizerView;

/**
 * @author Florian Enner < florian @ hebirobotics.com >
 * @since 04 Jul 2015
 */
public class Visualizer extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        VisualizerView root = new VisualizerView();
        Scene scene = new Scene(root.getView());
        stage.setMinWidth(500);
        stage.setMinHeight(550);
        stage.setTitle("HdrHistogram Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
