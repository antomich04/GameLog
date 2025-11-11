package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

    @FXML
    public void initialize() {
        System.out.println("SettingsController initialized!");
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
        System.out.println("Account section clicked");
        try {
            Parent accountPage = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/account-settings-page.fxml"));
            switchScene(accountPage, "GameLog - Account");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load account page.");
        }
    }
    private void handleHomeButton() {
        System.out.println("Home button clicked");
        try {
            Parent homePage = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/home-page.fxml"));
            switchScene(homePage, "GameLog - Home");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load home page.");
        }
    }
    private void handleBacklogButton() {
        System.out.println("Backlog button clicked");
        try {
            Parent backlogPage = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/backlog-page.fxml"));
            switchScene(backlogPage, "GameLog - Backlog");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load backlog page.");
        }
    }
    private void handleFavoritesButton() {
        System.out.println("Favorites button clicked");
        try {
            Parent favoritesPage = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/favorites-page.fxml"));
            switchScene(favoritesPage, "GameLog - Favorites");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load favorites page.");
        }
    }
    private void handleLogoutButton() {
        System.out.println("Logout button clicked");
        try {
            Parent loginPage = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/login-page.fxml"));
            switchScene(loginPage, "GameLog - Login");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load login page.");
        }
    }
    private void switchScene(Parent root, String title) {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}