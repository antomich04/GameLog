package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.gamelog.model.SessionManager;
import org.gamelog.utils.ThemeManager;
import java.io.IOException;

public class NavigationBarController {

    @FXML
    private VBox navMenu;
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
    public void initialize() {
        ThemeManager.applyTheme(navMenu, "NavBar");

        homeButton.setOnMouseClicked(e -> navigateTo("/org/gamelog/Pages/home-page.fxml", "Home"));
        backlogButton.setOnMouseClicked(e -> navigateTo("/org/gamelog/Pages/backlog-page.fxml", "Backlog"));
        favoritesButton.setOnMouseClicked(e -> navigateTo("/org/gamelog/Pages/favorites-page.fxml", "Favorites"));
        settingsButton.setOnMouseClicked(e -> navigateTo("/org/gamelog/Pages/settings-page.fxml", "Settings"));
        logoutButton.setOnMouseClicked(e -> handleLogout());
    }

    private void navigateTo(String fxmlPath, String themeKey) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            //Apply Theme
            ThemeManager.applyTheme(root, themeKey);

            Stage stage = (Stage) navMenu.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogout() {
        SessionManager.clearSession();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/login-page.fxml"));
            Stage stage = (Stage) navMenu.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}