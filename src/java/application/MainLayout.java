package application;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainLayout implements Initializable {
    @FXML private SubScene passwordSubScene;
    @FXML private SubScene folderSubScene;

    @FXML private StackPane passwordSubSceneWrapper;
    @FXML private StackPane folderSubSceneWrapper;
    @FXML private TextField nameField;
    @FXML private TextField nameFolderField;
    @FXML private ColorPicker colorPicker;
    @FXML private Button iconButton;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TilePane tilePane;
    @FXML private Pane semiTransparent;
    @FXML private Button saveAccountButton;
    @FXML private Label usernameLabel;
    @FXML private Pane hoverOverlay;
    @FXML private HBox slidingButtonBox;
    @FXML private Button folderButton;
    @FXML private Button passwordButton;
    @FXML private Button addAccountButton;
    private boolean slideButtonsVisible = false;

    private boolean isHovering = false;
    private javafx.animation.PauseTransition hideDelay;

    private File selectedIconFile;
    private Integer editingAccountId = null;
    private Integer editingFolderId = null;
    private Integer currentFolderId = 0;

    private final Map<Node, FolderCard> folderControllerMap = new HashMap<>();
    private final Map<Node, AccountCard> accountControllerMap = new HashMap<>();

    public Pane getHoverLayer() {
        return hoverOverlay;
    }

    private void showSlideButtons() {
        passwordButton.setTranslateX(50);
        folderButton.setTranslateX(50);
        passwordButton.setOpacity(0);
        folderButton.setOpacity(0);

        TranslateTransition tt1 = new TranslateTransition(Duration.millis(150), passwordButton);
        tt1.setToX(0);
        TranslateTransition tt2 = new TranslateTransition(Duration.millis(150), folderButton);
        tt2.setToX(0);

        FadeTransition ft1 = new FadeTransition(Duration.millis(150), passwordButton);
        ft1.setToValue(1);
        FadeTransition ft2 = new FadeTransition(Duration.millis(150), folderButton);
        ft2.setToValue(1);

        new ParallelTransition(tt1, tt2, ft1, ft2).play();
    }


    private void hideSlideButtons() {
        TranslateTransition tt1 = new TranslateTransition(Duration.millis(150), passwordButton);
        tt1.setToX(50);
        TranslateTransition tt2 = new TranslateTransition(Duration.millis(150), folderButton);
        tt2.setToX(50);

        FadeTransition ft1 = new FadeTransition(Duration.millis(150), passwordButton);
        ft1.setToValue(0);
        FadeTransition ft2 = new FadeTransition(Duration.millis(150), folderButton);
        ft2.setToValue(0);

        new ParallelTransition(tt1, tt2, ft1, ft2).play();
    }

    @FXML
    private void toggleSlideButtons() {
        if (slideButtonsVisible) {
            hideSlideButtons();
        } else {
            showSlideButtons();
        }
        slideButtonsVisible = !slideButtonsVisible;
    }

    @FXML
    public void addAccount() {
        semiTransparent.setVisible(true);
        passwordSubSceneWrapper.setVisible(true);
    }

    @FXML
    public void addFolder() {
        semiTransparent.setVisible(true);
        folderSubSceneWrapper.setVisible(true);
    }

    @FXML
    public void editAccount(AccountModel account) {
        semiTransparent.setVisible(true);
        saveAccountButton.setText("Update");
        nameField.setText(account.name);
        emailField.setText(account.email);
        passwordField.setText(account.password);
        colorPicker.setValue(javafx.scene.paint.Color.web(account.color));


        if (account.iconUrl != null) {
            File file = new File(account.iconUrl);
            if (file.exists()) {
                selectedIconFile = file;
                iconButton.setText(file.getName());
            }
        }
        editingAccountId = account.id;
        passwordSubSceneWrapper.setVisible(true);
    }

    @FXML
    public void editFolder(FolderModel folder) {
        semiTransparent.setVisible(true);
        saveAccountButton.setText("Update");
        nameFolderField.setText(folder.name);
        editingFolderId = folder.id;
        folderSubSceneWrapper.setVisible(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadInitialData();

        javafx.application.Platform.runLater(() -> {
            String css = getClass().getResource("/styles/main.css").toExternalForm();
            tilePane.getScene().getStylesheets().add(css);

            tilePane.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                Node target = event.getPickResult().getIntersectedNode();
                if (slideButtonsVisible &&
                        target != addAccountButton &&
                        !slidingButtonBox.getChildren().contains(target)) {
                    hideSlideButtons();
                    slideButtonsVisible = false;
                }
            });
        });

        String username = UserSession.getUsername();
        if (username.endsWith("s")) {
            usernameLabel.setText(username + "' passwords");
        } else {
            usernameLabel.setText(username + "'s passwords");
        }

        ImageView passwordIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/key.png")));
        passwordIcon.setFitWidth(18);
        passwordIcon.setFitHeight(18);
        passwordButton.setGraphic(passwordIcon);

        ImageView folderIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/folder.png")));
        folderIcon.setFitWidth(18);
        folderIcon.setFitHeight(18);
        folderButton.setGraphic(folderIcon);

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/icons/add-image.png")));
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        iconButton.setGraphic(icon);
    }

    public void loadInitialData() {
        List<FolderModel> folders = new FolderManager().fetchFolders();
        List<AccountModel> accounts = new AccountManager().fetchAccounts(0);

        tilePane.getChildren().clear();
        folderControllerMap.clear();
        accountControllerMap.clear();
        String username = UserSession.getUsername();
        if (username.endsWith("s")) {
            usernameLabel.setText(username + "' passwords");
        } else {
            usernameLabel.setText(username + "'s passwords");
        }
        renderSavedFolders(folders);
        renderSavedAccounts(accounts);
        colorPicker.setValue(javafx.scene.paint.Color.web("#84e8d4"));
    }

    public void loadFolderData(int folderId) {
        this.currentFolderId = folderId;
        List<AccountModel> accounts = new AccountManager().fetchAccounts(folderId);

        if (folderId == 0) {
            String username = UserSession.getUsername();
            if (username.endsWith("s")) {
                usernameLabel.setText(username + "' passwords");
            } else {
                usernameLabel.setText(username + "'s passwords");
            }
        } else {
            FolderModel folder = FolderManager.getFolderById(folderId);
            if (folder != null) {
                usernameLabel.setText(folder.name);
            } else {
                usernameLabel.setText("Unknown Folder");
            }
        }

        tilePane.getChildren().clear();
        folderControllerMap.clear();
        accountControllerMap.clear();

        renderSavedAccounts(accounts);
        StackPane backArrowCard = createBackArrowCard();
        tilePane.getChildren().add(0, backArrowCard);
        colorPicker.setValue(javafx.scene.paint.Color.web("#84e8d4"));
    }

    @FXML
    public void closeForm() {
        semiTransparent.setVisible(false);
        passwordSubSceneWrapper.setVisible(false);
        folderSubSceneWrapper.setVisible(false);
        nameField.clear();
        nameFolderField.clear();
        emailField.clear();
        passwordField.clear();
        colorPicker.setValue(javafx.scene.paint.Color.web("#84e8d4"));
        selectedIconFile = null;
        editingAccountId = null;
        saveAccountButton.setText("Add");

        iconButton.setText("");
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/icons/add-image.png")));
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        iconButton.setGraphic(icon);
    }

    @FXML
    public void saveFolder() {
        String name = nameFolderField.getText().trim();
        if (name.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }

        if (editingFolderId != null) {
            if(FolderManager.updateFolder(editingFolderId, name)) {
                System.out.println("Folder updated successfully.");
            }
        } else {
            if(FolderManager.saveFolder(UserSession.getUserId(), name)) {
                System.out.println("Folder saved successfully.");
            }
        }

        closeForm();
        loadInitialData();
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

        if (editingAccountId != null) {
            if(AccountManager.updateAccount(editingAccountId, name, colorHex, iconPath, email, password)) {
                System.out.println("Account updated successfully.");
            }
        } else {
            if(AccountManager.saveAccount(UserSession.getUserId(), name, colorHex, iconPath, email, password)) {
                System.out.println("Account saved successfully.");
            }
        }

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

    public void renderSavedFolders(List<FolderModel> folders) {
        for (FolderModel fold : folders) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/folderCard.fxml"));
                StackPane folder = loader.load();
                FolderCard controller = loader.getController();
                controller.setMainLayout(this);
                folderControllerMap.put(folder, controller);
                folder.setUserData(fold.id);
                folder.getStyleClass().add("folder-card");
                controller.setData(fold.id, fold.name);
                StackPane cardWrapper = new StackPane();
                cardWrapper.getChildren().add(folder);
                tilePane.getChildren().add(cardWrapper);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void renderSavedAccounts(List<AccountModel> accounts) {
        for (AccountModel acc : accounts) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/accountCard.fxml"));
                StackPane card = loader.load();
                AccountCard controller = loader.getController();
                controller.setMainLayout(this);
                accountControllerMap.put(card, controller);
                card.setUserData(acc.id);
                card.getStyleClass().add("account-card");
                controller.setData(acc.id, acc.name, acc.email, acc.password, acc.iconUrl, acc.color);
                StackPane cardWrapper = new StackPane();
                cardWrapper.getChildren().add(card);
                tilePane.getChildren().add(cardWrapper);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private StackPane createBackArrowCard() {
        StackPane arrowCard = new StackPane();
        arrowCard.getStyleClass().add("account-card");

        ImageView arrowIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/reply-big.png")));
        arrowIcon.setFitWidth(94);
        arrowIcon.setFitHeight(94);

        Button backButton = new Button();
        backButton.setGraphic(arrowIcon);
        backButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        backButton.setOnAction(e -> loadInitialData());

        arrowCard.setOnDragOver(event -> {
            if (event.getGestureSource() != arrowCard && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        arrowCard.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                try {
                    int accountId = Integer.parseInt(db.getString());
                    AccountManager.updateFolderId(accountId, 0);
                    loadFolderData(currentFolderId);
                    success = true;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });

        arrowCard.getChildren().add(backButton);
        return arrowCard;
    }
}
