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

    private FXMLLoader loader;
    private String originalUsername = "Mike Pap";
    private String originalEmail = "MikePap@gmail.com";
    private String originalPassword = "********";

    public void initialize() {
        loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/settings-page.fxml"));
        confirmButton.setOnAction(event -> handleConfirm());
        backButton.setOnAction(event -> goToSettingsPage());

        // Set current user data
        setCurrentUserData();
    }
    private void setCurrentUserData() {
        usernameField.setText(originalUsername);
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

        boolean usernameEdited = !username.equals(originalUsername);
        boolean emailEdited = !email.equals(originalEmail);
        boolean passwordEdited = !password.equals(originalPassword);

        // Validate username
        if (usernameEdited) {
            if (username.isEmpty()) {
                usernameError.setText("*Username cannot be empty!*");
                usernameError.setVisible(true);
                valid = false;
            } else if (username.length() < 3) {
                usernameError.setText("*Username must be at least 3 characters!*");
                usernameError.setVisible(true);
                valid = false;
            }
        }
        // Validate email
        if (emailEdited) {
            if (email.isEmpty()) {
                emailError.setText("*Email cannot be empty!*");
                emailError.setVisible(true);
                valid = false;
            } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}$")) {
                emailError.setText("*Please enter a valid email address!*");
                emailError.setVisible(true);
                valid = false;
            }
        }
        // Validate password
        if (passwordEdited) {
            if (password.length() < 6) {
                passwordError.setText("*Password must be at least 6 characters!*");
                passwordError.setVisible(true);
                valid = false;
            }
        }
        // Check if at least one field was actually edited
        boolean anyFieldEdited = usernameEdited || emailEdited || passwordEdited;
        if (!anyFieldEdited) {
            goToSettingsPage();
            return;
        }
        if (valid) {
            updateAccountSettings(usernameEdited, emailEdited, passwordEdited, username, email, password);
        }
    }

    private void updateAccountSettings(boolean usernameEdited, boolean emailEdited, boolean passwordEdited,
                                       String username, String email, String password) {
        // TODO: Implement account settings update logic

        System.out.println("Updating account settings:");

        if (usernameEdited) {
            System.out.println("Updating username to: " + username);
            // Update username in database
        }

        if (emailEdited) {
            System.out.println("Updating email to: " + email);
            // Update email in database
        }

        if (passwordEdited) {
            System.out.println("Updating password");
            // Update password in database
        }

        if (!usernameEdited && !emailEdited && !passwordEdited) {
            System.out.println("No changes detected");
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