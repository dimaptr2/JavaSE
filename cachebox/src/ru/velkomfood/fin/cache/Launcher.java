package ru.velkomfood.fin.cache;

import com.sap.conn.jco.JCoException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.velkomfood.fin.cache.controller.DbManager;
import ru.velkomfood.fin.cache.controller.SapSniffer;

import java.io.IOException;
import java.sql.SQLException;

public class Launcher extends Application {

    SapSniffer sapSniffer;
    DbManager dbManager;

    // Widgets
    Stage mainWindow;
    private FXMLLoader loader;
    private BorderPane mainBorderPane;
    private Scene scene;

    @Override
    public void init() {

        sapSniffer = SapSniffer.getInstance();
        dbManager = DbManager.getInstance();
//        dbManager.createDataSource();

        try {
            sapSniffer.initSAPConnection();
            dbManager.openDbConnection();
            dbManager.initLocalDatabase();
        } catch (JCoException | SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainWindow = primaryStage;
        mainWindow.setTitle("Cash Journal");
//        sapSniffer.readCreditSlips("20170110", "20170110");
//        sapSniffer.buildReceipt();
//        java.util.List<Integer> positions = new ArrayList<>();
//        sapSniffer.calculateReceiptSums(positions);
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

    // PRIVATE SECTION
    private void showMainWindow() throws IOException {
        loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("start.fxml"));
        mainBorderPane = loader.load();
        scene = new Scene(mainBorderPane);
        MainWindowController controller = loader.getController();
        controller.setStage(mainWindow);
        mainWindow.setScene(scene);
        mainWindow.show();
    }

}
