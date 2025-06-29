package application;
import application.utils.EncryptionHandler;
import application.utils.EnvHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main extends Application {
    private static final String WINDOW_TITLE = "TEST PASSWORD MANAGER";

    public static void main(String[] args) {
        Logger.getLogger("javafx.fxml").setLevel(Level.SEVERE);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            SetUpDB.initializeDB();
            EnvHandler.loadEnv(".env");
            EncryptionHandler.generateAndStoreKey("keystore.jceks", EnvHandler.get("KEYSTORE_PASS"));

            initWindow();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void initWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/regLayout.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 600, 400);
            Stage stage = new Stage(StageStyle.DECORATED);
            //stage.setMaximized(true);
            stage.setTitle(WINDOW_TITLE);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
