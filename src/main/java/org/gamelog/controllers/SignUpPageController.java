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

    public void initialize() {
        loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/login-page.fxml"));
        signUpButton.setOnAction(event -> handleSignUp());
        loginLink.setOnMouseClicked(event -> goToLoginPage());
    }

    @FXML
    private void handleSignUp() {
        boolean signedup=true;
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        clearErrorMessages();

        if (username.isEmpty()) {
            usernameError.setText("*Required field!*");
            usernameError.setVisible(true);
            signedup=false;
        }

        if (email.isEmpty()) {
            emailError.setText("*Required field!*");
            emailError.setVisible(true);
            signedup=false;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}$")) {
            emailError.setText("*Please enter a valid email address!*");
            emailError.setVisible(true);
            signedup=false;
        }
        if (password.isEmpty()) {
            passwordError.setText("*Required field!*");
            passwordError.setVisible(true);
            signedup=false;
        } else if (password.length() < 6) {
            passwordError.setText("*Password must be at least 6 characters!*");
            passwordError.setVisible(true);
            signedup=false;
        }
        if (signedup){
            try{
                loader =  new FXMLLoader(getClass().getResource("/org/gamelog/Pages/home-page.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            }catch(IOException e){
                e.printStackTrace();
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