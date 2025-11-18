package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.gamelog.model.SessionManager;
import java.io.IOException;

public class NavigationBarController {

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

    public void initialize() {
        homeButton.setOnMouseClicked(event -> handleHomeButton());
        backlogButton.setOnMouseClicked(event -> handleBacklogButton());
        favoritesButton.setOnMouseClicked(event -> handleFavoritesButton());
        settingsButton.setOnMouseClicked(event -> handleSettingsButton());
        logoutButton.setOnMouseClicked(event -> handleLogoutButton());
    }

    private void handleHomeButton() {
        navigateToPage("/org/gamelog/Pages/home-page.fxml", "GameLog - Home");
    }

    private void handleBacklogButton() {
        navigateToPage("/org/gamelog/Pages/backlog-page.fxml", "GameLog - Backlog");
    }

    private void handleFavoritesButton() {
        navigateToPage("/org/gamelog/Pages/favorites-page.fxml", "GameLog - Favorites");
    }

    private void handleSettingsButton() {
        navigateToPage("/org/gamelog/Pages/settings-page.fxml", "GameLog - Settings");
    }

    private void handleLogoutButton() {
        SessionManager.clearSession();
        navigateToPage("/org/gamelog/Pages/login-page.fxml", "GameLog - Login");
    }

    private void navigateToPage(String fxmlPath, String title) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) homeButton.getScene().getWindow();
            Scene scene = new Scene(page);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}