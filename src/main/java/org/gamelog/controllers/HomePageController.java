package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.gamelog.model.SessionManager;

public class HomePageController {

    @FXML
    private Label iconLetter;
    @FXML
    private Label welcomeLabel;

    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        if (session != null) {
            String username = session.getUsername();
            welcomeLabel.setText("Welcome back, " + username);
            iconLetter.setText(String.valueOf(Character.toUpperCase(username.charAt(0))));
        }
    }
}