package org.gamelog.controllers;

import jakarta.mail.MessagingException;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.gamelog.repository.AuthRepo;
import org.gamelog.utils.EmailSender;
import org.gamelog.utils.ThemeManager;
import java.io.IOException;
import java.sql.SQLException;

public class EmailSubmissionController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button backBtn;
    @FXML
    private Button submitBtn;
    @FXML
    private TextField emailInput;
    @FXML
    private Text emailErrorMessage;

    private String email;
    private AuthRepo authRepo;

    public void initialize(){

        backBtn.setOnMouseClicked(e -> {
            try{
                Stage stage = (Stage) rootPane.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/login-page.fxml"));
                Parent root = loader.load();

                ThemeManager.applyTheme(root, "Login");

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        });


        submitBtn.setOnMouseClicked(e -> {

            email = emailInput.getText().trim();
            authRepo = new AuthRepo();

            //Client side sanitization
            if(email.isEmpty()){
                emailErrorMessage.setText("*Required field!*");
                emailErrorMessage.setVisible(true);
                return;
            }else if(!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}$")){
                emailErrorMessage.setText("*Please enter a valid email address!!*");
                emailErrorMessage.setVisible(true);
                return;
            }else if(!authRepo.emailExists(email)){
                emailErrorMessage.setText("*Email not registered in system!*");
                emailErrorMessage.setVisible(true);
                return;
            }

            //Disables the button to prevent multiple submissions
            submitBtn.setDisable(true);

            //Separate thread for the email and db operations
            Task<String> sendEmailTask = new Task<>() {
                @Override
                protected String call() throws Exception {

                    String verificationCode = authRepo.generateAndStoreCode(email);

                    EmailSender emailSender = new EmailSender();
                    emailSender.sendVerificationEmail(email, verificationCode);

                    //Returns the email to be used in the succeeded() method
                    return email;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();

                    submitBtn.setDisable(false);

                    try {
                        //Navigation to Forgot Password Page
                        Stage stage = (Stage) rootPane.getScene().getWindow();

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/code-verification-page.fxml"));
                        Parent root = loader.load();

                        Scene scene = new Scene(root);

                        //Passes the email to the next controller
                        CodeVerificationController fpc = loader.getController();
                        fpc.setEmail(email);

                        stage.setScene(scene);
                        stage.show();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                protected void failed() {
                    super.failed();

                    submitBtn.setDisable(false);

                    //Displays specific error message from the background exception
                    Throwable ex = getException();

                    String displayMessage;
                    if(ex instanceof SQLException){
                        displayMessage = "*System Error. Try again.*";
                    }else if(ex instanceof MessagingException) {
                        displayMessage = "*Email Failed: Check your network.*";
                    }else{
                        displayMessage = "*An unexpected error occurred. Please try again.*";
                    }

                    emailErrorMessage.setText(displayMessage);
                    emailErrorMessage.setVisible(true);
                }
            };

            //Executes the Task in a separate thread immediately
            new Thread(sendEmailTask).start();

        });

        emailInput.setOnKeyPressed(event -> {
            emailErrorMessage.setText("");
            emailErrorMessage.setVisible(false);
        });
    }
}