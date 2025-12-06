package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent; // Import Parent
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.gamelog.Main;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.GamesRepo;
import org.gamelog.repository.UserRepo;
import org.gamelog.utils.ThemeManager; // Import ThemeManager

public class GameCardsController {

    @FXML
    private Button deleteButton;
    @FXML
    private Button addToFavoritesBtn;
    @FXML
    private ImageView heartIconView;
    @FXML
    private Label gameTitle;
    @FXML
    private Label platformText;
    @FXML
    private Label progressText;
    @FXML
    private ProgressBar progressBar;

    private Node cardNode;
    private int backlogId;
    private int gid;
    private String platform;
    private Runnable onDeleteAction;
    private Runnable onFavoriteToggle;

    // Default to false (Light) to avoid flash of dark content
    private boolean isDarkMode = false;

    // --- DEFINED IMAGE PATHS ---
    private static final String FILLED_HEART_URL = "/org/gamelog/Assets/filled_heart_icon.png";
    private static final String EMPTY_HEART_LIGHT_URL = "/org/gamelog/Assets/heart_icon.png";
    private static final String EMPTY_HEART_DARK_URL = "/org/gamelog/Assets/favorites_icon.png";

    @FXML
    public void initialize() {
        deleteButton.setOnMouseClicked(e -> {
            boolean success = GamesRepo.removeBacklogItem(backlogId);

            if (success) {
                if (cardNode != null && cardNode.getParent() != null) {
                    ((Pane) cardNode.getParent()).getChildren().remove(cardNode);
                    showGameDeletionNotification();
                }
                if (onDeleteAction != null) {
                    onDeleteAction.run();
                }
            }
        });

        addToFavoritesBtn.setOnMouseClicked(e -> {
            String username = SessionManager.getInstance().getUsername();

            // Toggle state in DB
            boolean newState = GamesRepo.toggleFavorite(username, this.gid, this.platform);

            // Update Icon
            setFavoriteIcon(newState);

            // Notification
            showFavoriteNotification(newState);

            if (onFavoriteToggle != null) {
                onFavoriteToggle.run();
            }
        });
    }

    private void setFavoriteIcon(boolean isFavorite) {
        String url;

        if (isFavorite) {
            url = FILLED_HEART_URL;
        } else {
            if (isDarkMode) {
                url = EMPTY_HEART_DARK_URL;
            } else {
                url = EMPTY_HEART_LIGHT_URL;
            }
        }

        try {
            Image newImage = new Image(getClass().getResourceAsStream(url));
            if (heartIconView != null) {
                heartIconView.setImage(newImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading image asset: " + url);
        }
    }

    public void setIsDarkMode(boolean isDark) {
        this.isDarkMode = isDark;

        String username = SessionManager.getInstance().getUsername();
        if (username != null && this.gid != 0) {
            boolean isFavorite = GamesRepo.isFavorite(username, this.gid, this.platform);
            setFavoriteIcon(isFavorite);
        }

        // 2. Update the CSS for the card itself
        applyThemeToCard();
    }

    public void setCardNode(Node cardNode) {
        this.cardNode = cardNode;
        // Apply the theme
        applyThemeToCard();
    }

    private void applyThemeToCard() {
        if (cardNode != null && cardNode instanceof Parent) {
            ThemeManager.applyTheme((Parent) cardNode, "GameCard");
        }
    }

    public void setOnDeleteAction(Runnable onDeleteAction) {
        this.onDeleteAction = onDeleteAction;
    }

    public void setOnFavoriteToggle(Runnable onFavoriteToggle) {
        this.onFavoriteToggle = onFavoriteToggle;
    }

    public void setCardData(int backlog_id, int gid, String title, String platform, int achievements, int totalAchievements) {
        this.backlogId = backlog_id;
        this.gid = gid;
        this.platform = platform;

        if (gameTitle != null) gameTitle.setText(title);
        if (platformText != null) platformText.setText(platform);
        if (progressText != null) progressText.setText("🏆 " + achievements + "/" + totalAchievements + " Achievements");
        if (progressBar != null) progressBar.setProgress((double) achievements / totalAchievements);

        String username = SessionManager.getInstance().getUsername();
        if (username != null) {
            boolean isFavorite = GamesRepo.isFavorite(username, gid, platform);
            setFavoriteIcon(isFavorite);
        }
    }

    private void showGameDeletionNotification() {
        String username = SessionManager.getInstance().getUsername();
        if (!UserRepo.isNotificationsEnabled(username)) {
            return;
        }

        String gameName = this.gameTitle != null ? this.gameTitle.getText() : "Unknown Game";
        String platformStr = this.platformText != null ? this.platformText.getText() : "Unknown Platform";

        try {
            Image iconImage = new Image(Main.class.getResourceAsStream("/org/gamelog/Assets/logo.png"));
            ImageView iconView = new ImageView(iconImage);
            iconView.setFitHeight(90);
            iconView.setFitWidth(120);

            Notifications.create()
                    .title("Game Removed")
                    .text("\"" + gameName + "\" removed from your backlog (" + platformStr + ")")
                    .graphic(iconView)
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .show();
        } catch (Exception e) {
            Notifications.create()
                    .title("Game Removed")
                    .text("\"" + gameName + "\" removed from your backlog (" + platformStr + ")")
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .show();
        }
    }

    private void showFavoriteNotification(boolean addedToFavorites) {
        String username = SessionManager.getInstance().getUsername();
        if (!UserRepo.isNotificationsEnabled(username)) {
            return;
        }

        String gameName = this.gameTitle != null ? this.gameTitle.getText() : "Unknown Game";
        String platformStr = this.platformText != null ? this.platformText.getText() : "Unknown Platform";

        try {
            Image iconImage = new Image(Main.class.getResourceAsStream("/org/gamelog/Assets/logo.png"));
            ImageView iconView = new ImageView(iconImage);
            iconView.setFitHeight(90);
            iconView.setFitWidth(120);

            if (addedToFavorites) {
                Notifications.create()
                        .title("Added to Favorites")
                        .text("\"" + gameName + "\" added to favorites (" + platformStr + ")")
                        .graphic(iconView)
                        .position(Pos.BOTTOM_RIGHT)
                        .hideAfter(Duration.seconds(5))
                        .show();
            } else {
                Notifications.create()
                        .title("Removed from Favorites")
                        .text("\"" + gameName + "\" removed from favorites (" + platformStr + ")")
                        .graphic(iconView)
                        .position(Pos.BOTTOM_RIGHT)
                        .hideAfter(Duration.seconds(5))
                        .show();
            }
        } catch (Exception e) {
            if (addedToFavorites) {
                Notifications.create()
                        .title("Added to Favorites")
                        .text("\"" + gameName + "\" added to favorites (" + platformStr + ")")
                        .position(Pos.BOTTOM_RIGHT)
                        .hideAfter(Duration.seconds(5))
                        .show();
            } else {
                Notifications.create()
                        .title("Removed from Favorites")
                        .text("\"" + gameName + "\" removed from favorites (" + platformStr + ")")
                        .position(Pos.BOTTOM_RIGHT)
                        .hideAfter(Duration.seconds(5))
                        .show();
            }
        }
    }
}