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
import java.nio.file.Files;

public class LoginLayout {
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink switchToRegLink;

    @FXML
    public void initialize() {
        String lastEmail = SessionHandler.loadEmail();
        emailField.setText(lastEmail);

        loginButton.setOnAction(event -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                System.out.println("All fields are required.");
                return;
            }

            Integer userId = Login.loginUser(email, password);

            if (userId != null) {
                System.out.println("Login successful!");
                SessionHandler.saveEmail(email);
                String token = SessionHandler.generateToken();
                SessionHandler.saveSession(userId, email, token);
                UserSession.setCurrentUserId(userId);

                try {
                    Parent mainRoot = FXMLLoader.load(getClass().getResource("/layouts/mainLayout.fxml"));
                    Scene mainScene = new Scene(mainRoot);
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(mainScene);
                    stage.setTitle("Password Manager");
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
