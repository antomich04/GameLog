package org.gamelog.controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.gamelog.model.AchievementStatus;
import org.gamelog.model.DetailedBacklogItem;
import org.gamelog.model.GameInfo;
import org.gamelog.model.SessionManager;
import org.gamelog.repository.GamesRepo;
import org.gamelog.utils.RawgClient;
import java.text.DecimalFormat;
import java.util.List;

public class EditItemController {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button backBtn;
    @FXML
    private Button confirmBtn;
    @FXML
    private ImageView coverImageContainer;
    @FXML
    private Text gameRating;
    @FXML
    private Text gameReleaseDate;
    @FXML
    private Text gameTitle;
    @FXML
    private Text gamePlatforms;
    @FXML
    private Text achievementsCounter;
    @FXML
    private VBox loadingOverlay;
    @FXML
    private VBox achievementsListContainer;
    @FXML
    private ScrollPane achievementsScrollPane;
    @FXML
    private Button toggleAchievementsBtn;

    private DetailedBacklogItem currentItem;
    private final DecimalFormat ratingFormat = new DecimalFormat("0.00");
    private String previousPageFxml;
    private static final int PLATFORM_CHAR_LIMIT = 40;
    private String currentUsername;
    private List<AchievementStatus> achievementData;
    private EventHandler<MouseEvent> clickAwayHandler;
    private Runnable onDataUpdated;

    public void initialize(){
        backBtn.setOnMouseClicked(e -> {
            returnToPreviousPage();
        });

        confirmBtn.setOnMouseClicked(e -> {
            updateProgressAndClose();
        });

        confirmBtn.setDisable(true);

        toggleAchievementsBtn.setOnMouseClicked(e -> {
            toggleAchievementList();
        });

        this.currentUsername = SessionManager.getInstance().getUsername();

        toggleAchievementsBtn.setVisible(false);
        toggleAchievementsBtn.setManaged(false);
        achievementsCounter.setVisible(false);
        achievementsCounter.setManaged(false);
    }

    private void updateProgressAndClose(){
        if(onDataUpdated!=null){
            onDataUpdated.run();
        }

        returnToPreviousPage();
    }

    public void setOnDataUpdated(Runnable onDataUpdated){ this.onDataUpdated = onDataUpdated; }

    private void toggleAchievementList() {
        boolean isVisible = achievementsScrollPane.isVisible();

        if (isVisible) {
            achievementsScrollPane.setVisible(false);
            achievementsScrollPane.setManaged(false);
            toggleAchievementsBtn.setText("+");
        } else {
            if (achievementData != null) {
                achievementsScrollPane.setVisible(true);
                achievementsScrollPane.setManaged(true);
            } else {
                loadAchievements();
            }
            toggleAchievementsBtn.setText("-");
        }
    }

    private void loadAchievements() {
        loadingOverlay.setVisible(true);
        loadingOverlay.setManaged(true);
        achievementsCounter.setDisable(true);

        Thread achievementLoader = new Thread(() -> {

            int gid = currentItem.getGid();
            int rawgId = currentItem.getRawgId();

            //Fetches the synced achievement status
            List<AchievementStatus> statusList = GamesRepo.fetchAchievementStatus(currentUsername, gid, rawgId);

            Platform.runLater(() -> {
                this.achievementData = statusList;
                buildAchievementChecklist(statusList);

                loadingOverlay.setVisible(false);
                loadingOverlay.setManaged(false);
                achievementsCounter.setDisable(false);
            });
        });

        achievementLoader.setDaemon(true);
        achievementLoader.start();
    }

    //Dynamically creates CheckBox elements for each achievement status.
    private void buildAchievementChecklist(List<AchievementStatus> statusList) {
        achievementsListContainer.getChildren().clear();

        boolean hasAchievements = !statusList.isEmpty() && currentItem.getTotalAchievements() > 0;

        if (!hasAchievements) {
            // Case: No achievements available or API failed to return any valid ones
            achievementsCounter.setText("No Achievements Available!");
            achievementsCounter.setVisible(true);
            achievementsCounter.setManaged(true);

            toggleAchievementsBtn.setVisible(false);
            toggleAchievementsBtn.setManaged(false);

            if (achievementsScrollPane != null) {
                achievementsScrollPane.setVisible(false);
                achievementsScrollPane.setManaged(false);
            }
            return;
        }

        achievementsCounter.setVisible(true);
        achievementsCounter.setManaged(true);
        toggleAchievementsBtn.setVisible(true);
        toggleAchievementsBtn.setManaged(true);
        toggleAchievementsBtn.setText("+");

        double calculatedMaxRowWidth = 0;

        for (AchievementStatus status : statusList) {

            CheckBox achievementCheckbox = new CheckBox();
            achievementCheckbox.setSelected(status.isAchieved());
            achievementCheckbox.setOnAction(e -> handleAchievementToggle(status, achievementCheckbox.isSelected()));

            Text achievementNameText = new Text(status.getName());
            achievementNameText.setFont(javafx.scene.text.Font.font("MS Reference Sans Serif", 14));

            double estimatedTextWidth = status.getName().length() * 7.5;

            double estimatedTotalRowWidth = estimatedTextWidth + 25;

            if (estimatedTotalRowWidth > calculatedMaxRowWidth) {
                calculatedMaxRowWidth = estimatedTotalRowWidth;
            }

            HBox achievementRow = new HBox(10); //10px spacing
            achievementRow.setAlignment(Pos.CENTER_LEFT);

            achievementRow.getChildren().addAll(achievementNameText, achievementCheckbox);
            achievementsListContainer.getChildren().add(achievementRow);
        }

        if (achievementsScrollPane != null) {
            double finalWidth = calculatedMaxRowWidth + 30;

            achievementsScrollPane.setPrefWidth(Math.min(finalWidth, 600));
            achievementsListContainer.setPrefWidth(finalWidth - 10);
        }

        updateAchievementCounter();
    }

    //Handles the toggling of an achievement checkbox and updates the database/UI.
    private void handleAchievementToggle(AchievementStatus status, boolean isChecked) {
        achievementsListContainer.setDisable(true);

        Thread updateThread = new Thread(() -> {

            boolean success = GamesRepo.updateAchievementStatus(
                    currentUsername,
                    status.getGid(),
                    status.getName(),
                    isChecked
            );

            Platform.runLater(() -> {
                achievementsListContainer.setDisable(false);

                if (success) {
                    updateAchievementCounter();
                }
            });
        });

        updateThread.setDaemon(true);
        updateThread.start();
    }

    //Updates the achievementsCounter text based on current progress.
    private void updateAchievementCounter() {
        Thread countThread = new Thread(() -> {
            int gid = currentItem.getGid();
            int achievedCount = GamesRepo.getAchievedCount(currentUsername, gid);
            int totalCount = currentItem.getTotalAchievements();

            GamesRepo.updateBacklogProgress(currentUsername, gid, achievedCount);

            Platform.runLater(() -> {
                currentItem.setProgress(achievedCount); // Update local model
                achievementsCounter.setText("Achievements: " + achievedCount + "/" + totalCount);
            });
        });
        countThread.setDaemon(true);
        countThread.start();
    }

    public void returnToPreviousPage(){

        //Removes event filter to avoid memory leaks
        if (rootPane.getScene() != null && clickAwayHandler != null) {
            rootPane.getScene().removeEventFilter(MouseEvent.MOUSE_CLICKED, clickAwayHandler);
        }

        try{
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource(previousPageFxml)));
            stage.setScene(scene);
            stage.show();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void setBacklogId(int backlogId) {
        //Shows Loading Overlay
        loadingOverlay.setVisible(true);
        loadingOverlay.setManaged(true);

        achievementsCounter.setVisible(false);
        achievementsCounter.setManaged(false);
        toggleAchievementsBtn.setVisible(false);
        toggleAchievementsBtn.setManaged(false);

        Thread initialLoader = new Thread(() -> {
            //Fetches the Detailed Backlog Item from the DB
            DetailedBacklogItem item = GamesRepo.fetchDetailedBacklogItem(backlogId);

            Platform.runLater(() -> {
                if (item != null) {
                    this.currentItem = item;

                    loadAchievements();
                    fetchAndDisplayPlatforms(item.getRawgId());

                    //If rating is null or 0.0, proceeds with the API fetch.
                    if (item.getRating() == null || item.getRating() <= 0.0 || item.getReleaseDate() == null || item.getCoverImageUrl() == null) {
                        //Starts the API fetch/update process for metadata
                        loadAndPopulateDetails(item.getGid(), item.getRawgId());
                    } else {
                        //Data is already available, populates the UI and hides loading
                        populateUI(item);
                        loadingOverlay.setVisible(false);
                        loadingOverlay.setManaged(false);

                        setupClickAwayDismissal();
                    }
                } else {
                    loadingOverlay.setVisible(false);
                    loadingOverlay.setManaged(false);
                }
            });
        });
        initialLoader.setDaemon(true);
        initialLoader.start();
    }

    private void setupClickAwayDismissal() {
        if (rootPane.getScene() == null) return;

        if (clickAwayHandler != null) {
            rootPane.getScene().removeEventFilter(MouseEvent.MOUSE_CLICKED, clickAwayHandler);
        }

        clickAwayHandler = event -> {
            if (achievementsScrollPane.isVisible()) {

                Node clickedNode = (Node) event.getTarget();

                if (!achievementsScrollPane.isHover() && !toggleAchievementsBtn.isHover() &&
                        clickedNode != achievementsScrollPane && clickedNode != toggleAchievementsBtn) {


                    boolean clickIsInsideDropdown = achievementsScrollPane.getBoundsInParent().contains(event.getX(), event.getY());
                    boolean clickIsInsideButton = toggleAchievementsBtn.getBoundsInParent().contains(event.getX(), event.getY());

                    if (!clickIsInsideDropdown && !clickIsInsideButton) {
                        achievementsScrollPane.setVisible(false);
                        achievementsScrollPane.setManaged(false);
                        toggleAchievementsBtn.setText("+");
                    }
                }
            }
        };

        // Add the handler to the root pane
        rootPane.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, clickAwayHandler);
    }

    private void fetchAndDisplayPlatforms(int rawgId) {
        Thread platformLoader = new Thread(() -> {
            //Fetches the full platform list from the API
            List<String> fullPlatforms = RawgClient.fetchGamePlatforms(rawgId);

            Platform.runLater(() -> {
                String joinedPlatforms = String.join(", ", fullPlatforms);

                currentItem.setPlatform(joinedPlatforms);

                gamePlatforms.setText(limitPlatformText(joinedPlatforms));
            });
        });
        platformLoader.setDaemon(true);
        platformLoader.start();
    }

    private void loadAndPopulateDetails(int gid, int rawgId) {

        Thread detailLoader = new Thread(() -> {
            //Fetches data from RAWG API
            GameInfo details = RawgClient.fetchGameDetails(rawgId);

            //Updates the database
            if (details.getRating() != null && details.getCoverImageUrl() != null) {
                GamesRepo.updateGameInfo(gid, details);
            }

            //Updates the cached BacklogItem with the new data
            currentItem.setRating(details.getRating() != null ? details.getRating() : currentItem.getRating());
            currentItem.setReleaseDate(details.getReleaseDate() != null ? details.getReleaseDate() : currentItem.getReleaseDate());
            currentItem.setCoverImageUrl(details.getCoverImageUrl() != null ? details.getCoverImageUrl() : currentItem.getCoverImageUrl());

            Platform.runLater(() -> {
                populateUI(currentItem);
                loadingOverlay.setVisible(false);
                loadingOverlay.setManaged(false);

                setupClickAwayDismissal();
            });

        });

        detailLoader.setDaemon(true);
        detailLoader.start();
    }

    private void populateUI(DetailedBacklogItem item) {
        //Sets all UI elements based on the final item data

        gameTitle.setText(item.getGameName());
        gamePlatforms.setText(limitPlatformText(item.getPlatform()));

        if (achievementData == null) {
            achievementsCounter.setText("Achievements: " + item.getProgress() + "/" + item.getTotalAchievements());
        }

        //Handles optional fields
        if (item.getRating() != 0.0 && item.getRating() != null) {
            gameRating.setText(ratingFormat.format(item.getRating()));
        } else {
            gameRating.setText("N/A");
        }

        if (item.getReleaseDate() != null && !item.getReleaseDate().isEmpty()) {
            gameReleaseDate.setText(item.getReleaseDate());
        } else {
            gameReleaseDate.setText("N/A");
        }

        //Loads Cover Image
        if (item.getCoverImageUrl() != null && !item.getCoverImageUrl().isEmpty()) {
            try {
                Image cover = new Image(item.getCoverImageUrl(), true);
                coverImageContainer.setImage(cover);
            } catch (Exception e) {
                coverImageContainer.setImage(null);
            }
        }

        confirmBtn.setDisable(false);
    }

    private String limitPlatformText(String platformsList) {
        if (platformsList == null || platformsList.isEmpty()) {
            return "N/A";
        }

        //Joins the list into a single string for length comparison and truncation
        String fullText = String.join(", ", platformsList);

        if (fullText.length() > PLATFORM_CHAR_LIMIT) {

            //Gets the substring up to the limit
            String limitedSubString = fullText.substring(0, PLATFORM_CHAR_LIMIT);

            //Searches for the last full platform separator within that limited substring.
            int lastSeparatorIndex = limitedSubString.lastIndexOf(", ");

            String truncatedText;
            if (lastSeparatorIndex != -1) {
                truncatedText = limitedSubString.substring(0, lastSeparatorIndex);
            } else {
                truncatedText = limitedSubString;
            }

            //Cleans up the end of the truncated text
            truncatedText = truncatedText.trim();
            if (truncatedText.endsWith(",")) {
                truncatedText = truncatedText.substring(0, truncatedText.length() - 1);
            }

            //Adds the ellipsis
            return truncatedText + ", ...";
        }

        //If the full joined string does not exceed the limit, returns it
        return fullText;
    }

    public void setPreviousPage(String fxmlPath) {
        if (fxmlPath != null && !fxmlPath.isEmpty()) {
            this.previousPageFxml = fxmlPath;
        }
    }
}
