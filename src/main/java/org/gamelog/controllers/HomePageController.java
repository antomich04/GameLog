package org.gamelog.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.gamelog.model.BacklogItem;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.GamesRepo;
import java.io.IOException;
import java.util.List;

public class HomePageController {

    @FXML
    public Label emptyStateLabel;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Label iconLetter;
    @FXML
    private Label welcomeLabel;
    @FXML
    private VBox cardsContainer;
    @FXML
    private VBox loadingOverlay;

    public void initialize() {

        SessionManager session = SessionManager.getInstance();
        if (session != null) {
            String username = session.getUsername();
            welcomeLabel.setText("Welcome back, " + username);
            iconLetter.setText(String.valueOf(Character.toUpperCase(username.charAt(0))));
        }

        loadLatestGames();

        iconLetter.setOnMouseClicked(e -> {
            try{
                Stage stage = (Stage) rootPane.getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/settings-page.fxml")));
                stage.setScene(scene);
                stage.show();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        });
    }

    private void loadLatestGames() {

        //Shows Loading Overlay
        if(loadingOverlay != null){
            loadingOverlay.setVisible(true);
            loadingOverlay.setManaged(true);
        }

        cardsContainer.getChildren().clear();

        //Starts a new thread for network and database work
        Thread backgroundLoader = new Thread(() -> {

            List<BacklogItem> latestItems = null;
            try {
                latestItems = GamesRepo.fetchLatestBacklogItems(SessionManager.getInstance().getUsername());
            }catch(Exception e){
                e.printStackTrace();
            }

            //Updates the UI
            List<BacklogItem> finalLatestItems = latestItems;
            Platform.runLater(() -> {

                boolean isEmpty = true;

                if(finalLatestItems != null && !finalLatestItems.isEmpty()){
                    isEmpty = false;
                    //Populates the UI with cards
                    for(BacklogItem item : finalLatestItems){
                        addCardForHome(item);
                    }
                }

                //Updates visibility for the empty state
                updateEmptyState(isEmpty);

                //Hides Loading Overlay
                if(loadingOverlay != null){
                    loadingOverlay.setVisible(false);
                    loadingOverlay.setManaged(false);
                }
            });
        });

        backgroundLoader.setDaemon(true);
        backgroundLoader.start();
    }

    private void updateEmptyState(boolean isEmpty) {

        if (emptyStateLabel != null) {
            emptyStateLabel.setVisible(isEmpty);
            emptyStateLabel.setManaged(isEmpty);
        }

        if (cardsContainer != null) {
            cardsContainer.setVisible(!isEmpty);
            cardsContainer.setManaged(!isEmpty);
        }
    }

    private void addCardForHome(BacklogItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Components/small-card.fxml"));
            Pane card = loader.load();
            SmallCardController cardController = loader.getController();

            cardController.setCardData(
                    item.getBacklogId(),
                    item.getGameName(),
                    item.getPlatform(),
                    item.getProgress(),
                    item.getTotalAchievements()
            );

            cardController.setOnDeleteAction(() -> {
                //When a card is deleted, reloads the list to fetch the next latest game
                loadLatestGames();
            });

            cardController.setCardNode(card);
            card.getStyleClass().add("xlarge"); //Retains styling

            cardsContainer.getChildren().add(card);

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}