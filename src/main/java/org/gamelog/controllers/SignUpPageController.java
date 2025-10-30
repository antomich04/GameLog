package org.gamelog.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class SignUpPageController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button signUpButton;
    @FXML
    private Button loginLink;
    @FXML
    private Label usernameError;
    @FXML
    private Label emailError;
    @FXML
    private Label passwordError;



    @FXML
    private void handleSignUp(ActionEvent event) {
        boolean signedup=true;
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        clearErrorMessages();

        if (username.isEmpty()) {
            usernameError.setText("*Username is required!*");
            usernameError.setVisible(true);
            signedup=false;
        }

        if (email.isEmpty()) {
            emailError.setText("*Email is required!*");
            emailError.setVisible(true);
            signedup=false;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            emailError.setText("*Please enter a valid email address!*");
            emailError.setVisible(true);
            signedup=false;
        }
        if (password.isEmpty()) {
            passwordError.setText("*Password is required!*");
            passwordError.setVisible(true);
            signedup=false;
        } else if (password.length() < 6) {
            passwordError.setText("*Password must be at least 6 characters!*");
            passwordError.setVisible(true);
            signedup=false;
        }
        if (signedup){
            System.out.println("signed up");
        }
    }

    @FXML
    private void goToLoginPage(ActionEvent event) {

    }

    private void clearErrorMessages() {
        usernameError.setVisible(false);
        emailError.setVisible(false);
        passwordError.setVisible(false);
    }


}