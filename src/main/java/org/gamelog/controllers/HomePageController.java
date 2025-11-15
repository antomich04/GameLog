package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.gamelog.model.SessionManager;
import java.io.IOException;

public class HomePageController {

    @FXML
    private Button homeButton;
    @FXML
    private Button backlogButton;
    @FXML
    private Button favoritesButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label iconLetter;
    @FXML
    private Label welcomeLabel;

    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        if (session != null) {
            String username = session.getUsername();
            welcomeLabel.setText("Welcome back, " + username);
            iconLetter.setText(String.valueOf(Character.toUpperCase(username.charAt(0))));
        }

        backlogButton.setOnMouseClicked(event -> {
            handleBacklogButton();
        });
        favoritesButton.setOnMouseClicked(event -> {
            handleFavoritesButton();
        });
        settingsButton.setOnMouseClicked(event -> {
            handleSettingsButton();
        });
        logoutButton.setOnMouseClicked(event -> {
            handleLogoutButton();
        });
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

    private void handleSettingsButton() {
        try {
            Parent settingsPage = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/settings-page.fxml"));
            switchScene(settingsPage, "GameLog - Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogoutButton() {
        SessionManager.clearSession();
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
