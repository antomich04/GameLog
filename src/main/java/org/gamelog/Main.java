package org.gamelog;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.gamelog.repository.DatabaseInitializer;

import java.io.IOException;

public class Main extends Application{
    
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseInitializer.init();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Pages/splash-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("GameLog");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(Main.class.getResource("Assets/Icon.png").toExternalForm()));
        stage.show();
    }
}
