package ru.velkomfood.fin.cache;

import com.sap.conn.jco.JCoException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private Launcher launcher;

    @FXML
    private DatePicker fromDate;
    @FXML
    private BorderPane mainBorderPain;
    @FXML
    private TableView mainContentTable;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setLauncher(Launcher launcher) {
        this.launcher = launcher;
    }

    public void setMultipleSelectionMode(SelectionMode selMode) {
        mainContentTable.getSelectionModel().setSelectionMode(selMode);
    }

    // Connect to SAP
    public void connectToSap(ActionEvent event) {

        Alert alert = null;

        try {
            sapSniffer.initSAPConnection();
            if (sapSniffer.getjCoDestination().isValid()) {
                sapSniffer.getjCoDestination().ping();
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("SAP connection status");
                String message = "Успешное соединение! Клиент: ";
                message = message + sapSniffer.getjCoDestination().getClient();
                alert.setContentText(message);
            }
        } catch (JCoException jcoe) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("SAP connection error ...");
            alert.setContentText(jcoe.getMessage());
        }

        alert.setHeaderText(null);
        alert.showAndWait();

    }

    // Upload material master data
    // Upload credit slips
    @FXML
    public void pushSearchButton(ActionEvent event) {

        // Process the date (date picker's value) as a string value
        StringBuilder sbDate = new StringBuilder(0);

        if (fromDate.getValue() == null) {
            sbDate.append("");
        } else {
            String[] strDate = String.valueOf(fromDate.getValue()).split("-");
            for (String s : strDate) {
                if (!s.equals("")) {
                    sbDate.append(s);
                }
            }
        }

        launcher.showSAPUploadProcess(sbDate.toString());
    }

    public void closeWindow(ActionEvent event) {
        cache.refreshData();
        stage.close();
    }

}
