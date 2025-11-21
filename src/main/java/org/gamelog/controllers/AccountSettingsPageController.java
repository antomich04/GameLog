package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.gamelog.model.SessionManager;

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
    private String originalEmail = "MikePap@gmail.com";
    private String originalPassword = "********";

    public void initialize() {
        SessionManager sessionManager = SessionManager.getInstance();
        String username = sessionManager.getUsername();
        usernameLetter.setText(String.valueOf(Character.toUpperCase(username.charAt(0))));

        loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/settings-page.fxml"));
        confirmButton.setOnAction(event -> handleConfirm());
        backButton.setOnAction(event -> goToSettingsPage());

        setCurrentUserData(username);
    }

    private void setCurrentUserData(String username) {
        usernameField.setText(username);
        emailField.setText(originalEmail);
        passwordField.setText(originalPassword);
    }

    @FXML
    private void handleConfirm() {
        boolean valid = true;
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        clearErrorMessages();

        boolean usernameEdited = !username.equals(username);
        boolean passwordEdited = !password.equals(originalPassword);
        boolean anyFieldEdited = usernameEdited || passwordEdited;

        if (usernameEdited) {
            if (username.isEmpty()) {
                usernameError.setText("*Required Field!*");
                usernameError.setVisible(true);
                valid = false;
            } else if (username.length() < 3) {
                usernameError.setText("*Username must be at least 3 characters!*");
                usernameError.setVisible(true);
                valid = false;
            }
        }

        if (passwordEdited) {
            if (password.length() < 6) {
                passwordError.setText("*Password must be at least 6 characters!*");
                passwordError.setVisible(true);
                valid = false;
            }
        }

        if (!anyFieldEdited) {
            goToSettingsPage();
            return;
        }

        if (valid) {
            updateAccountSettings(usernameEdited, passwordEdited, username, email, password);
        }

    }

    private void updateAccountSettings(boolean usernameEdited, boolean passwordEdited,
                                       String username, String email, String password) {

        if (usernameEdited) {
            System.out.println("Updating username to: " + username);
        }

        if (passwordEdited) {
            System.out.println("Updating password");

        }

        if (!usernameEdited && !passwordEdited) {

        }

        goToSettingsPage();
    }

    @FXML
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