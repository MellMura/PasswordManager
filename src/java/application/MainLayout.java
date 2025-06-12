package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainLayout implements Initializable {
    @FXML private SubScene formSubScene;

    @FXML private TextField nameField;
    @FXML private ColorPicker colorPicker;
    @FXML private Button iconButton;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TilePane tilePane;

    private File selectedIconFile;

    private final Map<Node, AccountCard> controllerMap = new HashMap<>();


    @FXML
    public void addAccount() {
        formSubScene.setVisible(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadInitialData();
    }

    public void loadInitialData() {
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

        AccountManager.saveAccount(UserSession.getUserId(), name, colorHex, iconPath, email, password);
        closeForm();
        loadInitialData();
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
        controllerMap.clear();

        for (AccountModel acc : accounts) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/accountCard.fxml"));
                StackPane card = loader.load();
                AccountCard controller = loader.getController();
                controller.setData(acc.name, acc.email, acc.password, acc.iconUrl, acc.color);
                controller.setMainLayout(this);
                controllerMap.put(card, controller);
                card.setUserData(acc.id);
                tilePane.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleDrop(Node draggedNode, Node targetNode) {
        if (draggedNode == null || targetNode == null || draggedNode == targetNode) return;

        List<Node> nodes = new java.util.ArrayList<>(tilePane.getChildren());
        nodes.remove(draggedNode);

        int insertIndex = nodes.indexOf(targetNode);
        nodes.add(insertIndex, draggedNode);

        tilePane.getChildren().setAll(nodes);
    }
}
