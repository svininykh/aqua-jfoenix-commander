package io.hackaday.raspiaqua.smartapp;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.jfoenix.controls.JFXMasonryPane;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.hackaday.raspiaqua.proto.Aquarium.MessagePacket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {

    private final ResourceBundle bundle = ResourceBundle.getBundle("bundles.UILocales");

    private static final double TILE_SIZE = 64;
    private final AquaDeviceTile light = new AquaDeviceTile(bundle, TILE_SIZE);
    private final AquaDeviceTile compressor = new AquaDeviceTile(bundle, TILE_SIZE);
    private final AquaDeviceTile heater = new AquaDeviceTile(bundle, TILE_SIZE);
    private final AquaDeviceTile filter = new AquaDeviceTile(bundle, TILE_SIZE);

    Socket pbSocket = null;

    org.slf4j.Logger logger = LoggerFactory.getLogger(MainApp.class);

    MessagePacket messageResponse;
    MessagePacket messageRequest;

    @Override
    public void init() {
        try {
            pbSocket = new Socket("localhost", 8997);
            pbSocket.setSoTimeout(1000);
            messageRequest = MessagePacket.newBuilder()
                    .setServerName("localhost")
                    .setClientName(InetAddress.getLocalHost().getHostName())
                    .build();
            logger.debug("Size: {} Request:\n{}", messageRequest.getSerializedSize(), messageRequest.toString());
            try {
                CodedOutputStream pbOutputStream = CodedOutputStream.newInstance(pbSocket.getOutputStream());
                pbOutputStream.writeMessageNoTag(messageRequest);
                pbOutputStream.flush();
                CodedInputStream pbInputStream = CodedInputStream.newInstance(pbSocket.getInputStream());
                messageResponse = MessagePacket.parseFrom(pbInputStream.readBytes().toByteArray());
                logger.debug("Size: {} Response:\n{}", messageResponse.getSerializedSize(), messageResponse.toString());
            } finally {
                pbSocket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }

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
