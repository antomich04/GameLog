package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;

public class SettingsPageController {

    @FXML
    private HBox accountClickableArea;

    public void initialize() {
        accountClickableArea.setOnMouseClicked(event -> {
            handleAccountClick();
        });
    }

    private void handleAccountClick() {
        try {
            Parent accountPage = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/account-settings-page.fxml"));
            switchScene(accountPage, "GameLog - Account");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchScene(Parent root, String title) {
        Stage stage = (Stage) accountClickableArea.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
}