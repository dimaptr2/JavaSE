package ru.velkomfood.fin.cache;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.velkomfood.fin.cache.controller.CacheEngine;
import ru.velkomfood.fin.cache.controller.DbManager;
import ru.velkomfood.fin.cache.controller.SapSniffer;
import ru.velkomfood.fin.cache.view.UploadController;

import java.io.IOException;
import java.sql.SQLException;

public class Launcher extends Application {

    SapSniffer sapSniffer;
    DbManager dbManager;
    CacheEngine cacheEngine;

    // Widgets
    Stage mainWindow;
    private FXMLLoader loader;
    private BorderPane mainBorderPane;
    private Scene scene;

    @Override
    public void init() {

        sapSniffer = SapSniffer.getInstance();
        dbManager = DbManager.getInstance();
        cacheEngine = CacheEngine.getInstance();
//        dbManager.createDataSource();

        try {
//            sapSniffer.initSAPConnection();
            dbManager.openDbConnection();
            dbManager.initLocalDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainWindow = primaryStage;
        mainWindow.setTitle("Cash Journal");
        showMainWindow();
    }

    @Override
    public void stop() {
        try {
            dbManager.closeDbConnection();
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        }
    }

    // Start point of the application
    public static void main(String[] args) {
        launch(args);
    }

    // PUBLIC SECTION FOR DIALOGS
    public void showSAPUploadProcess(String dt) {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("view/sapdata.fxml"));

        try {

            AnchorPane pane = loader.load();
            Stage executionStage = new Stage();
            executionStage.setTitle("Загрузка данных");
            executionStage.initModality(Modality.WINDOW_MODAL);
            executionStage.initOwner(mainWindow);
            Scene scene = new Scene(pane);
            executionStage.setScene(scene);
            UploadController controller = loader.getController();
            controller.setSapSniffer(sapSniffer);
            controller.setCache(cacheEngine);
            controller.setDatum(dt);
            controller.setUploadStage(executionStage);
            executionStage.showAndWait();

        } catch (IOException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Application error");
            alert.setContentText(e.getMessage());

        }

    }

    // PRIVATE SECTION (START MAIN WINDOW)
    private void showMainWindow() throws IOException {
        loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("start.fxml"));
        mainBorderPane = loader.load();
        scene = new Scene(mainBorderPane);
        MainWindowController controller = loader.getController();
        controller.setStage(mainWindow);
        controller.setLauncher(this);
        controller.setMultipleSelectionMode(SelectionMode.MULTIPLE);
        mainWindow.setScene(scene);
        mainWindow.show();
    }

}
