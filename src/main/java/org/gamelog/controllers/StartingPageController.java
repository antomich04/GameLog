package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class StartingPageController{

    @FXML
    private Button getStartedBtn;

    @FXML
    private VBox rootPane;

    @FXML
    public void initialize(){
        getStartedBtn.setOnMouseClicked(event -> {
            loadLoginPage();
        });
    }

    private void loadLoginPage(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/login-page.fxml"));
            Scene loginPage = new Scene(loader.load());


            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(loginPage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
