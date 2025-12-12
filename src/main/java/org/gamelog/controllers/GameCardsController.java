package org.gamelog.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.GamesRepo;
import org.gamelog.repository.UserRepo;
import java.io.IOException;
import org.gamelog.utils.ThemeManager;

public class GameCardsController {

    @FXML
    private AnchorPane rootPane;
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
    @FXML
    private Button editItemBtn;

    private Node cardNode;
    private int backlogId;
    private int gid;
    private String platform;
    private Runnable onDeleteAction;
    private Runnable onFavoriteToggle;
    private String callingPageFxml;
    private int achievedCount;
    private int totalAchievements;
    String username = SessionManager.getInstance().getUsername();

    private boolean isDarkMode = false;

    private static final String FILLED_HEART_URL = "/org/gamelog/Assets/filled_heart_icon.png";
    private static final String EMPTY_HEART_LIGHT_URL = "/org/gamelog/Assets/heart_icon.png";
    private static final String EMPTY_HEART_DARK_URL = "/org/gamelog/Assets/favorites_icon.png";

    @FXML
    public void initialize() {
        deleteButton.setOnMouseClicked(e -> {
            boolean success = GamesRepo.removeBacklogItem(backlogId);

            if (success) {
                // Removes the card visually
                if (cardNode != null && cardNode.getParent() != null) {
                    ((Pane) cardNode.getParent()).getChildren().remove(cardNode);
                    showGameDeletionNotification();
                }

                // Triggers the callback to notify Home Page to refresh
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

            // Show notification for favorite action
            showFavoriteNotification(newState);

            if (onFavoriteToggle != null) {
                onFavoriteToggle.run();
            }
        });

        editItemBtn.setOnMouseClicked(e -> {
            openEditItemPage(this.backlogId);
        });
    }

    public void setCallingPageFxml(String fxmlPath) {
        this.callingPageFxml = fxmlPath;
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
            e.printStackTrace();
        }
    }

    public void setIsDarkMode(boolean isDark) {
        this.isDarkMode = isDark;

        String username = SessionManager.getInstance().getUsername();
        if (username != null && this.gid != 0) {
            boolean isFavorite = GamesRepo.isFavorite(username, this.gid, this.platform);
            setFavoriteIcon(isFavorite);
        }

        applyThemeToCard();
    }

    public void setCardNode(Node cardNode) {
        this.cardNode = cardNode;
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

    public void setCardData(int backlog_id,int gid, String title, String platform, int achievements, int totalAchievements) {
        this.backlogId = backlog_id;
        this.gid = gid;
        this.platform = platform;
        this.achievedCount = achievements;
        this.totalAchievements = totalAchievements;

        if (gameTitle != null) gameTitle.setText(title);
        if (platformText != null) platformText.setText(platform);

        updateProgressDisplay(this.achievedCount, this.totalAchievements);

        String username = SessionManager.getInstance().getUsername();
        if (username != null) {
            boolean isFavorite = GamesRepo.isFavorite(username, gid, platform);
            setFavoriteIcon(isFavorite);
        }
    }

    private void openEditItemPage(int backlogId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/edit-item-page.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene editScene = new Scene(loader.load());

            EditItemController controller = loader.getController();
            controller.setBacklogId(backlogId);

            //Passes the previous page path
            if (this.callingPageFxml != null) {
                controller.setPreviousPage(this.callingPageFxml);
            }

            controller.setOnDataUpdated(this::refreshCardProgress);

            stage.setScene(editScene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshCardProgress() {
        Thread refreshThread = new Thread(() -> {
            String username = SessionManager.getInstance().getUsername();

            this.achievedCount = GamesRepo.getAchievedCount(username, this.gid);

            Platform.runLater(() -> {
                updateProgressDisplay(this.achievedCount, this.totalAchievements);
            });
        });

        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    private void updateProgressDisplay(int achieved, int total) {

        if (total <= 0) {
            if (progressText != null) progressText.setText("🏆 No achievements available!");

            if (progressBar != null){
                progressBar.setProgress(1.0);
                progressBar.getStyleClass().remove("progress-bar");
                progressBar.getStyleClass().add("completed-progress-bar");
            }
            return;
        }

        double progress = (double) achieved / total;

        if (progressText != null) progressText.setText("🏆 " + achieved + "/" + total + " Achievements");

        if (progressBar != null) {
            progressBar.setProgress(progress);
            if(progressBar.getProgress() == 1.0){
                progressBar.getStyleClass().remove("progress-bar");
                progressBar.getStyleClass().add("completed-progress-bar");
            }
        }
    }

    private void showGameDeletionNotification() {
        //Checks if Notifications Are Enabled
        if (!UserRepo.isNotificationsEnabled(username)) {
            return;
        }

        String gameName = this.gameTitle != null ? this.gameTitle.getText() : "Unknown Game";
        String platform = this.platformText != null ? this.platformText.getText() : "Unknown Platform";

        try {
            Image iconImage = new Image(getClass().getResourceAsStream("/org/gamelog/Assets/Logo.png"));
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
        //Checks if Notifications Are Enabled
        if (!UserRepo.isNotificationsEnabled(username)) {
            return;
        }

        String gameName = this.gameTitle != null ? this.gameTitle.getText() : "Unknown Game";
        String platform = this.platformText != null ? this.platformText.getText() : "Unknown Platform";

        try {
            Image iconImage = new Image(getClass().getResourceAsStream("/org/gamelog/Assets/Logo.png"));
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