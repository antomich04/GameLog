package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddGameModalController implements Initializable {

    @FXML
    private TextField gameNameField;
    @FXML
    private ComboBox<String> platformComboBox;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addGameButton;

    private Stage stage;
    private String selectedGameName;
    private String selectedPlatform;
    private boolean confirmed = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        platformComboBox.getItems().addAll("PS5", "XBOX", "PC");

        cancelButton.setOnMouseClicked(event -> {
            confirmed = false;
            stage.close();
        });

        addGameButton.setOnMouseClicked(event -> {
            if (isInputValid()) {
                selectedGameName = gameNameField.getText().trim();
                selectedPlatform = platformComboBox.getValue();
                confirmed = true;
                stage.close();
            }
        });
    }


    private boolean isInputValid() {
        if (gameNameField.getText() == null || gameNameField.getText().trim().isEmpty()) {
            gameNameField.setStyle("-fx-border-color: red;");
            return false;
        }

        if (platformComboBox.getValue() == null) {
            platformComboBox.setStyle("-fx-border-color: red;");
            return false;
        }

        gameNameField.setStyle("");
        platformComboBox.setStyle("");
        return true;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getSelectedGameName() {
        return selectedGameName;
    }

    public String getSelectedPlatform() {
        return selectedPlatform;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}