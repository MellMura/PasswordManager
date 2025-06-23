package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FolderCard {
    private FolderModel currentModel;

    @FXML
    private Label nameLabel;
    @FXML
    private StackPane cardWrapper;
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
        return nameLabel.getText();
    }

    public void setData(int id, String name) {
        currentModel = new FolderModel(id, name);
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
    }


}
