package org.gamelog.gamelog;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SplashScreenController{

    @FXML
    private AnchorPane rootPane;

    @FXML
    public void initialize(){
        //Creates a 3-second delay
        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        pause.setOnFinished(event -> fadeOutSplash());
        pause.play();
    }

    private void fadeOutSplash(){
        //Performs a fadeout transition
        FadeTransition fade = new FadeTransition(Duration.seconds(0.7), rootPane);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        fade.setOnFinished(event -> switchToStartingPage());
        fade.play();
    }

    private void switchToStartingPage(){
        try{
            //Loads the starting page FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("starting-page.fxml"));
            Scene startingScene = new Scene(loader.load());


            Stage stage = (Stage) rootPane.getScene().getWindow();


            stage.setScene(startingScene);

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
