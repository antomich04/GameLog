package org.gamelog.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
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
import java.util.ArrayList;
import java.util.Comparator;
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
    private List<BacklogItem> cachedBacklogItems = new ArrayList<>();   //Used for sorting
    private String currentSortCriteria = "Newest first";

    public void initialize() {

        //Initializes the filtering options
        setupFilterMenu();

        //Fetches the user's backlog
        loadBacklogData();

        addBacklogItemBtn.setOnMouseClicked(e -> {
            showAddGameModal();
        });
    }

    private void setupFilterMenu() {
        ContextMenu sortMenu = new ContextMenu();
        ToggleGroup sortGroup = new ToggleGroup();

        String[] options = {
                "Newest first",
                "Oldest first",
                "Name (A-Z)",
                "Name (Z-A)",
                "Platform (A-Z)",
                "Platform (Z-A)",
                "Progress (0% - 100%)",
                "Progress (100% - 0%)"
        };

        for (String option : options) {
            RadioMenuItem item = new RadioMenuItem(option);
            item.setToggleGroup(sortGroup);

            //Sets the checkmark for the default item
            if(option.equals(currentSortCriteria)){
                item.setSelected(true);
            }

            //Triggers sort when clicked
            item.setOnAction(e -> {
                currentSortCriteria = option;
                sortAndDisplay();
            });

            sortMenu.getItems().add(item);
        }

        //Shows the menu when the filter button is clicked
        filterBacklogItemsBtn.setOnMouseClicked(e -> {
            sortMenu.show(filterBacklogItemsBtn, Side.BOTTOM, 0, 0);
        });
    }

    private void loadBacklogData() {

        if(loadingOverlay != null){
            loadingOverlay.setVisible(true);
            loadingOverlay.setManaged(true);
        }

        if(backlogEmptyLabel != null){
            backlogEmptyLabel.setVisible(false);
            backlogEmptyLabel.setManaged(false);
        }

        backlogContainer.getChildren().clear();

        Thread backgroundLoader = new Thread(() -> {
            List<BacklogItem> items = null;
            try {
                items = GamesRepo.fetchUserBacklog(username);
            }catch(Exception e){
                e.printStackTrace();
            }

            List<BacklogItem> finalItems = items;
            Platform.runLater(() -> {
                //Updates the cache with fresh data
                if(finalItems != null){
                    cachedBacklogItems = new ArrayList<>(finalItems);
                }else{
                    cachedBacklogItems = new ArrayList<>();
                }

                sortAndDisplay();

                if(loadingOverlay != null){
                    loadingOverlay.setVisible(false);
                    loadingOverlay.setManaged(false);
                }
            });
        });

        backgroundLoader.setDaemon(true);
        backgroundLoader.start();
    }

    private void sortAndDisplay() {
        //Safety check
        if (cachedBacklogItems == null || cachedBacklogItems.isEmpty()) {
            updateEmptyState();
            return;
        }


        //Sorting Logic
        switch (currentSortCriteria) {
            case "Name (A-Z)":
                cachedBacklogItems.sort(Comparator.comparing(BacklogItem::getGameName, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Name (Z-A)":
                cachedBacklogItems.sort(Comparator.comparing(BacklogItem::getGameName, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "Platform (A-Z)":
                cachedBacklogItems.sort(Comparator.comparing(BacklogItem::getPlatform, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Platform (Z-A)":
                cachedBacklogItems.sort(Comparator.comparing(BacklogItem::getPlatform, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "Newest first":
                cachedBacklogItems.sort(Comparator.comparingInt(BacklogItem::getBacklogId).reversed());
                break;
            case "Oldest first":
                cachedBacklogItems.sort(Comparator.comparingInt(BacklogItem::getBacklogId));
                break;
            case "Progress (0% - 100%)":
                cachedBacklogItems.sort(Comparator.comparingInt(BacklogItem::getProgress));
                break;
            case "Progress (100% - 0%)":
                cachedBacklogItems.sort(Comparator.comparingInt(BacklogItem::getProgress).reversed());
                break;
            default:
                break;
        }

        //Clears the container visually
        backlogContainer.getChildren().clear();

        //Re-populates the container with the sorted list
        for (BacklogItem item : cachedBacklogItems) {
            addGameCard(item.getBacklogId(), item.getGameName(), item.getPlatform(), item.getProgress(), item.getTotalAchievements());
        }

        updateEmptyState();
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