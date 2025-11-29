package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;

public class SmallCardController {

    @FXML
    private Button deleteButton;
    @FXML
    private Label gameTitle;
    @FXML
    private Label platformText;
    @FXML
    private Label progressText;
    @FXML
    private ProgressBar progressBar;

    private Node cardNode;

    @FXML
    public void initialize() {
        deleteButton.setOnAction(e -> {
            if (cardNode != null && cardNode.getParent() != null) {
                ((Pane) cardNode.getParent()).getChildren().remove(cardNode);
            }
        });
    }

    public void setCardNode(Node cardNode) {
        this.cardNode = cardNode;
    }

    public void setCardData(String title, String platform, int achievements, int totalAchievements) {
        if (gameTitle != null) gameTitle.setText(title);
        if (platformText != null) platformText.setText(platform);
        if (progressText != null) progressText.setText("🏆 " + achievements + "/" + totalAchievements + " Achievements");
        if (progressBar != null) progressBar.setProgress((double) achievements / totalAchievements);
    }
}