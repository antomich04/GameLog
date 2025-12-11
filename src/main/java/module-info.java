module org.gamelog.gamelog {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires org.postgresql.jdbc;
    requires jbcrypt;
    requires jakarta.mail;
    requires java.net.http;
    requires java.naming;
    requires com.fasterxml.jackson.databind;
    requires org.controlsfx.controls;
    requires javafx.base;


    opens org.gamelog to javafx.fxml;
    exports org.gamelog;
    opens org.gamelog.controllers to javafx.fxml;
    exports org.gamelog.controllers;
    opens org.gamelog.model to javafx.base;
}