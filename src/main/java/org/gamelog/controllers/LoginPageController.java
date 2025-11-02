package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginPageController {

    @FXML
    private TextField usernameInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private Button loginBtn;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Text usernameErrorMessage;
    @FXML
    private Text passwordErrorMessage;
    @FXML
    private Text forgotPasswordLink;
    @FXML
    private Text signupLink;
    @FXML
    private FXMLLoader loader;
    private String username;
    private String password;

    public void initialize(){
        loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/sign_up_page.fxml"));
        loginBtn.setOnMouseClicked(event -> {
            handleLogin();
        });
        forgotPasswordLink.setOnMouseClicked(event -> {
           System.out.println("Forgot Password Link");
        });
        signupLink.setOnMouseClicked(event -> {
            Stage stage = (Stage) signupLink.getScene().getWindow();
            try {
                Parent root = loader.load();
                stage.setScene(new Scene(root));

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        usernameErrorMessage.setText("");
        passwordErrorMessage.setText("");
    }

    private void handleLogin(){
        username = usernameInput.getText();
        password = passwordInput.getText();

        if(username.isEmpty() && password.isEmpty()){
            usernameErrorMessage.setText("*Required field!*");
            passwordErrorMessage.setText("*Required field!*");
            return;
        }else if(username.isEmpty()){
            usernameErrorMessage.setText("*Required field!*");
            passwordErrorMessage.setText("");
            return;
        }else if(password.isEmpty()){
            passwordErrorMessage.setText("*Required field!*");
            usernameErrorMessage.setText("");
        }
        //TODO Den kanei kati otan einai kai ta dyo swsta prepei na sindesoume tin vasi!
    }
}
