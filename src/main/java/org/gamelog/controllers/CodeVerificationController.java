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
import org.gamelog.utils.ThemeManager; // Import ThemeManager

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class CodeVerificationController {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button backBtn;
    @FXML
    private Button submitBtn;
    @FXML
    private Text resendCode;
    @FXML
    private TextField otp1;
    @FXML
    private TextField otp2;
    @FXML
    private TextField otp3;
    @FXML
    private TextField otp4;
    @FXML
    private Text codeErrorMessage;

    private String email;
    private List<TextField> otpFields;
    private AuthRepo authRepo;

    public void initialize() {
        otpFields = Arrays.asList(otp1, otp2, otp3, otp4);
        authRepo = new AuthRepo();

        setupOtpInputLogic();

        backBtn.setOnMouseClicked(e -> {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/login-page.fxml"));
                Parent root = loader.load();

                // 2. APPLY
                ThemeManager.applyTheme(root, "Login");

                Stage stage =  (Stage) rootPane.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        });

        submitBtn.setOnMouseClicked(e -> {
            String enteredCode = otp1.getText().trim() +
                    otp2.getText().trim() +
                    otp3.getText().trim() +
                    otp4.getText().trim();

            //Client side sanitization
            if(enteredCode.length() != 4){
                codeErrorMessage.setText("*Please enter the full 4-digit code.*");
                codeErrorMessage.setVisible(true);
                clearOtpsText();
                return;
            }

            codeErrorMessage.setText("");
            codeErrorMessage.setVisible(false);
            clearOtpsText();

            validateCodeAsync(enteredCode);

        });

        resendCode.setOnMouseClicked(e -> {
            sendCodeAsync(email);
        });
    }

    public void setEmail(String email){
        this.email = email;
    }

    private void clearOtpsText(){
        otp1.clear();
        otp2.clear();
        otp3.clear();
        otp4.clear();
    }

    private void setupOtpInputLogic() {
        for (int i = 0; i < otpFields.size(); i++) {

            TextField currentField = otpFields.get(i);
            TextField nextField = (i < otpFields.size() - 1) ? otpFields.get(i + 1) : null;
            TextField prevField = (i > 0) ? otpFields.get(i - 1) : null;

            //Enforces 1 numeric character input and shifts focus forward
            currentField.textProperty().addListener((observable, oldValue, newValue) -> {

                //Restricts to only one digit
                if(newValue.length() > 1){
                    currentField.setText(newValue.substring(0, 1));
                    return;
                }

                //Restricts to digits only
                if(!newValue.matches("\\d*")){
                    currentField.setText(oldValue);
                    return;
                }

                //If a single digit was entered, shifts focus to the next field
                if(newValue.length() == 1 && nextField != null){
                    nextField.requestFocus();
                }
            });

            //Shifts focus backwards
            currentField.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("BACK_SPACE") && currentField.getText().isEmpty()) {
                    if (prevField != null) {
                        prevField.requestFocus();
                        //Clears the previous field
                        prevField.setText("");
                        //Consumes the event to prevent further processing
                        event.consume();
                    }
                }

                if (codeErrorMessage != null && codeErrorMessage.isVisible()) {
                    codeErrorMessage.setText("");
                    codeErrorMessage.setVisible(false);
                }
            });
        }
    }

    private void sendCodeAsync(String recipientEmail) {

        //Disables interaction and shows loading
        submitBtn.setDisable(true);
        resendCode.setDisable(true);
        otp1.clear();
        otp2.clear();
        otp3.clear();
        otp4.clear();
        otp1.setDisable(true);
        otp2.setDisable(true);
        otp3.setDisable(true);
        otp4.setDisable(true);
        codeErrorMessage.setText("Sending new code...");
        codeErrorMessage.setVisible(true);

        Task<Void> sendEmailTask = new Task<>() {
            @Override
            protected Void call() throws Exception {

                String verificationCode = authRepo.generateAndStoreCode(recipientEmail);
                EmailSender emailSender = new EmailSender();
                emailSender.sendVerificationEmail(recipientEmail, verificationCode);
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();

                submitBtn.setDisable(false);
                resendCode.setDisable(false);
                otp1.setDisable(false);
                otp2.setDisable(false);
                otp3.setDisable(false);
                otp4.setDisable(false);
                codeErrorMessage.setText("New code sent successfully!");
            }

            @Override
            protected void failed() {
                super.failed();

                submitBtn.setDisable(false);
                resendCode.setDisable(false);

                Throwable ex = getException();
                String displayMessage;
                if(ex instanceof SQLException){
                    displayMessage = "*System Error: Could not save code to DB. Try again.*";
                }else if(ex instanceof MessagingException) {
                    displayMessage = "*Email Failed: Check your network or contact support.*";
                }else{
                    displayMessage = "*An unexpected error occurred.*";
                }

                codeErrorMessage.setText(displayMessage);
                ex.printStackTrace();
            }
        };

        new Thread(sendEmailTask).start();
    }

    private void  validateCodeAsync(String enteredCode) {

        submitBtn.setDisable(true);
        resendCode.setDisable(true);
        codeErrorMessage.setText("Verifying code...");
        codeErrorMessage.setVisible(true);

        Task<Boolean> validationTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {

                return authRepo.validateRecoveryCode(email, enteredCode);
            }

            @Override
            protected void succeeded() {
                super.succeeded();

                submitBtn.setDisable(false);
                resendCode.setDisable(false);

                if (getValue()) {
                    codeErrorMessage.setText("");

                    try {
                        Stage stage = (Stage) rootPane.getScene().getWindow();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/change-password-page.fxml"));
                        Parent root = loader.load();

                        Scene scene = new Scene(root);
                        ChangePasswordController cpc = loader.getController();
                        cpc.setEmail(email); //Passes email for the final update
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                } else {
                    codeErrorMessage.setText("*Invalid or expired code!*");
                }
            }

            @Override
            protected void failed() {
                super.failed();

                submitBtn.setDisable(false);
                resendCode.setDisable(false);

                Throwable ex = getException();

                String displayMessage;
                if(ex instanceof SQLException){
                    displayMessage = "*System Error: Database connection lost. Try again.*";
                }else{
                    displayMessage = "*An unexpected error occurred during verification.*";
                }

                codeErrorMessage.setText(displayMessage);
                ex.printStackTrace();
            }
        };

        new Thread(validationTask).start();
    }

}