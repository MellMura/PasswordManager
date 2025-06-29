package application.controllers;

import application.managers.AccountManager;
import application.models.Account;
import application.managers.FolderManager;
import application.models.Folder;
import application.models.UserSession;
import application.utils.FileSaver;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
    @FXML private TextField searchField;
    @FXML private Button clearSearchButton;
    @FXML private HBox breadcrumbBox;
    private boolean slideButtonsVisible = false;

    private File selectedIconFile;
    private Integer editingAccountId = null;
    private Integer editingFolderId = null;
    private Integer currentFolderId = 0;
    private Folder currentFolderModel;

    private final Map<Node, FolderCard> folderControllerMap = new HashMap<>();
    private final Map<Node, AccountCard> accountControllerMap = new HashMap<>();

    public Pane getHoverLayer() {
        return hoverOverlay;
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

        searchField.setVisible(false);
        searchField.setManaged(false);
        clearSearchButton.setVisible(false);
        clearSearchButton.setManaged(false);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            performSearch(newValue);
        });
    }

    public void loadInitialData() {
        List<Folder> folders = new FolderManager().fetchFolders(0);
        List<Account> accounts = new AccountManager().fetchAccounts(0);
        currentFolderId = 0;
        updateBreadcrumbs();

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
        updateBreadcrumbs();
        List<Folder> folders = new FolderManager().fetchFolders(folderId);
        List<Account> accounts = new AccountManager().fetchAccounts(folderId);

        if (folderId == 0) {
            String username = UserSession.getUsername();
            if (username.endsWith("s")) {
                usernameLabel.setText(username + "' passwords");
            } else {
                usernameLabel.setText(username + "'s passwords");
            }
        } else {
            currentFolderModel = FolderManager.getFolderById(folderId);
            if (currentFolderModel != null) {
                usernameLabel.setText(currentFolderModel.name);
            } else {
                usernameLabel.setText("Unknown Folder");
            }
        }

        tilePane.getChildren().clear();
        folderControllerMap.clear();
        accountControllerMap.clear();

        renderSavedFolders(folders);
        renderSavedAccounts(accounts);
        StackPane backArrowCard = createBackArrowCard();
        tilePane.getChildren().add(0, backArrowCard);
        colorPicker.setValue(javafx.scene.paint.Color.web("#84e8d4"));
    }

    private List<Folder> buildFolderPath(int folderId) {
        List<Folder> path = new java.util.ArrayList<>();
        Folder folder = FolderManager.getFolderById(folderId);

        while (folder != null && folder.id != 0) {
            path.add(0, folder);
            folder = FolderManager.getFolderById(folder.folder_id);
        }

        return path;
    }

    private void updateBreadcrumbs() {
        breadcrumbBox.getChildren().clear();

        Label home = new Label("Home");
        home.getStyleClass().add("breadcrumb");
        home.setOnMouseClicked(e -> loadInitialData());

        breadcrumbBox.getChildren().add(home);

        if (currentFolderId != 0) {
            List<Folder> path = buildFolderPath(currentFolderId);

            for (Folder folder : path) {
                Label separator = new Label(">");
                separator.getStyleClass().add("breadcrumb-separator");
                breadcrumbBox.getChildren().add(separator);

                Label folderLabel = new Label(folder.name);
                folderLabel.getStyleClass().add("breadcrumb");
                folderLabel.setOnMouseClicked(e -> loadFolderData(folder.id));
                breadcrumbBox.getChildren().add(folderLabel);
            }
        }
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
    private void showSearchField(){
        searchField.setVisible(true);
        searchField.setManaged(true);
        clearSearchButton.setVisible(true);
        clearSearchButton.setManaged(true);

        searchField.setOpacity(0);
        searchField.setTranslateX(20);
        clearSearchButton.setOpacity(0);
        clearSearchButton.setTranslateX(20);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), searchField);
        slideIn.setToX(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), searchField);
        fadeIn.setToValue(1);

        TranslateTransition clearSlideIn = new TranslateTransition(Duration.millis(200), clearSearchButton);
        clearSlideIn.setToX(0);
        FadeTransition clearFadeIn = new FadeTransition(Duration.millis(200), clearSearchButton);
        clearFadeIn.setToValue(1);

        new ParallelTransition(slideIn, fadeIn, clearSlideIn, clearFadeIn).play();

        searchField.requestFocus();
    }

    @FXML
    private void hideSearchField(){
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), searchField);
        slideOut.setToX(20);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), searchField);
        fadeOut.setToValue(0);

        TranslateTransition clearSlideOut = new TranslateTransition(Duration.millis(200), clearSearchButton);
        clearSlideOut.setToX(20);
        FadeTransition clearFadeOut = new FadeTransition(Duration.millis(200), clearSearchButton);
        clearFadeOut.setToValue(0);

        ParallelTransition hideTransition = new ParallelTransition(slideOut, fadeOut, clearSlideOut, clearFadeOut);
        hideTransition.setOnFinished(event -> {
            searchField.clear();
            searchField.setVisible(false);
            searchField.setManaged(false);
            clearSearchButton.setVisible(false);
            clearSearchButton.setManaged(false);
        });

        hideTransition.play();
    }

    @FXML
    private void toggleSearchField() {
        boolean currentlyVisible = searchField.isVisible();
        if (currentlyVisible) {
            hideSearchField();
        } else {
            showSearchField();
        }
    }

    @FXML
    private void clearSearchField() {
        searchField.clear();
        loadInitialData();
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
    public void editAccount(Account account) {
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
    public void editFolder(Folder folder) {
        semiTransparent.setVisible(true);
        saveAccountButton.setText("Update");
        nameFolderField.setText(folder.name);
        editingFolderId = folder.id;
        folderSubSceneWrapper.setVisible(true);
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
            if(FolderManager.saveFolder(UserSession.getUserId(), currentFolderId, name)) {
                System.out.println("Folder saved successfully.");
            }
        }

        closeForm();
        if(currentFolderId != 0) {
            loadFolderData(currentFolderId);
        }
        else {
            loadInitialData();
        }
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
            if(AccountManager.saveAccount(UserSession.getUserId(), currentFolderId, name, colorHex, iconPath, email, password)) {
                System.out.println("Account saved successfully.");
            }
        }

        closeForm();
        if(currentFolderId != 0) {
            loadFolderData(currentFolderId);
        }
        else {
            loadInitialData();
        }
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

    public void performSearch(String query) {
        List<Folder> folders = new FolderManager().searchByName(query);
        List<Account> accounts = new AccountManager().searchByName(query);

        tilePane.getChildren().clear();
        folderControllerMap.clear();
        accountControllerMap.clear();

        renderSavedFolders(folders);
        renderSavedAccounts(accounts);
    }

    public void renderSavedFolders(List<Folder> folders) {
        for (Folder fold : folders) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/folderCard.fxml"));
                StackPane folder = loader.load();
                FolderCard controller = loader.getController();
                controller.setMainLayout(this);
                folderControllerMap.put(folder, controller);
                folder.setUserData(fold.id);
                folder.getStyleClass().add("folder-card");
                controller.setData(fold.id, fold.folder_id, fold.name);
                StackPane cardWrapper = new StackPane();
                cardWrapper.getChildren().add(folder);
                tilePane.getChildren().add(cardWrapper);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void renderSavedAccounts(List<Account> accounts) {
        for (Account acc : accounts) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/accountCard.fxml"));
                StackPane card = loader.load();
                AccountCard controller = loader.getController();
                controller.setMainLayout(this);
                accountControllerMap.put(card, controller);
                card.setUserData(acc.id);
                controller.setCurrentFolderModel(currentFolderModel);
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
        backButton.setOnAction(e -> {
            Folder current = FolderManager.getFolderById(currentFolderId);

            if (current != null) {
                int parentId = current.folder_id;

                if (parentId != 0) {
                    loadFolderData(parentId);
                } else {
                    loadInitialData();
                }
            } else {
                loadInitialData(); // fallback
            }
        });

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
