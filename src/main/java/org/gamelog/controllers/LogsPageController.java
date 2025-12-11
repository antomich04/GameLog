package org.gamelog.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.gamelog.model.LogEntry;
import org.gamelog.repository.UserRepo;
import java.sql.Timestamp;

public class LogsPageController {
    @FXML
    private BorderPane rootPane;
    @FXML
    private TableView<LogEntry> logTableView;
    @FXML
    private TableColumn<LogEntry, Timestamp> timestampCol;
    @FXML
    private TableColumn<LogEntry, String> operationCol;
    @FXML
    private TableColumn<LogEntry, String> tableCol;
    @FXML
    private TableColumn<LogEntry, String> userCol;
    @FXML
    private TableColumn<LogEntry, Integer> recordIdCol;
    @FXML
    private TableColumn<LogEntry, String> detailsCol;
    @FXML
    private Button backBtn;

    public void initialize(){
        backBtn.setOnMouseClicked(e -> {
            try{
                Stage stage = (Stage) rootPane.getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/org/gamelog/Pages/settings-page.fxml")));
                stage.setScene(scene);
                stage.show();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        });

        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        operationCol.setCellValueFactory(new PropertyValueFactory<>("operation"));
        tableCol.setCellValueFactory(new PropertyValueFactory<>("tableName"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("actingUser"));
        recordIdCol.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));

        loadLogData();
    }

    private void loadLogData(){
        ObservableList<LogEntry> logs = FXCollections.observableArrayList(
                UserRepo.getApplicationLogs()
        );
        logTableView.setItems(logs);
    }
}
