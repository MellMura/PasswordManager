package application.controllers;

import application.managers.auth.RegisterManager;
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

public class RegLayout {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TextField passwordCheckField;
    @FXML private Button createAccountButton;
    @FXML private Hyperlink switchToLoginLink;
    @FXML private AnchorPane rootPane;

    @FXML
    public void initialize() {
        Platform.runLater(() -> rootPane.requestFocus());
        createAccountButton.setOnAction(event -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String passwordCheck = passwordCheckField.getText().trim();

            if (!password.equals(passwordCheck)) {
                System.out.println("Passwords do not match!");
                return;
            }

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                System.out.println("All fields are required.");
                return;
            }

            boolean success = RegisterManager.registerUser(username, email, password);
            if (success) {
                switchToLoginScene();
            }
        });
    }

    private void switchToLoginScene() {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/layouts/loginLayout.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) createAccountButton.getScene().getWindow();
            stage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToLogin(ActionEvent event) {
        switchToLoginScene();
    }
}
