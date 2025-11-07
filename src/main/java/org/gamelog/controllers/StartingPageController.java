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

    private FXMLLoader loader;

    @FXML
    public void initialize(){
        loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/login-page.fxml"));


        getStartedBtn.setOnMouseClicked(event -> {
            loadLoginPage();
        });
    }

    private void loadLoginPage(){
        try{
            Scene loginPage = new Scene(loader.load());

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(loginPage);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
