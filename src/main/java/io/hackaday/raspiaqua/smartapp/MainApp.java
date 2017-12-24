package io.hackaday.raspiaqua.smartapp;

import com.jfoenix.controls.JFXMasonryPane;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.ResourceBundle;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final ResourceBundle bundle = ResourceBundle.getBundle("bundles.UILocales");

    private static final double TILE_SIZE = 64;
    private final AquaDeviceTile light = new AquaDeviceTile(bundle, TILE_SIZE);
    private final AquaDeviceTile compressor = new AquaDeviceTile(bundle, TILE_SIZE);
    private final AquaDeviceTile heater = new AquaDeviceTile(bundle, TILE_SIZE);
    private final AquaDeviceTile filter = new AquaDeviceTile(bundle, TILE_SIZE);

    @Override
    public void init() {
        light.setName("light");
        light.setIcon(FontAwesomeIcon.LIGHTBULB_ALT);
        light.setColor(Color.ORANGE);

        compressor.setName("compressor");
        compressor.setIcon(FontAwesomeIcon.COGS);
        compressor.setColor(Color.AQUAMARINE);

        heater.setName("heater");
        heater.setIcon(FontAwesomeIcon.PLUG);
        heater.setColor(Color.RED);

        filter.setName("filter");
        filter.setIcon(FontAwesomeIcon.FILTER);
        filter.setColor(Color.CHARTREUSE);
    }

    @Override
    public void start(Stage stage) throws Exception {
        JFXMasonryPane pane = new JFXMasonryPane();
        pane.setPrefSize(240, 360);
        pane.getChildren().add(light.getTile());
        pane.getChildren().add(compressor.getTile());
        pane.getChildren().add(heater.getTile());
        pane.getChildren().add(filter.getTile());

        Scene scene = new Scene(pane);
        stage.setTitle(bundle.getString("key.title"));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
