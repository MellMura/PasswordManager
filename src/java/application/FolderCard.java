package application;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FolderCard {
    private FolderModel currentModel;

    @FXML
    private Label nameFolderLabel;
    @FXML
    private StackPane cardWrapper;
    @FXML
    private VBox hoverButtons;
    private Button editButton;
    private Button deleteButton;
    private MainLayout mainLayout;

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

    public void setMainLayout(MainLayout layout) {
        this.mainLayout = layout;
    }

    public String getName() {
        return nameFolderLabel.getText();
    }

    public void deleteFolder() {
        String name = nameFolderLabel.getText();
        boolean success = FolderManager.removeFolder(name);
        if (success) {
            mainLayout.loadInitialData();
        }
    }

    public void setData(int id, String name) {
        currentModel = new FolderModel(id, name);
        hoverButtons = new VBox(5);
        hoverButtons.setVisible(false);
        hoverButtons.setOpacity(0);
        hoverButtons.setTranslateX(-10);

        Button editBtn = new Button();
        Button deleteBtn = new Button();

        editBtn.getStyleClass().add("hover-folder-button");
        deleteBtn.getStyleClass().add("hover-folder-button");
        this.editButton = editBtn;
        this.deleteButton = deleteBtn;

        hoverButtons.getChildren().addAll(editBtn, deleteBtn);
        hoverButtons.setOnMouseEntered(e -> showHoverButtons());
        hoverButtons.setOnMouseExited(e -> hideHoverButtons());
        if (!mainLayout.getHoverLayer().getChildren().contains(hoverButtons)) {
            mainLayout.getHoverLayer().getChildren().add(hoverButtons);
        }

        nameFolderLabel.setText(name);
        cardWrapper.setOnMouseEntered(e -> showHoverButtons());
        cardWrapper.setOnMouseExited(e -> hideHoverButtons());

        ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/pencil-black.png")));
        editIcon.setFitWidth(18);
        editIcon.setFitHeight(18);
        editBtn.setGraphic(editIcon);
        editBtn.setPrefSize(38, 38);
        editBtn.setOnAction(e -> mainLayout.editFolder(currentModel));
        editBtn.setAlignment(Pos.CENTER);

        ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/trash-black.png")));
        deleteIcon.setFitWidth(18);
        deleteIcon.setFitHeight(18);
        deleteBtn.setGraphic(deleteIcon);
        deleteBtn.setPrefSize(38, 38);
        deleteBtn.setOnAction(e -> deleteFolder());
        deleteBtn.setAlignment(Pos.CENTER);
    }
}
