package org.gamelog.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.gamelog.model.BacklogItem;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.GamesRepo;
import org.gamelog.utils.ThemeManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FavoritesPageController {

    @FXML
    private BorderPane rootPane;
    @FXML
    private Button filterFavoritesBtn;
    @FXML
    private Label emptyFavoritesLabel;
    @FXML
    private TilePane favoritesContainer;
    @FXML
    private VBox loadingOverlay;

    private final String username = SessionManager.getInstance().getUsername();
    private String currentSortCriteria = "Newest first";
    private List<BacklogItem> cachedFavoriteItems = new ArrayList<>();

    @FXML
    public void initialize() {
        ThemeManager.applyTheme(rootPane, "Favorites");

        setupFilterMenu();
        loadFavoritesData();
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

            if(option.equals(currentSortCriteria)){
                item.setSelected(true);
            }

            item.setOnAction(e -> {
                currentSortCriteria = option;
                sortAndDisplay();
            });

            sortMenu.getItems().add(item);
        }

        filterFavoritesBtn.setOnMouseClicked(e -> {
            sortMenu.show(filterFavoritesBtn, Side.BOTTOM, 0, 0);
        });
    }

    private void loadFavoritesData() {

        if(loadingOverlay != null){
            loadingOverlay.setVisible(true);
            loadingOverlay.setManaged(true);
        }

        if(emptyFavoritesLabel != null){
            emptyFavoritesLabel.setVisible(false);
            emptyFavoritesLabel.setManaged(false);
        }

        favoritesContainer.getChildren().clear();

        Thread backgroundLoader = new Thread(() -> {
            List<BacklogItem> items = null;
            try {
                items = GamesRepo.fetchUserFavorites(username);
            }catch(Exception e){
                e.printStackTrace();
            }

            List<BacklogItem> finalItems = items;
            Platform.runLater(() -> {
                if(finalItems != null){
                    cachedFavoriteItems = new ArrayList<>(finalItems);
                }else{
                    cachedFavoriteItems = new ArrayList<>();
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
        if (cachedFavoriteItems == null || cachedFavoriteItems.isEmpty()) {
            updateEmptyState();
            return;
        }

        // Sorting Logic
        switch (currentSortCriteria) {
            case "Name (A-Z)":
                cachedFavoriteItems.sort(Comparator.comparing(BacklogItem::getGameName, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Name (Z-A)":
                cachedFavoriteItems.sort(Comparator.comparing(BacklogItem::getGameName, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "Platform (A-Z)":
                cachedFavoriteItems.sort(Comparator.comparing(BacklogItem::getPlatform, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Platform (Z-A)":
                cachedFavoriteItems.sort(Comparator.comparing(BacklogItem::getPlatform, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "Newest first":
                cachedFavoriteItems.sort(Comparator.comparingInt(BacklogItem::getBacklogId).reversed());
                break;
            case "Oldest first":
                cachedFavoriteItems.sort(Comparator.comparingInt(BacklogItem::getBacklogId));
                break;
            case "Progress (0% - 100%)":
                cachedFavoriteItems.sort(Comparator.comparingInt(BacklogItem::getProgress));
                break;
            case "Progress (100% - 0%)":
                cachedFavoriteItems.sort(Comparator.comparingInt(BacklogItem::getProgress).reversed());
                break;
            default:
                break;
        }

        favoritesContainer.getChildren().clear();

        //Populates the container
        for (BacklogItem item : cachedFavoriteItems) {
            addFavoriteCard(item.getBacklogId(), item.getGid(), item.getGameName(), item.getPlatform(), item.getProgress(), item.getTotalAchievements());
        }

        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean empty = favoritesContainer.getChildren().isEmpty();
        if(emptyFavoritesLabel != null) {
            emptyFavoritesLabel.setVisible(empty);
            emptyFavoritesLabel.setManaged(empty);
        }
    }

    private void addFavoriteCard(int backlog_id, int gid, String gameName, String platform, int progress, int totalAchievements) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Components/game_cards.fxml"));
            Node card = loader.load();
            GameCardsController cardController = loader.getController();

            boolean isDark = SessionManager.getInstance().isDarkMode();
            cardController.setIsDarkMode(isDark);

            cardController.setCallingPageFxml("/org/gamelog/Pages/favorites-page.fxml");
            cardController.setCardData(backlog_id, gid, gameName, platform, progress, totalAchievements);
            cardController.setCardNode(card);

            cardController.setOnDeleteAction(() -> {
                loadFavoritesData();
            });

            cardController.setOnFavoriteToggle(() -> {
                loadFavoritesData();
            });

            favoritesContainer.getChildren().add(card);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}