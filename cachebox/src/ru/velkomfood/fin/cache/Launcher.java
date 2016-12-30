package ru.velkomfood.fin.cache;

import com.sap.conn.jco.JCoException;
import javafx.application.Application;
import javafx.stage.Stage;
import ru.velkomfood.fin.cache.controller.DbManager;
import ru.velkomfood.fin.cache.controller.SapSniffer;

import java.sql.SQLException;

public class Launcher extends Application {

    private SapSniffer sapSniffer;
    private DbManager dbManager;

    @Override
    public void init() {

        sapSniffer = SapSniffer.getInstance();
        dbManager = DbManager.getInstance();
//        dbManager.createDataSource();

        try {
            sapSniffer.initSAPConnection();
//            sapSniffer.getAllMaterials();
            dbManager.openDbConnection();
            dbManager.initLocalDatabase();
            dbManager.setMaterials(sapSniffer.getMaterials());
        } catch (JCoException | SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage window = primaryStage;
        window.setTitle("Cash Journal");
        sapSniffer.readCreditSlips("", "");
        window.show();
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
}
