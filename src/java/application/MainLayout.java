package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainLayout {
    @FXML private SubScene formSubScene;

    @FXML private TextField nameField;
    @FXML private ColorPicker colorPicker;
    @FXML private Button iconButton;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TilePane tilePane;

    private File selectedIconFile;

    @FXML
    public void addAccount() {
        formSubScene.setVisible(true);
    }

    @FXML
    public void initialize() {
        List<AccountModel> accounts = new AccountManager().fetchAccounts();
        renderSavedAccounts(accounts);
        colorPicker.setValue(javafx.scene.paint.Color.web("#00bfff"));
    }

    @FXML
    public void closeForm() {
        formSubScene.setVisible(false);
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        colorPicker.setValue(javafx.scene.paint.Color.web("#00bfff"));
        selectedIconFile = null;
        iconButton.setText("Choose Icon");
    }

    @FXML
    public void saveAccount() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        String colorHex = String.format("#%02X%02X%02X",
                (int)(colorPicker.getValue().getRed() * 255),
                (int)(colorPicker.getValue().getGreen() * 255),
                (int)(colorPicker.getValue().getBlue() * 255));

        String iconPath = FileSaver.saveToUploads(selectedIconFile);

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }

        AccountManager.saveAccount(name, colorHex, iconPath, email, password);
        closeForm();
        initialize();
    }

    @FXML
    public void chooseIcon() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Icon");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(iconButton.getScene().getWindow());
        if (file != null) {
            selectedIconFile = file;
            iconButton.setText(file.getName());
        }
    }

    public void renderSavedAccounts(List<AccountModel> accounts) {
        tilePane.getChildren().clear();

        for (AccountModel acc : accounts) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/accountCard.fxml"));
                StackPane card = loader.load();
                AccountCard controller = loader.getController();
                controller.setData(acc.name, acc.email, acc.password, acc.iconUrl, acc.color);

                tilePane.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
