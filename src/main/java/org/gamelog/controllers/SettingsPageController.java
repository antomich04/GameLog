package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;

public class SettingsPageController {

    @FXML
    private Button homeButton;
    @FXML
    private Button backlogButton;
    @FXML
    private Button favoritesButton;
    @FXML
    private Button logoutButton;
    @FXML
    private HBox accountSection;

    public void initialize() {

        homeButton.setOnMouseClicked(event -> {
            handleHomeButton();
        });

        backlogButton.setOnMouseClicked(event -> {
            handleBacklogButton();
        });

        favoritesButton.setOnMouseClicked(event -> {
            handleFavoritesButton();
        });

        logoutButton.setOnMouseClicked(event -> {
            handleLogoutButton();
        });

        accountSection.setOnMouseClicked(event -> {
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

    private void handleHomeButton() {
        try {
            Parent homePage = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/home-page.fxml"));
            switchScene(homePage, "GameLog - Home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleBacklogButton() {
        try {
            Parent backlogPage = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/backlog-page.fxml"));
            switchScene(backlogPage, "GameLog - Backlog");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleFavoritesButton() {
        try {
            Parent favoritesPage = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/favorites-page.fxml"));
            switchScene(favoritesPage, "GameLog - Favorites");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogoutButton() {
        try {
            Parent loginPage = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/login-page.fxml"));
            switchScene(loginPage, "GameLog - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchScene(Parent root, String title) {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
}