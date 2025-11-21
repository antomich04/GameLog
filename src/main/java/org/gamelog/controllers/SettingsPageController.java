package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.UserRepo;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;

public class SettingsPageController {

    @FXML
    private HBox accountClickableArea;

    @FXML
    private HBox deleteAccountClickableArea;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label usernameLetter;

    @FXML
    private Label memberSinceLabel;

    public void initialize() {
        accountClickableArea.setOnMouseClicked(event -> {
            handleAccountClick();
        });

        SessionManager sessionManager = SessionManager.getInstance();
        String username = sessionManager.getUsername();
        usernameLabel.setText(username);
        usernameLetter.setText(String.valueOf(Character.toUpperCase(username.charAt(0))));

        Timestamp ts = UserRepo.getCreationDate(username);
        if (ts != null) {
            LocalDate date = ts.toLocalDateTime().toLocalDate();
            memberSinceLabel.setText("Member since: " + date.toString()); //YYYY-MM-DD format
        }

        deleteAccountClickableArea.setOnMouseClicked(event -> {
            handleAccountDeletion(username);
        });
    }

    private void handleAccountClick() {
        try {
            Parent accountPage = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/account-settings-page.fxml"));
            switchScene(accountPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAccountDeletion(String username) {
        if(UserRepo.deleteUser(username)){
            SessionManager.clearSession();
            try{
                switchScene(FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/login-page.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void switchScene(Parent root) {
        Stage stage = (Stage) accountClickableArea.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}