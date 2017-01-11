package ru.velkomfood.fin.cache;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.velkomfood.fin.cache.controller.CacheEngine;
import ru.velkomfood.fin.cache.controller.DbManager;
import ru.velkomfood.fin.cache.controller.SapSniffer;


/**
 * Created by dpetrov on 10.01.2017.
 */
public class MainWindowController {

    CacheEngine cache = CacheEngine.getInstance();
    SapSniffer sapSniffer = SapSniffer.getInstance();
    DbManager dbManager = DbManager.getInstance();

    // Here is the reference to the main window
    private Stage stage;

    @FXML
    private DatePicker fromDate;
    @FXML
    private BorderPane mainBorderPain;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void pushSearchButton(ActionEvent event) {
    }

    public void closeWindow(ActionEvent event) {
        cache.refreshData();
        stage.close();
    }

}
