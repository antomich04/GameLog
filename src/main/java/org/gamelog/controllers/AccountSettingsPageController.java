package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.gamelog.Main;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.AuthRepo;
import org.gamelog.repository.UserRepo;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class AccountSettingsPageController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button confirmButton;
    @FXML
    private Button backButton;
    @FXML
    private Label usernameError;
    @FXML
    private Label emailError;
    @FXML
    private Label passwordError;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label usernameLetter;

    private FXMLLoader loader;
    private String currentEmail;
    private final String passwordPlaceholder = "********";
    private String currentPassword;
    private String currentUsername;
    SessionManager sessionManager;

    public void initialize() {
        sessionManager = SessionManager.getInstance();
        currentUsername = sessionManager.getUsername();
        usernameLetter.setText(String.valueOf(Character.toUpperCase(currentUsername.charAt(0))));

        currentEmail = UserRepo.getEmail(currentUsername);
        currentPassword = UserRepo.getPassword(currentUsername);

        loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/settings-page.fxml"));
        confirmButton.setOnAction(event -> handleConfirm());
        backButton.setOnAction(event -> goToSettingsPage());

        setCurrentUserData(currentUsername);
    }

    private void setCurrentUserData(String username) {
        usernameField.setText(username);
        emailField.setText(currentEmail);
        passwordField.setText(passwordPlaceholder);
    }

    private void handleConfirm() {
        boolean valid = true;
        String newUsername = usernameField.getText().trim();
        String newPassword = passwordField.getText().trim();

        clearErrorMessages();

        boolean usernameEdited = !newUsername.equals(currentUsername);
        boolean passwordEdited = !newPassword.equals(passwordPlaceholder);
        boolean anyFieldEdited = usernameEdited || passwordEdited;

        if (usernameEdited) {
            if (newUsername.isEmpty()) {
                usernameError.setText("*Required Field!*");
                usernameError.setVisible(true);
                valid = false;
            } else if (newUsername.length() < 3) {
                usernameError.setText("*Username must be at least 3 characters!*");
                usernameError.setVisible(true);
                valid = false;
            } else if (newUsername.equals(currentUsername)) {
                usernameError.setText("*New username cannot be the same as the previous one!*");
                usernameError.setVisible(true);
            }
        }

        if (passwordEdited) {
            if (newPassword.isEmpty()) {
                passwordError.setText("*Required field!*");
                passwordError.setVisible(true);
                valid = false;
            } else if (newPassword.length() < 6) {
                passwordError.setText("*Password must be at least 6 characters!*");
                passwordError.setVisible(true);
                valid = false;
            } else if (!newPassword.matches(".*[!@#$%^&*].*")) {
                passwordError.setText("*Password must contain at least one special character!*");
                passwordError.setVisible(true);
                valid = false;
            } else if (BCrypt.checkpw(newPassword, currentPassword)) {
                passwordError.setText("*New password cannot be the same as the current one!*");
                passwordError.setVisible(true);
                valid = false;
            }
        }

        if (!anyFieldEdited) {
            goToSettingsPage();
            return;
        }

        if (valid) {
            updateAccountSettings(usernameEdited, passwordEdited, newUsername, newPassword);
        }
    }

    private void updateAccountSettings(boolean usernameEdited, boolean passwordEdited, String newUsername, String newPassword) {
        AuthRepo authRepo = new AuthRepo();
        boolean usernameSuccess = false;

        //Update Username
        if (usernameEdited) {
            if (authRepo.usernameExists(newUsername)) {
                usernameError.setText("*Username already exists!*");
                usernameError.setVisible(true);
                return;
            } else {
                UserRepo.updateUsername(currentUsername, newUsername);
                currentUsername = newUsername;
                sessionManager.setUsername(newUsername);
                usernameLetter.setText(String.valueOf(Character.toUpperCase(newUsername.charAt(0))));
                usernameSuccess = true;
            }
        }

        //Update Password
        if (passwordEdited) {
            UserRepo.updatePassword(currentUsername, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        }

        //Create Notification
        if (usernameSuccess && passwordEdited) {
            showSettingsNotification("Credentials changed successfully");
        } else if (usernameSuccess) {
            showSettingsNotification("Username changed successfully");
        } else if (passwordEdited) {
            showSettingsNotification("Password changed successfully");
        }

        goToSettingsPage();
    }

    private void showSettingsNotification(String message) {
        // Check If Notifications Are Enabled
        if (!UserRepo.isNotificationsEnabled(currentUsername)) {
            return;
        }

        try {
            Image iconImage = new Image(Main.class.getResourceAsStream("/org/gamelog/Assets/Logo.png"));
            ImageView iconView = new ImageView(iconImage);
            iconView.setFitHeight(90);
            iconView.setFitWidth(120);

            Notifications.create()
                    .title("Settings Updated")
                    .text(message)
                    .graphic(iconView)
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .show();

        } catch (Exception e) {
            Notifications.create()
                    .title("Settings Updated")
                    .text(message)
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .show();
        }
    }

    private void goToSettingsPage() {
        try {
            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearErrorMessages() {
        usernameError.setVisible(false);
        emailError.setVisible(false);
        passwordError.setVisible(false);
    }
}