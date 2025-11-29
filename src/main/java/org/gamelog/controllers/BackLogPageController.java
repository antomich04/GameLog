package org.gamelog.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.gamelog.model.BacklogItem;
import org.gamelog.model.SearchResult;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.GamesRepo;
import java.io.IOException;
import java.util.List;

public class BackLogPageController {

    @FXML
    private Button addBacklogItemBtn;
    @FXML
    private Button filterBacklogItemsBtn;
    @FXML
    private Text backlogEmptyLabel;
    @FXML
    private TilePane backlogContainer;
    @FXML
    private VBox loadingOverlay;

    private final String username = SessionManager.getInstance().getUsername();

    public void initialize() {

        //Fetches the user's backlog
        loadBacklogData();

        addBacklogItemBtn.setOnMouseClicked(e -> {
            showAddGameModal();
        });
    }

    private void loadBacklogData() {

        //Shows the loading screen
        if (loadingOverlay != null) {
            loadingOverlay.setVisible(true);
            loadingOverlay.setManaged(true);
        }

        //Clears the UI content
        backlogContainer.getChildren().clear();

        //Starts a new thread for network and database work
        Thread backgroundLoader = new Thread(() -> {

            List<BacklogItem> items = GamesRepo.fetchUserBacklog(username);

            Platform.runLater(() -> {

                if (items != null) {
                    //Populates the TilePane
                    for(BacklogItem item : items) {
                        addGameCard(item.getBacklogId(), item.getGameName(), item.getPlatform(), item.getProgress(), item.getTotalAchievements());
                    }
                }

                //Hides the loading screen after all cards are rendered
                if (loadingOverlay != null) {
                    loadingOverlay.setVisible(false);
                    loadingOverlay.setManaged(false);
                }

                updateEmptyState();
            });
        });

        backgroundLoader.setDaemon(true);
        backgroundLoader.start();
    }

    private void showAddGameModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/add_game_modal.fxml"));

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

                //Retrieves the data
                SearchResult selectedGame = modalController.getSelectedGame();
                String selectedPlatform = modalController.getSelectedPlatform();

                //Extracts required data using model getters
                int rawgId = selectedGame.getRawgId();
                String gameName = selectedGame.getName(); //Uses the clean name field from the model

                //Inserts the backlog item into the database
                boolean success = GamesRepo.addBacklog(
                        username,
                        rawgId,
                        gameName,
                        selectedPlatform
                );

                //Conditional Reload based on insertion success
                if(success){    //The new item is now in the database
                    loadBacklogData();
                    updateEmptyState();
                }else{  //To be changed with an error message in UI level
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    alert.setTitle("Error Adding Game");
                    alert.setHeaderText(null);
                    alert.setContentText("This game and platform combination is already in your backlog, or a database error occurred. Please try again or check your existing list.");
                    alert.showAndWait();
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void addGameCard(int backlog_id, String gameName, String platform, int progress, int totalAchievements) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Components/small-card.fxml"));
            Node card = loader.load();
            SmallCardController cardController = loader.getController();

            cardController.setCardData(backlog_id, gameName, platform, progress, totalAchievements);
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
    }
}