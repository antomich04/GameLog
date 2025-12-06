package org.gamelog.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.UserRepo;
import org.gamelog.utils.ThemeManager;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

public class SettingsPageController {

    @FXML
    private BorderPane rootPane;
    @FXML
    private HBox accountClickableArea;
    @FXML
    private HBox deleteAccountClickableArea;
    @FXML
    private HBox aboutClickableArea;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label usernameLetter;
    @FXML
    private Label memberSinceLabel;
    @FXML
    private ToggleButton notificationsToggle;
    @FXML
    private ToggleButton darkModeToggle;

    private SessionManager sessionManager;

    public void initialize() {
        sessionManager = SessionManager.getInstance();
        String username = sessionManager.getUsername();

        usernameLabel.setText(username);
        if (username != null && !username.isEmpty()) {
            usernameLetter.setText(String.valueOf(Character.toUpperCase(username.charAt(0))));
        }

        Timestamp ts = UserRepo.getCreationDate(username);
        if (ts != null) {
            LocalDate date = ts.toLocalDateTime().toLocalDate();
            memberSinceLabel.setText("Member since: " + date.toString());
        }

        boolean isNotifEnabled = UserRepo.isNotificationsEnabled(username);
        notificationsToggle.setSelected(isNotifEnabled);

        notificationsToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            UserRepo.updateNotificationStatus(username, newValue);
        });

        boolean isDark = sessionManager.isDarkMode();
        darkModeToggle.setSelected(isDark);

        ThemeManager.applyTheme(rootPane, "Settings");

        // Handle Toggle Switch
        darkModeToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            handleDarkModeSwitch(newVal, username);
        });

        accountClickableArea.setOnMouseClicked(event -> {
            handleAccountClick();
        });

        deleteAccountClickableArea.setOnMouseClicked(event -> {
            handleAccountDeletion(username);
        });

        if (aboutClickableArea != null) {
            aboutClickableArea.setOnMouseClicked(e -> System.out.println("About clicked"));
        }
    }

    private void handleDarkModeSwitch(boolean isEnabled, String username) {
        UserRepo.setDarkMode(username, isEnabled);
        sessionManager.setDarkMode(isEnabled);
        ThemeManager.applyTheme(rootPane, "Settings");

        Node navBarNode = rootPane.getLeft();
        if (navBarNode instanceof Parent) {
            ThemeManager.applyTheme((Parent) navBarNode, "NavBar");
        }
    }

    private void handleAccountClick() {
        try {
            navigateTo("/org/gamelog/Pages/account-settings-page.fxml", "AccountSettings");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateTo(String fxmlPath, String themeKey) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            ThemeManager.applyTheme(root, themeKey);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
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
            boolean isDark = SessionManager.getInstance().isDarkMode();
            String cssPath = isDark ? "/org/gamelog/Styles/Dialogs_css/Dialogs_dark.css" : "/org/gamelog/Styles/Dialogs_css/Dialogs.css";

            dialogPane.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            dialogPane.getStyleClass().add("confirmation");
        } catch (Exception ignored) {}

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == deleteButton) {
            boolean shouldNotify = UserRepo.isNotificationsEnabled(username);

            if (UserRepo.deleteUser(username)) {
                SessionManager.clearSession();

                if (shouldNotify) {
                    showAccountDeletionNotification(username);
                }

                try {
                    Parent loginRoot = FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/login-page.fxml"));
                    Stage currentStage = (Stage) rootPane.getScene().getWindow();
                    currentStage.setScene(new Scene(loginRoot));
                    currentStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showAccountDeletionNotification(String username) {
        try {
            Image iconImage = new Image(getClass().getResourceAsStream("/org/gamelog/Assets/logo.png"));
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
}