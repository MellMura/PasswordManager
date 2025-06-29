package application.controllers;

import application.managers.auth.LoginManager;
import application.managers.SessionManager;
import application.models.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginLayout {
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink switchToRegLink;
    @FXML private AnchorPane rootPane;

    @FXML
    public void initialize() {
        Platform.runLater(() -> rootPane.requestFocus());
        String lastEmail = SessionManager.loadEmail();
        emailField.setText(lastEmail);

        loginButton.setOnAction(event -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                System.out.println("All fields are required.");
                return;
            }

            Integer userId = LoginManager.loginUser(email, password);

            if (userId != null) {
                System.out.println("Login successful!");
                SessionManager.saveEmail(email);
                String token = SessionManager.generateToken();
                SessionManager.saveSession(userId, email, token);
                UserSession.setCurrentUserId(userId);

                try {
                    Parent mainRoot = FXMLLoader.load(getClass().getResource("/layouts/mainLayout.fxml"));
                    Scene mainScene = new Scene(mainRoot);
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(mainScene);
                    stage.setMaximized(true);
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
