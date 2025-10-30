module org.gamelog.gamelog {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.gamelog to javafx.fxml;
    exports org.gamelog;
    exports org.gamelog.controllers;
    opens org.gamelog.controllers to javafx.fxml;
}