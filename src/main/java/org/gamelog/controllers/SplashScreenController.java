package org.gamelog.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.gamelog.model.Session;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.SessionRepo;

import java.io.IOException;

public class SplashScreenController{

    @FXML
    private AnchorPane rootPane;

    private FXMLLoader loader;

    @FXML
    public void initialize(){

        SessionRepo sessionRepo = new SessionRepo();
        Session activeSession = sessionRepo.getActiveSession();

        if(activeSession != null){
            SessionManager.createSessionFromExisting(activeSession.getUsername(), activeSession.getSessionToken());
            loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/home-page.fxml"));
        }else{
            loader = new FXMLLoader(getClass().getResource("/org/gamelog/Pages/starting-page.fxml"));
        }

        //Creates a 3-second delay
        PauseTransition pause = new PauseTransition(Duration.seconds(1));

        pause.setOnFinished(event -> fadeOutSplash());
        pause.play();
    }

    private void fadeOutSplash(){
        //Performs a fadeout transition
        FadeTransition fade = new FadeTransition(Duration.seconds(0.7), rootPane);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        fade.setOnFinished(event -> switchToNextPage());
        fade.play();
    }

    private void switchToNextPage(){
        try{
            //Loads the starting page FXML
            Scene nextScene = new Scene(loader.load());

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(nextScene);
            stage.show();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
