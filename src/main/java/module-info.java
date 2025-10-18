module org.gamelog.gamelog {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.gamelog.gamelog to javafx.fxml;
    exports org.gamelog.gamelog;
}