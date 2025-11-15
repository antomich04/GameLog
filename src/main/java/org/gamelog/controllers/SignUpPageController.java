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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.AuthRepo;
import org.gamelog.model.SignupResult;

import java.io.IOException;


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
    private Text loginLink;
    @FXML
    private Label usernameError;
    @FXML
    private Label emailError;
    @FXML
    private Label passwordError;
    @FXML
    private AnchorPane rootPane;

    private FXMLLoader loader;
    private AuthRepo authRepo;

    public void initialize() {
        loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/login-page.fxml"));
        signUpButton.setOnAction(event -> handleSignUp());
        loginLink.setOnMouseClicked(event -> goToLoginPage());
        authRepo = new AuthRepo();

        usernameField.setOnKeyTyped(event -> {
            usernameError.setText("");
            usernameError.setVisible(false);
        });

        emailField.setOnKeyTyped(event -> {
            emailError.setText("");
            emailError.setVisible(false);
        });

        passwordField.setOnKeyTyped(event -> {
            passwordError.setText("");
            passwordError.setVisible(false);
        });

    }

    @FXML
    private void handleSignUp() {
        boolean validSignup=true;
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        clearErrorMessages();

        if (username.isEmpty()) {
            usernameError.setText("*Required field!*");
            usernameError.setVisible(true);
            validSignup=false;
        }

        if (email.isEmpty()) {
            emailError.setText("*Required field!*");
            emailError.setVisible(true);
            validSignup=false;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}$")) {
            emailError.setText("*Please enter a valid email address!*");
            emailError.setVisible(true);
            validSignup=false;
        }

        if (password.isEmpty()) {
            passwordError.setText("*Required field!*");
            passwordError.setVisible(true);
            validSignup=false;
        } else if (password.length() < 6) {
            passwordError.setText("*Password must be at least 6 characters!*");
            passwordError.setVisible(true);
            validSignup=false;
        }else if (!password.contains("!@#$%^&*")){
            passwordError.setText("*Password must contain at least one special character!*");
            passwordError.setVisible(true);
        }

        if (validSignup) {
            SignupResult result = authRepo.signupUser(username, email, password);
            if (result.isSuccess()) {
                try {
                    SessionManager.createSession(username);

                    if(!SessionManager.isActive()){
                        passwordError.setText("Failed to create session. Please try again.");
                        passwordError.setVisible(true);
                        return;
                    }

                    loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/home-page.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) rootPane.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Displays specific error messages
                if (result.getMessage().contains("Username")) {
                    usernameError.setText(result.getMessage());
                    usernameError.setVisible(true);
                } else if (result.getMessage().contains("Email")) {
                    emailError.setText(result.getMessage());
                    emailError.setVisible(true);
                }
            }
        }
    }

    @FXML
    private void goToLoginPage() {
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