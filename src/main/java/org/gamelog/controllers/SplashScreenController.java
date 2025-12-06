package org.gamelog.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.gamelog.model.Session;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.SessionRepo;
import org.gamelog.repository.UserRepo; // Added Import
import org.gamelog.utils.DeviceUtils;
import org.gamelog.utils.ThemeManager; // Added Import

import java.io.IOException;

public class SplashScreenController {

    @FXML
    private AnchorPane rootPane;

    private FXMLLoader loader;
    private String nextThemeKey;

    @FXML
    public void initialize() {
        SessionRepo repo = new SessionRepo();

        String deviceId = DeviceUtils.getDeviceId();
        Session active = repo.getActiveSession(deviceId);

        if (active != null) {
            SessionManager.createSessionFromExisting(active.getUsername(), active.getSessionToken());

            boolean isDark = UserRepo.isDarkModeEnabled(active.getUsername());
            SessionManager.getInstance().setDarkMode(isDark);

            loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/home-page.fxml"));
            nextThemeKey = "Home";
        } else {
            loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/starting-page.fxml"));
            nextThemeKey = "Starting";
        }

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> fadeOutSplash());
        pause.play();
    }

    private void fadeOutSplash() {
        // Performs a fadeout transition
        FadeTransition fade = new FadeTransition(Duration.seconds(0.7), rootPane);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        fade.setOnFinished(event -> switchToNextPage());
        fade.play();
    }

    private void switchToNextPage() {
        try {
            Parent root = loader.load();

            // 3. Apply Theme
            ThemeManager.applyTheme(root, nextThemeKey);

            Scene nextScene = new Scene(root);
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(nextScene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}