package application;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.File;

public class AccountCard {
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private ImageView iconImage;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private StackPane cardWrapper;
    @FXML
    private Button deleteButton;
    @FXML
    private HBox hoverButtons;
    private MainLayout mainLayout;

    private AccountModel currentModel;

    public void setMainLayout(MainLayout layout) {
        this.mainLayout = layout;
    }

    public String getName() {
        return nameLabel.getText();
    }

    @FXML
    public void initialize() {
        cardWrapper.setOnMouseEntered(e -> {
            hoverButtons.setVisible(true);
            hoverButtons.setOpacity(1);
        });
        cardWrapper.setOnMouseExited(e -> {
            hoverButtons.setVisible(false);
            hoverButtons.setOpacity(0);
        });

        cardWrapper.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                Dragboard db = cardWrapper.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString("drag");
                db.setContent(content);
                db.setDragView(cardWrapper.snapshot(null, null));
                event.consume();
            }
        });

        cardWrapper.setOnDragOver(event -> {
            if (event.getGestureSource() != cardWrapper && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        cardWrapper.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                Node draggedNode = (Node) event.getGestureSource();

                TilePane parent = (TilePane) cardWrapper.getParent();
                ObservableList<Node> children = parent.getChildren();

                int fromIndex = children.indexOf(draggedNode);
                int toIndex = children.indexOf(cardWrapper);

                if (fromIndex != -1 && toIndex != -1 && fromIndex != toIndex) {
                    children.remove(fromIndex);
                    children.add(toIndex, draggedNode);

                    Integer draggedId = (Integer) draggedNode.getUserData();
                    AccountManager.reorderPositions(fromIndex + 1, toIndex + 1, draggedId);
                    mainLayout.loadInitialData();
                }

                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void setData(int id, String name, String email, String password, String iconUrl, String colorHex) {
        currentModel = new AccountModel(id, name, email, password, iconUrl, colorHex);

        nameLabel.setText(name);
        emailLabel.setText("E-mail: " + email);
        passwordLabel.setText("Password: " + password);

        if (iconUrl != null && !iconUrl.isEmpty()) {
            File iconFile = new File(iconUrl);
            if (iconFile.exists()) {
                Image image = new Image(iconFile.toURI().toString());
                iconImage.setImage(image);
            }
        }

        try {
            Color color = Color.web(colorHex);
            rootPane.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid color: " + colorHex);
        }
    }



    public void deleteAccount() {
        String name = nameLabel.getText();
        boolean success = AccountManager.removeAccount(name);
        if (success) {
            Pane parent = (Pane) cardWrapper.getParent();
            parent.getChildren().remove(cardWrapper);
        }

    }

    @FXML
    public void editAccount() {
        AccountModel acc = new AccountModel();
        acc.id = (Integer) cardWrapper.getUserData(); // or pass as field if you store it in setData
        acc.name = nameLabel.getText();
        acc.email = emailLabel.getText().replace("E-mail: ", "");
        acc.password = passwordLabel.getText().replace("Password: ", "");
        Background bg = rootPane.getBackground();
        if (bg != null && !bg.getFills().isEmpty()) {
            acc.color = bg.getFills().get(0).getFill().toString();
        } else {
            acc.color = "#00bfff";
        }
        acc.iconUrl = iconImage.getImage() != null ? iconImage.getImage().getUrl().replace("file:/", "") : null;

        mainLayout.editAccount(acc);
    }
}
