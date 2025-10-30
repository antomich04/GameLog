package org.gamelog.gamelog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class StartingPageController {

    @FXML
    private Button getStartedButton;

    @FXML
    private void handleGetStarted(ActionEvent event) {
        try {
            Parent signUpRoot = FXMLLoader.load(getClass().getResource("sign_up_page.fxml"));
            Scene signUpScene = new Scene(signUpRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(signUpScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
