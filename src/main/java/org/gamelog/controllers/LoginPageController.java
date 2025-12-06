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
import org.gamelog.model.SessionManager;
import org.gamelog.repository.AuthRepo;
import org.gamelog.model.LoginResult;
import org.gamelog.repository.UserRepo; // Added Import
import org.gamelog.utils.ThemeManager; // Added Import

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

    private AuthRepo authRepo;

    public void initialize(){
        authRepo = new AuthRepo();
        loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/sign_up_page.fxml"));
        loginBtn.setOnMouseClicked(event -> {
            handleLogin();
        });

        forgotPasswordLink.setOnMouseClicked(event -> {
            try {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/email-submission-page.fxml")));
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        signupLink.setOnMouseClicked(event -> goToSignupPage());

        //Event listeners that clear the error messages once the user starts typing again
        usernameInput.setOnKeyPressed(event -> {
            usernameErrorMessage.setText("");
            usernameErrorMessage.setVisible(false);
        });

        passwordInput.setOnKeyPressed(event -> {
            passwordErrorMessage.setText("");
            passwordErrorMessage.setVisible(false);
        });
    }

    private void handleLogin(){
        username = usernameInput.getText().trim();
        password = passwordInput.getText().trim();

        clearErrorMessages();

        if(username.isEmpty() && password.isEmpty()){
            usernameErrorMessage.setText("*Required field!*");
            passwordErrorMessage.setText("*Required field!*");
            usernameErrorMessage.setVisible(true);
            passwordErrorMessage.setVisible(true);
            return;
        }else if(username.isEmpty()){
            usernameErrorMessage.setText("*Required field!*");
            usernameErrorMessage.setVisible(true);
            return;
        }else if(password.isEmpty()){
            passwordErrorMessage.setText("*Required field!*");
            passwordErrorMessage.setVisible(true);
            return;
        }

        LoginResult result = authRepo.loginUser(username, password);
        if(result.isSuccess()){
            try{
                SessionManager.createSession(username);

                //Checks if session was created successfully
                if(!SessionManager.isActive()){
                    //Session creation failed
                    passwordErrorMessage.setText("Failed to create session. Please try again.");
                    passwordErrorMessage.setVisible(true);
                    return;
                }

                boolean isDark = UserRepo.isDarkModeEnabled(username);
                SessionManager.getInstance().setDarkMode(isDark);
                // -------------------------------

                loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/home-page.fxml"));
                Parent root = loader.load();

                // 3. Apply theme
                ThemeManager.applyTheme(root, "Home");

                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            }catch(IOException e){
                e.printStackTrace();
            }
        }else{
            if (result.getMessage().contains("not found!")) {
                usernameErrorMessage.setText(result.getMessage());
                usernameErrorMessage.setVisible(true);
            }else if(result.getMessage().contains("password")){
                passwordErrorMessage.setText(result.getMessage());
                passwordErrorMessage.setVisible(true);
            }
        }
    }

    private void clearErrorMessages(){
        usernameErrorMessage.setText("");
        passwordErrorMessage.setText("");
        usernameErrorMessage.setVisible(false);
        passwordErrorMessage.setVisible(false);
    }

    private void goToSignupPage(){
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent root = loader.load();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}