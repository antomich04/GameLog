package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.gamelog.Main;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.UserRepo;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

public class SettingsPageController {

    @FXML
    private HBox accountClickableArea;
    @FXML
    private HBox deleteAccountClickableArea;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label usernameLetter;
    @FXML
    private Label memberSinceLabel;

    @FXML
    private ToggleButton notificationsToggle;

    public void initialize() {
        SessionManager sessionManager = SessionManager.getInstance();
        String username = sessionManager.getUsername();

        boolean isEnabled = UserRepo.isNotificationsEnabled(username);
        notificationsToggle.setSelected(isEnabled);

        // Notification Toggle Button
        notificationsToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            UserRepo.updateNotificationStatus(username, newValue);
        });

        accountClickableArea.setOnMouseClicked(event -> {
            handleAccountClick();
        });

        usernameLabel.setText(username);
        usernameLetter.setText(String.valueOf(Character.toUpperCase(username.charAt(0))));

        Timestamp ts = UserRepo.getCreationDate(username);
        if (ts != null) {
            LocalDate date = ts.toLocalDateTime().toLocalDate();
            memberSinceLabel.setText("Member since: " + date.toString());
        }

        deleteAccountClickableArea.setOnMouseClicked(event -> {
            handleAccountDeletion(username);
        });
    }

    private void handleAccountClick() {
        try {
            Parent accountPage = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/account-settings-page.fxml"));
            switchScene(accountPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAccountDeletion(String username) {


        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Account");
        confirmation.setHeaderText("Permanent Account Deletion");
        confirmation.setContentText("This will permanently delete your account and all data. This action cannot be undone.");

        Window window = confirmation.getDialogPane().getScene().getWindow();
        Stage stage = (Stage) window;
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/org/gamelog/Assets/Icon.png")));
        } catch (Exception ignored) {}

        ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmation.getButtonTypes().setAll(deleteButton, cancelButton);

        DialogPane dialogPane = confirmation.getDialogPane();
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/org/gamelog/Styles/dialogs.css").toExternalForm());
            dialogPane.getStyleClass().add("confirmation");
        } catch (Exception ignored) {}

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == deleteButton) {
            boolean shouldNotify = UserRepo.isNotificationsEnabled(username);

            if (UserRepo.deleteUser(username)) {
                SessionManager.clearSession();

                if (shouldNotify) {
                    showAccountDeletionNotification(username); //Create Notification
                }

                try {
                    switchScene(FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/login-page.fxml")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showAccountDeletionNotification(String username) {
        try {
            Image iconImage = new Image(Main.class.getResourceAsStream("/org/gamelog/Assets/Logo.png"));
            ImageView iconView = new ImageView(iconImage);
            iconView.setFitHeight(90);
            iconView.setFitWidth(120);

            Notifications.create()
                    .title("Account Deleted")
                    .text("Account '" + username + "' has been permanently deleted")
                    .graphic(iconView)
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .show();

        } catch (Exception e) {
            Notifications.create()
                    .title("Account Deleted")
                    .text("Account '" + username + "' has been permanently deleted")
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .show();
        }
    }

    private void switchScene(Parent root) {
        Stage stage = (Stage) accountClickableArea.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}