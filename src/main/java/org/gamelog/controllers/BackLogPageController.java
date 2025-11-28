package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class BackLogPageController {

    @FXML
    private Button addBacklogItemBtn;
    @FXML
    private Button filterBacklogItemsBtn;
    @FXML
    private Label backlogEmptyLabel;
    @FXML
    private TilePane backlogContainer;
    @FXML
    private VBox contentVBox;

    @FXML
    public void initialize() {
        updateEmptyState();

        addBacklogItemBtn.setOnMouseClicked(e -> {
            showAddGameModal();
        });
    }

    private void showAddGameModal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/gamelog/Pages/add_game_modal.fxml")
            );

            VBox modalRoot = loader.load();
            AddGameModalController modalController = loader.getController();

            Stage modalStage = new Stage();
            modalController.setStage(modalStage);

            Scene scene = new Scene(modalRoot);
            modalStage.setScene(scene);
            modalStage.setTitle("Game Addition");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initStyle(StageStyle.UTILITY);
            modalStage.setResizable(false);
            modalStage.showAndWait();

            if (modalController.isConfirmed()) {
                String gameName = modalController.getSelectedGameName();
                String platform = modalController.getSelectedPlatform();
                addGameCard(gameName, platform);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addGameCard(String gameName, String platform) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/gamelog/Components/small-card.fxml")
            );
            Node card = loader.load();
            SmallCardController cardController = loader.getController();
            cardController.setCardData(gameName, platform, 0, 20);
            cardController.setCardNode(card);

            backlogContainer.getChildren().add(card);
            updateEmptyState();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateEmptyState() {
        boolean empty = backlogContainer.getChildren().isEmpty();
        backlogEmptyLabel.setVisible(empty);
        backlogEmptyLabel.setManaged(empty);

        if (empty) {
            contentVBox.setAlignment(Pos.CENTER);
        } else {
            contentVBox.setAlignment(Pos.TOP_LEFT);
        }
    }
}