package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.gamelog.utils.ThemeManager;
import java.io.IOException;

public class StartingPageController {

    @FXML
    private Button getStartedBtn;

    @FXML
    private VBox rootPane;

    @FXML
    public void initialize() {
        ThemeManager.applyTheme(rootPane, "Starting");

        getStartedBtn.setOnMouseClicked(event -> {
            loadLoginPage();
        });
    }

    private void loadLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/login-page.fxml"));
            Parent root = loader.load();

            ThemeManager.applyTheme(root, "Login");

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}