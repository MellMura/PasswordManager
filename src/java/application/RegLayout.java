package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;

public class RegLayout {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TextField passwordCheckField;
    @FXML private Button createAccountButton;
    @FXML private Hyperlink alreadyHaveOne;

    @FXML
    public void initialize() {
        createAccountButton.setOnAction(event -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String passwordCheck = passwordCheckField.getText().trim();

            if (!password.equals(passwordCheck)) {
                System.out.println("Passwords do not match!");
                return;
            }

            Register.registerUser(username, email, password);
        });
    }
}
