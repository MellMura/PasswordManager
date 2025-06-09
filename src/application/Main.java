package application;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public class Main extends Application {
    private static final String WINDOW_TITLE = "TEST PASSWORD MANAGER";

    private TextField usernameField;
    private TextField emailField;
    private TextField passwordField;
    private TextField passwordCheckField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            initWindow();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void initWindow() {
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle(WINDOW_TITLE);

        Button regButton = new Button();
        regButton.setText("Create account");
        regButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText().trim();
                String passwordCheck = passwordCheckField.getText().trim();

                if (!password.equals(passwordCheck)) {
                    System.out.println("Passwords do not match!");
                    return;
                }

                Register.registerUser(username, email, password);
            }
        });

        VBox root = getVBox();

        root.getChildren().add(regButton);
        root.setStyle("-fx-background-color: darkcyan;");
        Scene scene = new Scene(root, 1400, 700);
        stage.setScene(scene);
        stage.show();
    }

    private VBox getVBox() {
        Label welcomeLabel = new Label("Welcome to Password Manager Alpha!");
        Label regLabel = new Label("Let's create your personal account");

        Label usernameLabel = new Label("Create a Username:");
        usernameField = new TextField();
        usernameField.setFocusTraversable(false);

        Label emailLabel = new Label("Enter your Email:");
        emailField = new TextField();
        emailField.setFocusTraversable(false);

        Label passwordLabel = new Label("Create a strong Master Password:");
        passwordField = new PasswordField();
        passwordField.setFocusTraversable(false);

        Label passwordCheckLabel = new Label("Repeat your Password:");
        passwordCheckField = new PasswordField();
        passwordCheckField.setFocusTraversable(false);

        VBox root = new VBox(10);
        Node[] elements = { welcomeLabel, regLabel, usernameLabel, usernameField, emailLabel, emailField, passwordLabel, passwordField, passwordCheckLabel, passwordCheckField };
        root.getChildren().addAll(elements);
        return root;
    }


}
