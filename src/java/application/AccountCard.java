package application;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
    @FXML private HBox emailBox;
    @FXML private HBox passwordBox;
    @FXML
    private ImageView iconImage;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private StackPane cardWrapper;
    @FXML
    private HBox cardHeader;
    private Label emailValue;
    private Label passwordValue;

    private VBox hoverButtons;
    private Button editButton;
    private Button deleteButton;
    private MainLayout mainLayout;

    private AccountModel currentModel;

    private String originalPassword;
    private boolean isPasswordVisible = false;


    @FXML
    public void initialize() {
        cardWrapper.setOnMouseEntered(e -> showHoverButtons());
        cardWrapper.setOnMouseExited(e -> hideHoverButtons());

        cardWrapper.setOnDragDetected(event -> {
            Dragboard db = cardWrapper.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(currentModel.id + "");
            db.setContent(content);
            db.setDragView(cardWrapper.snapshot(null, null));
            event.consume();
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

                Node draggedWrapper = draggedNode;
                Node targetWrapper = cardWrapper;

                // Traverse up to the outer StackPane wrapper
                while (!(draggedWrapper.getParent() instanceof TilePane)) {
                    draggedWrapper = draggedWrapper.getParent();
                }
                while (!(targetWrapper.getParent() instanceof TilePane)) {
                    targetWrapper = targetWrapper.getParent();
                }

                TilePane parent = (TilePane) targetWrapper.getParent();
                ObservableList<Node> children = parent.getChildren();

                int fromIndex = children.indexOf(draggedWrapper);
                int toIndex = children.indexOf(targetWrapper);

                if (fromIndex != -1 && toIndex != -1 && fromIndex != toIndex) {
                    children.remove(fromIndex);
                    children.add(toIndex, draggedWrapper);

                    int draggedId = Integer.parseInt(db.getString());
                    AccountManager.reorderPositions(fromIndex + 1, toIndex + 1, draggedId);
                    mainLayout.loadInitialData();
                }

                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private Color saturateColor(Color color, double factor) {
        float[] hsb = java.awt.Color.RGBtoHSB(
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                null
        );

        // Increase saturation: clamp to 1.0 max
        hsb[1] = Math.min(1.0f, hsb[1] * (float) factor);

        int rgb = java.awt.Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        return Color.rgb(r, g, b, color.getOpacity());
    }

    private Color lightenColor(Color color, double factor) {
        double r = Math.min(1.0, color.getRed() + (1.0 - color.getRed()) * factor);
        double g = Math.min(1.0, color.getGreen() + (1.0 - color.getGreen()) * factor);
        double b = Math.min(1.0, color.getBlue() + (1.0 - color.getBlue()) * factor);
        return new Color(r, g, b, color.getOpacity());
    }

    private boolean isDark(Color color) {
        double luminance = 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
        return luminance < 0.5;
    }

    public void setMainLayout(MainLayout layout) {
        this.mainLayout = layout;
    }

    public String getName() {
        return nameLabel.getText();
    }

    @FXML
    private void handleMouseEntered() {
        if (hoverButtons != null) {
            if (!mainLayout.getHoverLayer().getChildren().contains(hoverButtons)) {
                mainLayout.getHoverLayer().getChildren().add(hoverButtons);
            }

            // Translate scene position to layout coordinates
            double sceneX = cardWrapper.localToScene(0, 0).getX();
            double sceneY = cardWrapper.localToScene(0, 0).getY();
            double layoutX = sceneX - mainLayout.getHoverLayer().localToScene(0, 0).getX() + cardWrapper.getWidth() - 10;
            double layoutY = sceneY - mainLayout.getHoverLayer().localToScene(0, 0).getY() + 10;

            hoverButtons.setLayoutX(layoutX);
            hoverButtons.setLayoutY(layoutY);
            hoverButtons.setVisible(true);
            hoverButtons.setOpacity(1.0);
            hoverButtons.getStyleClass().add("hover-buttons");
        }
    }

    @FXML
    private void handleMouseExited() {
        if (hoverButtons != null) {
            hoverButtons.setVisible(false);
            hoverButtons.setOpacity(0.0);
        }
    }


    private void showHoverButtons() {
        if (hoverButtons != null) {
            if (!mainLayout.getHoverLayer().getChildren().contains(hoverButtons)) {
                mainLayout.getHoverLayer().getChildren().add(hoverButtons);
            }

            // Position the buttons outside top-right
            double sceneX = cardWrapper.localToScene(0, 0).getX();
            double sceneY = cardWrapper.localToScene(0, 0).getY();
            double offsetX = sceneX - mainLayout.getHoverLayer().localToScene(0, 0).getX() + cardWrapper.getWidth() + 5;
            double offsetY = sceneY - mainLayout.getHoverLayer().localToScene(0, 0).getY();

            hoverButtons.setLayoutX(offsetX);
            hoverButtons.setLayoutY(offsetY);
            hoverButtons.setVisible(true);
            hoverButtons.setOpacity(1);
        }
    }

    private void hideHoverButtons() {
        if (hoverButtons != null) {
            hoverButtons.setVisible(false);
            hoverButtons.setOpacity(0);
        }
    }

    public void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        String password = currentModel != null ? currentModel.password : "";
        passwordValue.setText(isPasswordVisible ? password : "•".repeat(password.length()));
    }

    public void setData(int id, String name, String email, String password, String iconUrl, String colorHex) {
        currentModel = new AccountModel(id, name, email, password, iconUrl, colorHex);
        hoverButtons = new VBox(5);
        hoverButtons.setVisible(false);
        hoverButtons.setOpacity(0);

        Button editBtn = new Button();
        Button deleteBtn = new Button();

        editBtn.getStyleClass().add("hover-button");
        deleteBtn.getStyleClass().add("hover-button");
        this.editButton = editBtn;
        this.deleteButton = deleteBtn;

        hoverButtons.getChildren().addAll(editBtn, deleteBtn);
        hoverButtons.setOnMouseEntered(e -> showHoverButtons());
        hoverButtons.setOnMouseExited(e -> hideHoverButtons());
        if (!mainLayout.getHoverLayer().getChildren().contains(hoverButtons)) {
            mainLayout.getHoverLayer().getChildren().add(hoverButtons);
        }

        nameLabel.setText(name);

        if (iconUrl != null && !iconUrl.isEmpty()) {
            File iconFile = new File(iconUrl);
            if (iconFile.exists()) {
                Image image = new Image(iconFile.toURI().toString());
                iconImage.setImage(image);
            }
        }

        try {Color baseColor = Color.web(colorHex);
            Color saturatedColor = saturateColor(baseColor, 1.7);
            Color textColor = isDark(saturatedColor) ? Color.WHITE : Color.BLACK;
            Color finalSaturatedColor = saturatedColor;


            editBtn.setOnMouseEntered(e -> {
                Color hoverColor = isDark(finalSaturatedColor)
                        ? lightenColor(finalSaturatedColor, 0.2)
                        : saturateColor(finalSaturatedColor, 1.4);
                editBtn.setBackground(new Background(new BackgroundFill(hoverColor, new CornerRadii(5), Insets.EMPTY)));
            });
            editBtn.setOnMouseExited(e -> {
                editBtn.setBackground(new Background(new BackgroundFill(finalSaturatedColor, new CornerRadii(5), Insets.EMPTY)));
            });

            deleteBtn.setOnMouseEntered(e -> {
                Color hoverColor = isDark(finalSaturatedColor)
                        ? lightenColor(finalSaturatedColor, 0.2)
                        : saturateColor(finalSaturatedColor, 1.4);
                deleteBtn.setBackground(new Background(new BackgroundFill(hoverColor, new CornerRadii(5), Insets.EMPTY)));
            });
            deleteBtn.setOnMouseExited(e -> {
                deleteBtn.setBackground(new Background(new BackgroundFill(finalSaturatedColor, new CornerRadii(5), Insets.EMPTY)));
            });
            rootPane.setBackground(new Background(
                    new BackgroundFill(baseColor, new CornerRadii(10), Insets.EMPTY)
            ));
            cardHeader.setBackground(new Background(
                    new BackgroundFill(saturatedColor, new CornerRadii(10), Insets.EMPTY)
            ));
            deleteButton.setBackground(new Background(
                    new BackgroundFill(saturatedColor, new CornerRadii(5), Insets.EMPTY)
            ));
            editButton.setBackground(new Background(
                    new BackgroundFill(saturatedColor, new CornerRadii(5), Insets.EMPTY)
            ));
            nameLabel.setTextFill(textColor);

            String iconVariant = textColor.equals(Color.WHITE) ? "-white" : "-black";
            ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/pencil" + iconVariant + ".png")));
            editIcon.setFitWidth(18);
            editIcon.setFitHeight(18);
            editBtn.setGraphic(editIcon);
            editBtn.setPrefSize(38, 38);
            editBtn.setOnAction(e -> editAccount());
            editBtn.setAlignment(Pos.CENTER);

            ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/trash" + iconVariant + ".png")));
            deleteIcon.setFitWidth(18);
            deleteIcon.setFitHeight(18);
            deleteBtn.setGraphic(deleteIcon);
            deleteBtn.setPrefSize(38, 38);
            deleteBtn.setOnAction(e -> deleteAccount());
            editBtn.setAlignment(Pos.CENTER);

            ImageView emailIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/mail" + iconVariant + ".png")));
            emailIcon.setFitWidth(16);
            emailIcon.setFitHeight(16);

            emailValue = new Label(email);
            emailValue.setTextFill(textColor);

            emailBox.getChildren().clear();
            emailBox.getChildren().addAll(emailIcon, emailValue);

            ImageView passwordIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/lock-keyhole" + iconVariant + ".png")));
            passwordIcon.setFitWidth(16);
            passwordIcon.setFitHeight(16);

            this.originalPassword = password;
            passwordValue = new Label(isPasswordVisible ? originalPassword : "•".repeat(originalPassword.length()));

            isPasswordVisible = false;
            passwordValue.setTextFill(textColor);
            passwordValue.setOnMouseClicked(event -> togglePasswordVisibility());
            passwordValue.setCursor(Cursor.HAND);

            passwordBox.getChildren().clear();
            passwordBox.getChildren().addAll(passwordIcon, passwordValue);

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid color: " + colorHex);
        }
    }



    public void deleteAccount() {
        String name = nameLabel.getText();
        boolean success = AccountManager.removeAccount(name);
        if (success) {
            mainLayout.loadInitialData();
        }
    }

    @FXML
    public void editAccount() {
        AccountModel acc = new AccountModel();
        acc.id = (Integer) cardWrapper.getUserData();
        acc.name = nameLabel.getText();
        acc.email = emailValue.getText().replace("E-mail: ", "");
        acc.password = currentModel.password;
        Background bg = rootPane.getBackground();
        if (bg != null && !bg.getFills().isEmpty()) {
            acc.color = bg.getFills().get(0).getFill().toString();
        } else {
            acc.color = "#84e8d4";
        }
        acc.iconUrl = iconImage.getImage() != null ? iconImage.getImage().getUrl().replace("file:/", "") : null;

        mainLayout.editAccount(acc);
    }
}
