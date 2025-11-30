package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.GamesRepo;

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

            if (success) {
                //Removes the card visually
                if (cardNode != null && cardNode.getParent() != null) {
                    ((Pane) cardNode.getParent()).getChildren().remove(cardNode);
                }

                //Triggers the callback to notify Home Page to refresh
                if (onDeleteAction != null) {
                    onDeleteAction.run();
                }
            }
        });

        addToFavoritesBtn.setOnMouseClicked(e -> {
            String username = SessionManager.getInstance().getUsername();

            boolean newState = GamesRepo.toggleFavorite(username, this.gid, this.platform);

            setFavoriteIcon(newState);

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
}