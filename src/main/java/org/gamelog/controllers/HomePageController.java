package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import org.gamelog.model.SessionManager;

public class HomePageController {

    @FXML
    private Label iconLetter;
    @FXML
    private Label welcomeLabel;
    @FXML
    private VBox cardsContainer;

    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        if (session != null) {
            String username = session.getUsername();
            welcomeLabel.setText("Welcome back, " + username);
            iconLetter.setText(String.valueOf(Character.toUpperCase(username.charAt(0))));
        }
        addCards();
    }

    private void addCards() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Components/small-card.fxml"));
            Pane card1 = loader.load();
            card1.getStyleClass().add("xlarge");

            loader = new FXMLLoader(getClass().getResource("/org/gamelog/Components/small-card.fxml"));
            Pane card2 = loader.load();
            card2.getStyleClass().add("xlarge");

            cardsContainer.getChildren().addAll(card1, card2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}