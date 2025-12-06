package org.gamelog.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.gamelog.model.BacklogItem;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.GamesRepo;
import org.gamelog.utils.ThemeManager;

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
    @FXML
    private VBox emptyStateContainer;

    public void initialize() {
        // 1. APPLY THEME
        // This page handles its own theme application upon loading.
        ThemeManager.applyTheme(rootPane, "Home");

        SessionManager session = SessionManager.getInstance();
        if (session != null) {
            String username = session.getUsername();
            welcomeLabel.setText("Welcome back, " + username);
            iconLetter.setText(String.valueOf(Character.toUpperCase(username.charAt(0))));
        }

        loadLatestGames();

        iconLetter.setOnMouseClicked(e -> {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/settings-page.fxml"));
                Parent root = loader.load();

                // Note: We do NOT need to apply the theme here manually.
                // The SettingsPageController's initialize() method will handle it.

                Stage stage = (Stage) rootPane.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        });
    }

    private void loadLatestGames() {
        // Hide empty state initially
        if(emptyStateContainer != null){
            emptyStateContainer.setVisible(false);
            emptyStateContainer.setManaged(false);
        }

        // Show Loading Overlay
        if(loadingOverlay != null){
            loadingOverlay.setVisible(true);
            loadingOverlay.setManaged(true);
        }

        cardsContainer.getChildren().clear();

        // Starts a new thread for network and database work
        Thread backgroundLoader = new Thread(() -> {
            List<BacklogItem> latestItems = null;
            try {
                latestItems = GamesRepo.fetchLatestBacklogItems(SessionManager.getInstance().getUsername());
            } catch(Exception e){
                e.printStackTrace();
            }

            // Updates the UI
            List<BacklogItem> finalLatestItems = latestItems;
            Platform.runLater(() -> {
                boolean isEmpty = true;

                if(finalLatestItems != null && !finalLatestItems.isEmpty()){
                    isEmpty = false;
                    // Populates the UI with cards
                    for(BacklogItem item : finalLatestItems){
                        addCardForHome(item);
                    }
                }

                // Hide Loading Overlay
                if(loadingOverlay != null){
                    loadingOverlay.setVisible(false);
                    loadingOverlay.setManaged(false);
                }

                // Update visibility for the empty state
                updateEmptyState(isEmpty);
            });
        });

        backgroundLoader.setDaemon(true);
        backgroundLoader.start();
    }

    private void updateEmptyState(boolean isEmpty) {
        if (emptyStateContainer != null) {
            emptyStateContainer.setVisible(isEmpty);
            emptyStateContainer.setManaged(isEmpty);
        }

        if (cardsContainer != null) {
            cardsContainer.setVisible(!isEmpty);
            cardsContainer.setManaged(!isEmpty);
        }
    }

    private void addCardForHome(BacklogItem item) {
        try {
            // Updated path to match the actual file name "game_cards.fxml"
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Components/game_cards.fxml"));
            Pane card = loader.load();
            GameCardsController cardController = loader.getController();

            // CRITICAL: Pass Theme State to the card
            // GameCards are dynamic components, so we must tell them the current theme state
            // so they can decide between the White border heart (Dark Mode) or Black border heart (Light Mode).
            boolean isDark = SessionManager.getInstance().isDarkMode();
            cardController.setIsDarkMode(isDark);

            cardController.setCardData(
                    item.getBacklogId(),
                    item.getGid(),
                    item.getGameName(),
                    item.getPlatform(),
                    item.getProgress(),
                    item.getTotalAchievements()
            );

            cardController.setOnDeleteAction(() -> {
                loadLatestGames();
            });

            cardController.setCardNode(card);
            card.getStyleClass().add("xlarge"); // Retains styling for the home page layout

            cardsContainer.getChildren().add(card);

        } catch(IOException e){
            e.printStackTrace();
        }
    }
}