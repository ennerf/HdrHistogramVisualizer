package us.hebi.histogram.visualizer;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import us.hebi.histogram.visualizer.gui.VisualizerView;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

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

        // Auto load css file
        registerCssChangeListener(root.getView().getStylesheets(), Paths.get("visualizer.css"));

    }

    private void registerCssChangeListener(final ObservableList<String> stylesheets, Path cssPath) throws IOException {

        // Auto-load configuration CSS and property file
        final Path absCssPath = cssPath.toAbsolutePath();
        final String cssUri = absCssPath.toUri().toString();
        final String fileName = cssPath.getFileName().toString();

        if (Files.exists(absCssPath)) {
            stylesheets.add(cssUri);
        }

        // Listen to directory change events
        final WatchService watchService = FileSystems.getDefault().newWatchService();
        final Path directory = absCssPath.getParent();
        directory.register(watchService, ENTRY_MODIFY, ENTRY_DELETE);

        // Poll in a new thread
        Runnable poller = () -> {
            try {
                while (true) {

                    // Poll all registered events
                    WatchKey key = watchService.take();

                    // Re-load CSS file on modification
                    key.pollEvents().stream()
                            .filter(e -> e.context().toString().equals(fileName))
                            .reduce((prior, later) -> later) // select last event
                            .ifPresent(e -> {

                                // Always remove
                                Platform.runLater(() -> stylesheets.remove(cssUri));

                                // Re-load on non delete
                                if (e.kind() != ENTRY_DELETE) {
                                    Platform.runLater(() -> stylesheets.add(cssUri));
                                }
                            });

                    // Re-queue on watch service
                    key.reset();
                }

            } catch (InterruptedException e) {
                // Ignore interrupts and poll again
            }

        };

        // Make sure watch service gets closed
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try (WatchService resource = watchService) {
                } catch (IOException e) {
                }
            }
        });

        // Start polling thread
        Thread pollThread = new Thread(poller);
        pollThread.setDaemon(true);
        pollThread.start();

    }

    @Override
    public void stop() {
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
