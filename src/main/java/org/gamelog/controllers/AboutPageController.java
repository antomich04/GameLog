package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.gamelog.utils.ThemeManager;

public class AboutPageController {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextArea aboutTextArea;
    @FXML
    private Button backBtn;

    public void initialize() {
        ThemeManager.applyTheme(rootPane, "AboutPage");

        backBtn.setOnMouseClicked(event -> {
            try{
                Stage stage = (Stage) rootPane.getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/settings-page.fxml")));
                stage.setScene(scene);
                stage.show();
            }catch(Exception e){
                e.printStackTrace();
            }
        });

        aboutTextArea.setText("    GameLog was created as part of the course 'Database Technology' during the 7th semester at dept. of Information and Electronics " +
                "Engineering at International Hellenic University. It was created by Antonios Michailos and Michail Papageorgiou.\n\n" +
                "    This app serves the purpose of " +
                "enabling the user to keep track of his gaming backlog. He can add games to his list and see his progress to each game by seeing how much " +
                "achievements he has obtained. He is also able to update his progress from each game's corresponding page. Also, he can create his favorite games " +
                "list where he can only view the games that made a good impression to him. The application supports dark mode so the user can personalise his experience. " +
                "The application also includes notifications that keep the user updated regarding the outcome of his actions throughout the interface. The user is able " +
                "to disable the notifications from the settings page.");

    }
}
