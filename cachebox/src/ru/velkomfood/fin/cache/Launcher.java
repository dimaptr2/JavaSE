package ru.velkomfood.fin.cache;

import com.sap.conn.jco.JCoException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.velkomfood.fin.cache.controller.DbManager;
import ru.velkomfood.fin.cache.controller.SapSniffer;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class Launcher extends Application {

    private SapSniffer sapSniffer;
    private DbManager dbManager;

    // Widgets
    private Stage window;
    private FXMLLoader loader;
    private BorderPane mainBorderPane;
    private Scene scene;
    private VBox vBoxLeft;
    private HBox hBoxTop;
    private Label labelHead;

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
//            dbManager.setMaterials(sapSniffer.getMaterials());
        } catch (JCoException | SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Cash Journal");
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
        window.setScene(scene);
        window.show();
    }
}
