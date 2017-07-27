package ru.velkomfood.fin.cash.logon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.velkomfood.fin.cash.logon.controller.DbReader;
import ru.velkomfood.fin.cash.logon.controller.DeviceStatus;
import ru.velkomfood.fin.cash.logon.controller.FiscalDevice;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by dpetrov on 18.07.17.
 */
public class StartUI extends Application {

    private Stage window;
    private FiscalDevice fiscalDevice;
    private DeviceStatus deviceStatus;
    private DbReader dbReader;
    private FXMLLoader loader;
    private StartUIController controller;
    private BorderPane mainBorderPain;
    private Scene scene;

//    private final String URL = "http://srv-fin.eatmeat.ru:8083";
//    private String addrServer;

    @Override
    public void init() {
        fiscalDevice = FiscalDevice.getInstance();
        deviceStatus = DeviceStatus.getInstance();
        fiscalDevice.setDeviceStatus(deviceStatus);
        fiscalDevice.initFiscalDevice();
        dbReader = DbReader.getInstance();
        try {
            dbReader.openConnection();
        } catch (SQLException sqEx) {
            sqEx.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        showMainWindow(window);
    }

    @Override
    public void stop() {
        try {
            dbReader.closeConnection();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


    private void showMainWindow(Stage window) throws IOException, SQLException {
        window.setTitle("Cash Journal Logon");
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("start.fxml"));
        mainBorderPain = loader.load();
        scene = new Scene(mainBorderPain);
        StartUIController controller = loader.getController();
        controller.setStage(window);
        controller.setDevice(fiscalDevice);
        controller.setDb(dbReader);
        window.setScene(scene);
        window.show();
        if (!dbReader.isOpened()) {
            noConnection();
            window.close();
        }
    }

    private void noConnection() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ошибка!");
        alert.setContentText("Нет соединения с базой данных");
        alert.showAndWait();
    }

}
