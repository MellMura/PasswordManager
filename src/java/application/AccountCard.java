package application;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.io.File;

public class AccountCard {
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private ImageView iconImage;
    @FXML
    private AnchorPane rootPane;


    public void setData(String name, String email, String password, String iconUrl, String colorHex) {
        nameLabel.setText(name);
        emailLabel.setText("E-mail: " + email);
        passwordLabel.setText("Password: " + password);

        if (iconUrl != null && !iconUrl.isEmpty()) {
            File iconFile = new File(iconUrl);
            if (iconFile.exists()) {
                Image image = new Image(iconFile.toURI().toString());
                iconImage.setImage(image);
            }
        }

        try {
            Color color = Color.web(colorHex);
            rootPane.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid color: " + colorHex);
        }
    }
}
