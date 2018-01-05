package io.hackaday.raspiaqua.smartapp;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXToolbar;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.hackaday.raspiaqua.proto.Aquarium.MessagePacket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    JFXButton button;
    JFXDialog dialog;

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

        button = new JFXButton("", new FontAwesomeIconView(FontAwesomeIcon.POWER_OFF));
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setBackground(new Background(new BackgroundFill(Color.CORAL, new CornerRadii(64), Insets.EMPTY)));
        button.setRipplerFill(Color.ANTIQUEWHITE);
        button.setOnAction((PushEvent) -> {
            sendRequest("localhost", 8997);
        });

        StackPane stackPane = new StackPane(pane, button);
        stackPane.alignmentProperty().setValue(Pos.BOTTOM_RIGHT);
        StackPane.setMargin(button, new Insets(0.0, 20.0, 20.0, 0.0));
        dialog = new JFXDialog(stackPane, new JFXSpinner(), JFXDialog.DialogTransition.TOP, true);

        BorderPane borderPane = new BorderPane(stackPane);

        JFXToolbar toolbar = new JFXToolbar();
        toolbar.setBackground(new Background(new BackgroundFill(Color.CORNFLOWERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        toolbar.setPrefHeight(32);
        JFXHamburger hamburger = new JFXHamburger();
        toolbar.setLeftItems(hamburger);
        Text text = new Text(bundle.getString("key.title"));
        toolbar.setCenter(text);
        borderPane.setTop(toolbar);

        Scene scene = new Scene(borderPane);
        stage.setTitle(bundle.getString("key.title"));
        stage.setScene(scene);
        stage.show();
        
        sendRequest("localhost", 8997);
        
    }

    void sendRequest(String host, int port) {
        try {
            button.setVisible(false);
            dialog.show();
            pbSocket = new Socket(host, port);
            pbSocket.setSoTimeout(10000);
            messageRequest = MessagePacket.newBuilder()
                    .setServerName(host)
                    .setClientName(InetAddress.getLocalHost().getHostName())
                    .build();
            logger.info("Size: {} Request:\n{}", messageRequest.getSerializedSize(), messageRequest.toString());
            try {
                CodedOutputStream pbOutputStream = CodedOutputStream.newInstance(pbSocket.getOutputStream());
                pbOutputStream.writeMessageNoTag(messageRequest);
                pbOutputStream.flush();
                CodedInputStream pbInputStream = CodedInputStream.newInstance(pbSocket.getInputStream());
                messageResponse = MessagePacket.parseFrom(pbInputStream.readBytes().toByteArray());
                logger.info("Size: {} Response:\n{}", messageResponse.getSerializedSize(), messageResponse.toString());
            } finally {
                button.setVisible(true);
                dialog.close();
                pbSocket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
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
