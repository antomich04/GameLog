package org.gamelog.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.gamelog.repository.UserRepo;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.Notifications;
import org.gamelog.Main;
import org.gamelog.model.SearchResult;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.GamesRepo;
import org.gamelog.utils.RawgClient;
import org.gamelog.utils.ThemeManager; // Import ThemeManager

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AddGameModalController implements Initializable {

    @FXML
    private VBox rootPane; // Ensure your FXML root element has fx:id="rootPane"
    @FXML
    private ComboBox<SearchResult> gameSearchComboBox;
    @FXML
    private ComboBox<String> platformComboBox;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addGameButton;

    private Stage stage;
    private SearchResult selectedGame;
    private String selectedPlatform;
    private final String username = SessionManager.getInstance().getUsername();
    private boolean confirmed = false;

    //Executor initialized once
    private final ScheduledExecutorService searchExecutor = Executors.newSingleThreadScheduledExecutor();
    //Stores the future object for cancellation
    private ScheduledFuture<?> pendingSearchFuture = null;
    private ChangeListener<String> textChangeListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. APPLY THEME
        ThemeManager.applyTheme(rootPane, "AddGameModal");

        platformComboBox.setDisable(true); //Disables until a game is selected

        //Setups Game Search Listener
        setupGameSearch();
        setupPlatformFetcher();

        cancelButton.setOnMouseClicked(event -> {
            confirmed = false;
            searchExecutor.shutdownNow();
            stage.close();
        });

        addGameButton.setOnMouseClicked(event -> {
            if (isInputValid()) {
                selectedGame = gameSearchComboBox.getValue();
                selectedPlatform = platformComboBox.getValue();
                confirmed = true;
                searchExecutor.shutdownNow();
                stage.close();

                //  Notification call
                String gameName = selectedGame != null ? selectedGame.toString() : "Unknown Game";
                showGameAdditionNotification(gameName, selectedPlatform);
            }
        });
    }

    private void setupPlatformFetcher() {
        gameSearchComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            //Only runs if a new, non-null SearchResult is selected
            if(newVal != null){
                platformComboBox.getItems().clear();
                platformComboBox.setDisable(true); //Disables while fetching

                //Runs API call on the background thread
                searchExecutor.execute(() -> {
                    final int rawgId = newVal.getRawgId();
                    final List<String> availablePlatforms = RawgClient.fetchGamePlatforms(rawgId);

                    //Update UI
                    Platform.runLater(() -> {

                        if(!availablePlatforms.isEmpty()){
                            platformComboBox.getItems().addAll(availablePlatforms);
                            platformComboBox.setDisable(false);
                        }else{
                            //Handles case where no platforms are found (e.g., set error message)
                            platformComboBox.setPromptText("No platforms found for this game.");
                            platformComboBox.setStyle("-fx-text-fill: red; -fx-border-color: red;");
                        }

                    });
                });

            } else {
                //If selection is cleared, clears platforms
                platformComboBox.getItems().clear();
                platformComboBox.setDisable(true);
                platformComboBox.setPromptText("Select platform...");
            }
        });
    }

    private void showGameAdditionNotification(String gameName, String platform) {
        // Check If Notifications Are Enabled
        if (!UserRepo.isNotificationsEnabled(username)) {
            return;
        }

        try {
            Image iconImage = new Image(Main.class.getResourceAsStream("/org/gamelog/Assets/logo.png"));
            ImageView iconView = new ImageView(iconImage);
            iconView.setFitHeight(90);
            iconView.setFitWidth(120);

            Notifications.create()
                    .title("Game Added")
                    .text("\"" + gameName + "\" added to your backlog (" + platform + ")")
                    .graphic(iconView)
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .show();
        } catch (Exception e) {
            Notifications.create()
                    .title("Game Added")
                    .text("\"" + gameName + "\" added to your backlog (" + platform + ")")
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .show();
        }
    }



    private void setupGameSearch() {

        //Blocks SPACE in popup listview
        gameSearchComboBox.skinProperty().addListener((obs, oldSkin, newSkin) -> {

            if (newSkin instanceof ComboBoxListViewSkin<?> skin) {
                javafx.scene.Node popupContent = skin.getPopupContent();

                if (popupContent instanceof ListView<?> listView) {
                    EventHandler<KeyEvent> blockSpace = e -> {
                        if (e.getCode() == KeyCode.SPACE) {
                            e.consume();
                        }
                    };

                    //Blocks SPACE at ListView level
                    listView.addEventFilter(KeyEvent.KEY_PRESSED, blockSpace);
                }

            }

        });


        //Blocks commitment logic on key released
        gameSearchComboBox.getEditor().addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                //Allows SPACE to be pressed but stops final commit
                event.consume();
            }
        });

        gameSearchComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal != oldVal) {
                gameSearchComboBox.setValue(newVal);
                gameSearchComboBox.hide();
                Platform.runLater(() -> {
                    if (platformComboBox != null) {
                        platformComboBox.requestFocus();
                    }
                });
            }
        });


        //String converter
        gameSearchComboBox.setConverter(new StringConverter<SearchResult>() {
            @Override
            public String toString(SearchResult result) {
                return result != null ? result.toString() : gameSearchComboBox.getEditor().getText();
            }

            @Override
            public SearchResult fromString(String string) {

                if (gameSearchComboBox.getValue() != null &&
                        gameSearchComboBox.getValue().toString().equals(string)) {
                    return gameSearchComboBox.getValue();
                }

                return null; //prevents ClassCastException
            }
        });


        //Search logic
        ChangeListener<String> textChangeListener = (obs, oldValue, newValue) -> {

            //Distinguishes a user type in from a programmatic commit
            SearchResult currentValue = gameSearchComboBox.getValue();
            if (currentValue != null && newValue != null && newValue.equals(currentValue.toString())) {
                return;
            }

            if (newValue == null || newValue.trim().isEmpty()) {
                if (pendingSearchFuture != null) {
                    pendingSearchFuture.cancel(false);
                }
                Platform.runLater(() -> {
                    gameSearchComboBox.getSelectionModel().clearSelection();
                    gameSearchComboBox.setValue(null);
                    gameSearchComboBox.setItems(FXCollections.emptyObservableList());
                    gameSearchComboBox.hide();
                });
                return;
            }

            if (newValue.length() < 3) {
                if (pendingSearchFuture != null) {
                    pendingSearchFuture.cancel(false);
                }
                gameSearchComboBox.hide();
                return;
            }

            if (pendingSearchFuture != null && !pendingSearchFuture.isDone()) {
                pendingSearchFuture.cancel(false);
            }

            Runnable searchTask = () -> {
                List<SearchResult> results = RawgClient.searchGames(newValue);

                Platform.runLater(() -> {
                    String text = gameSearchComboBox.getEditor().getText();
                    int pos = gameSearchComboBox.getEditor().getCaretPosition();

                    gameSearchComboBox.getEditor().textProperty().removeListener(this.textChangeListener);

                    gameSearchComboBox.getSelectionModel().clearSelection();
                    gameSearchComboBox.setValue(null);

                    ObservableList<SearchResult> obsResults = FXCollections.observableArrayList(results);
                    gameSearchComboBox.setItems(obsResults);

                    gameSearchComboBox.getEditor().setText(text);
                    gameSearchComboBox.getEditor().positionCaret(Math.min(pos, text.length()));

                    if (!obsResults.isEmpty()) {
                        if (!gameSearchComboBox.isShowing()) {
                            gameSearchComboBox.show();
                        }
                    } else {
                        gameSearchComboBox.hide();
                    }

                    gameSearchComboBox.getEditor().textProperty().addListener(this.textChangeListener);
                });
            };

            pendingSearchFuture = searchExecutor.schedule(searchTask, 300, TimeUnit.MILLISECONDS);
        };

        this.textChangeListener = textChangeListener;
        gameSearchComboBox.getEditor().textProperty().addListener(this.textChangeListener);
    }

    private boolean isInputValid() {
        if (gameSearchComboBox.getValue() == null || !(gameSearchComboBox.getValue() instanceof SearchResult)) {
            if (gameSearchComboBox.getEditor().getText() == null || gameSearchComboBox.getEditor().getText().trim().isEmpty()) {
                gameSearchComboBox.setStyle("-fx-border-color: red;");
                return false;
            }
            gameSearchComboBox.setStyle("-fx-border-color: red;");
            return false;
        }

        if (platformComboBox.getValue() == null) {
            platformComboBox.setStyle("-fx-border-color: red;");
            return false;
        }

        SearchResult game = gameSearchComboBox.getValue();
        String platform = platformComboBox.getValue();

        //Checks for duplicate backlog item
        if (GamesRepo.isDuplicateBacklogItem(username, game.getRawgId(), platform)) {
            return false;
        }

        gameSearchComboBox.setStyle("");
        platformComboBox.setStyle("");
        return true;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public SearchResult getSelectedGame() {
        return selectedGame;
    }

    public String getSelectedPlatform() {
        return selectedPlatform;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}