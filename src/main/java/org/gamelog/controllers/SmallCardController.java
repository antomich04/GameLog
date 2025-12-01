package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

public class SmallCardController {

    @FXML
    private Button deleteButton;
    @FXML
    private Button addToFavoritesBtn;
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

    //Used for different favorite states
    private static final String FILLED_HEART_URL = "/org/gamelog/Assets/filled_heart_icon.png";
    private static final String EMPTY_HEART_URL = "/org/gamelog/Assets/heart_icon.png";

    @FXML
    public void initialize() {
        deleteButton.setOnMouseClicked(e -> {
            boolean success = GamesRepo.removeBacklogItem(backlogId);
            SessionManager sessionManager = SessionManager.getInstance();
            String username = sessionManager.getUsername();

            if (success) {
                // Removes the card visually
                if (cardNode != null && cardNode.getParent() != null) {
                    ((Pane) cardNode.getParent()).getChildren().remove(cardNode);
                    showGameDeletionNotification(); // Notification Call
                }

                // Triggers the callback to notify Home Page to refresh
                if (onDeleteAction != null) {
                    onDeleteAction.run();
                }
            }
        });

        addToFavoritesBtn.setOnMouseClicked(e -> {
            String username = SessionManager.getInstance().getUsername();

            boolean newState = GamesRepo.toggleFavorite(username, this.gid, this.platform);

            setFavoriteIcon(newState);

            // Show notification for favorite action
            showFavoriteNotification(newState);

            if (onFavoriteToggle != null) {
                onFavoriteToggle.run();
            }
        });
    }

    private void setFavoriteIcon(boolean isFavorite) {
        ImageView iconView = (ImageView) addToFavoritesBtn.getGraphic();
        String url = isFavorite ? FILLED_HEART_URL : EMPTY_HEART_URL;
        iconView.setImage(new Image(getClass().getResourceAsStream(url)));
    }

    public void setCardNode(Node cardNode) {
        this.cardNode = cardNode;
    }

    public void setOnDeleteAction(Runnable onDeleteAction) {
        this.onDeleteAction = onDeleteAction;
    }

    public void setOnFavoriteToggle(Runnable onFavoriteToggle) {
        this.onFavoriteToggle = onFavoriteToggle;
    }

    public void setCardData(int backlog_id,int gid, String title, String platform, int achievements, int totalAchievements) {
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
        //Check if Notifications Are Enabled
        String username = SessionManager.getInstance().getUsername();
        if (!UserRepo.isNotificationsEnabled(username)) {
            return;
        }

        String gameName = this.gameTitle != null ? this.gameTitle.getText() : "Unknown Game";
        String platform = this.platformText != null ? this.platformText.getText() : "Unknown Platform";

        try {
            Image iconImage = new Image(Main.class.getResourceAsStream("/org/gamelog/Assets/Logo.png"));
            ImageView iconView = new ImageView(iconImage);
            iconView.setFitHeight(90);
            iconView.setFitWidth(120);

            Notifications.create()
                    .title("Game Removed")
                    .text("\"" + gameName + "\" removed from your backlog (" + platform + ")")
                    .graphic(iconView)
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .show();
        } catch (Exception e) {
            Notifications.create()
                    .title("Game Removed")
                    .text("\"" + gameName + "\" removed from your backlog (" + platform + ")")
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .show();
        }
    }

    private void showFavoriteNotification(boolean addedToFavorites) {
        //Check if Notifications Are Enabled
        String username = SessionManager.getInstance().getUsername();
        if (!UserRepo.isNotificationsEnabled(username)) {
            return;
        }

        String gameName = this.gameTitle != null ? this.gameTitle.getText() : "Unknown Game";
        String platform = this.platformText != null ? this.platformText.getText() : "Unknown Platform";

        try {
            Image iconImage = new Image(Main.class.getResourceAsStream("/org/gamelog/Assets/Logo.png"));
            ImageView iconView = new ImageView(iconImage);
            iconView.setFitHeight(90);
            iconView.setFitWidth(120);

            if (addedToFavorites) {
                Notifications.create()
                        .title("Added to Favorites")
                        .text("\"" + gameName + "\" added to favorites (" + platform + ")")
                        .graphic(iconView)
                        .position(Pos.BOTTOM_RIGHT)
                        .hideAfter(Duration.seconds(5))
                        .show();
            } else {
                Notifications.create()
                        .title("Removed from Favorites")
                        .text("\"" + gameName + "\" removed from favorites (" + platform + ")")
                        .graphic(iconView)
                        .position(Pos.BOTTOM_RIGHT)
                        .hideAfter(Duration.seconds(5))
                        .show();
            }
        } catch (Exception e) {
            if (addedToFavorites) {
                Notifications.create()
                        .title("Added to Favorites")
                        .text("\"" + gameName + "\" added to favorites (" + platform + ")")
                        .position(Pos.BOTTOM_RIGHT)
                        .hideAfter(Duration.seconds(5))
                        .show();
            } else {
                Notifications.create()
                        .title("Removed from Favorites")
                        .text("\"" + gameName + "\" removed from favorites (" + platform + ")")
                        .position(Pos.BOTTOM_RIGHT)
                        .hideAfter(Duration.seconds(5))
                        .show();
            }
        }
    }

}