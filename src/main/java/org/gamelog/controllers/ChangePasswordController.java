package org.gamelog.controllers;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.gamelog.repository.UserRepo;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.sql.SQLException;

public class ChangePasswordController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private PasswordField passwordInput1;
    @FXML
    private PasswordField passwordInput2;
    @FXML
    private Text passwordErrorMessage1;
    @FXML
    private Text passwordErrorMessage2;
    @FXML
    private Text passwordStatusMessage;
    @FXML
    private Button updateBtn;
    @FXML
    private Button backBtn;

    private String email;
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;

    public void initialize() {

        backBtn.setOnMouseClicked(e -> {
            try{
                Stage  stage = (Stage) rootPane.getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/login-page.fxml")));
                stage.setScene(scene);
                stage.show();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        });

        updateBtn.setOnMouseClicked(e -> {

            boolean hasFirstInputError = false;
            boolean hasSecondInputError = false;

            clearErrorMessages();

            newPassword = passwordInput1.getText().trim();
            confirmNewPassword = passwordInput2.getText().trim();

            if(newPassword.isEmpty()){
                passwordErrorMessage1.setText("*Required field!*");
                passwordErrorMessage1.setVisible(true);
                hasFirstInputError = true;
            }else if(newPassword.length() < 6) {
                passwordErrorMessage1.setText("*Password must be at least 6 characters!*");
                passwordErrorMessage1.setVisible(true);
                hasFirstInputError = true;
            }else if(!newPassword.matches(".*[!@#$%^&*].*")){
                passwordErrorMessage1.setText("*Password must contain at least one special character!*");
                passwordErrorMessage1.setVisible(true);
                hasFirstInputError = true;
            }

            if(!hasFirstInputError){
                if(confirmNewPassword.isEmpty()){
                    passwordErrorMessage2.setText("*Required field!*");
                    passwordErrorMessage2.setVisible(true);
                    hasSecondInputError = true;
                } else if (!confirmNewPassword.equals(newPassword)) {
                    passwordErrorMessage2.setText("*Passwords do not match!*");
                    passwordErrorMessage2.setVisible(true);
                    hasSecondInputError = true;
                }
            }

            if (!hasFirstInputError && !hasSecondInputError) {

                //Business logic check
                if(BCrypt.checkpw(newPassword, currentPassword)) {
                    passwordErrorMessage1.setText("*New password cannot be the same as the current one!*");
                    passwordErrorMessage1.setVisible(true);
                    return;
                }

                //Passes all tests
                updatePasswordAsync(newPassword);
            }

        });

        passwordInput1.setOnKeyPressed(e -> clearErrorMessages());
        passwordInput2.setOnKeyPressed(e -> clearErrorMessages());
    }

    public void setEmail(String email) {
        this.email = email;
        passwordInput1.setText("");
        passwordInput2.setText("");
        this.currentPassword = UserRepo.getPasswordByEmail(email);
    }

    private void updatePasswordAsync(String newPassword) {
        updateBtn.setDisable(true);

        passwordStatusMessage.setText("Updating password...");
        passwordStatusMessage.setVisible(true);

        Task<Boolean> updateTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {

                String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

                return UserRepo.updatePasswordByEmail(email, newHashedPassword);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                updateBtn.setDisable(false);

                if (getValue()) {

                    passwordStatusMessage.setText("Password successfully updated! Redirecting...");
                    passwordStatusMessage.setFill(Color.valueOf("#00FF00"));

                    PauseTransition delay = getPauseTransition();
                    delay.play();

                    showPasswordUpdateNotification();

                }
            }

            private PauseTransition getPauseTransition() {
                PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
                delay.setOnFinished(e -> {
                    try {
                        Stage stage = (Stage) rootPane.getScene().getWindow();
                        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/login-page.fxml")));
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                return delay;
            }

            @Override
            protected void failed() {
                super.failed();
                updateBtn.setDisable(false);

                Throwable ex = getException();

                String displayMessage = (ex instanceof SQLException)
                        ? "*Database error during update. Please try again.*"
                        : "*An unexpected error occurred during verification.*";

                passwordStatusMessage.setText(displayMessage);
                passwordStatusMessage.setFill(Color.RED);
                ex.printStackTrace();
            }
        };

        new Thread(updateTask).start();
    }

    private void clearErrorMessages() {
        passwordErrorMessage1.setText("");
        passwordErrorMessage1.setVisible(false);
        passwordErrorMessage2.setText("");
        passwordErrorMessage2.setVisible(false);
        passwordStatusMessage.setText("");
        passwordStatusMessage.setVisible(false);
    }

    private void showPasswordUpdateNotification() {
        String successTitle = "Success!";
        String successText = "Your password has been successfully updated.";

        try {
            Image iconImage = new Image(getClass().getResourceAsStream("/org/gamelog/Assets/Logo.png"));
            ImageView iconView = new ImageView(iconImage);
            iconView.setFitHeight(90);
            iconView.setFitWidth(120);

            Notifications.create()
                    .title(successTitle)
                    .text(successText)
                    .graphic(iconView)
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .show();
        } catch (Exception e) {
            //Fallback for when the logo image fails to load
            Notifications.create()
                    .title(successTitle)
                    .text(successText)
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(2))
                    .show();
        }
    }
}
