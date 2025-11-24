package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;

public class BackLogPageController {

    @FXML
    private Button addBacklogItemBtn;
    @FXML
    private Button filterBacklogItemsBtn;
    @FXML
    private Label backlogEmptyLabel;
    @FXML
    private TilePane backlogContainer;

    @FXML
    public void initialize() {

        backlogEmptyLabel.setText("No games in your backlog!");

        //Used to check whether the backlog container is empty
        updateEmptyState();

        //ONLY for testing, to be removed later
        addFakeCard();

        addBacklogItemBtn.setOnMouseClicked(e -> {

            //ONLY for testing, to be removed later
            if (!backlogContainer.getChildren().isEmpty()) {
                backlogContainer.getChildren().remove(0); // remove the fake card
                updateEmptyState();
            }
        });

        filterBacklogItemsBtn.setOnMouseClicked(e -> {

            //ONLY for testing, to be removed later
            addFakeCard();
        });
    }

    private void updateEmptyState() {
        boolean empty = backlogContainer.getChildren().isEmpty();
        backlogEmptyLabel.setVisible(empty);
        backlogEmptyLabel.setManaged(empty);
    }

    private void addFakeCard() {
        Label fakeCard = new Label("Fake Card");
        fakeCard.setStyle("-fx-background-color: #dddddd; -fx-padding: 20; "
                + "-fx-background-radius: 8; -fx-border-color: black;");

        fakeCard.setPrefSize(300, 180); // simulate a card size

        backlogContainer.getChildren().add(fakeCard);

        updateEmptyState();
    }

}