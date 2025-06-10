package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginLayout {
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink switchToRegLink;

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            boolean success = Login.loginUser(email, password);

            if (success) {
                System.out.println("Login successful!");
                // TODO: Load the main application window
            } else {
                System.out.println("Invalid email or password.");
            }
        });
    }

    @FXML
    private void switchToReg(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/layouts/regLayout.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) switchToRegLink.getScene().getWindow();
            stage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
