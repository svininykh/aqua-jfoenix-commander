package io.hackaday.raspiaqua.smartapp;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXTogglePane;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author svininykh-av
 */
public class AquaDeviceTile {
    
    private final ResourceBundle bundle;
    private final double size;
    
    private String name = "unknown";
    private FontAwesomeIcon icon = FontAwesomeIcon.POWER_OFF;
    private Color color = Color.BLUEVIOLET;
    
    private FlowPane tile = new FlowPane();
    
    public AquaDeviceTile(ResourceBundle bundle, double size) {
        this.bundle = bundle;
        this.size = size;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public FontAwesomeIcon getIcon() {
        return icon;
    }
    
    public void setIcon(FontAwesomeIcon icon) {
        this.icon = icon;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public FlowPane getTile() {
        FontAwesomeIconView iconView = new FontAwesomeIconView();
        iconView.setIcon(icon);
        iconView.setSize("32");
        StackPane stack = new StackPane(iconView);
        stack.setPadding(new Insets(4));
        stack.setPrefSize(size, size);
        JFXBadge badge = new JFXBadge(stack);
        badge.setText(bundle.getString("key.off"));
        badge.setBorder(Border.EMPTY);
        badge.setPadding(new Insets(2));
        JFXToggleButton button = new JFXToggleButton();
        button.toggleColorProperty().set(color);
        button.setOnAction((SwitchEvent) -> {
            if (button.isSelected()) {
                badge.setText(bundle.getString("key.on"));
                badge.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                badge.setText(bundle.getString("key.off"));
                badge.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });
        JFXTogglePane toggle = new JFXTogglePane();
        toggle.setPrefSize(size, size);
        toggle.getChildren().add(button);
        
        Text text = new Text(bundle.getString("key.".concat(name)));
        text.setFont(new Font(10));
        
        tile = new FlowPane(badge, toggle, text);
        tile.setAlignment(Pos.CENTER);
        tile.setPrefSize(size, size * 2);
        
        return tile;
    }
    
    public void setTile(String name, FontAwesomeIcon icon, Color color) {
        this.name = name;
        this.icon = icon;
        this.color = color;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
