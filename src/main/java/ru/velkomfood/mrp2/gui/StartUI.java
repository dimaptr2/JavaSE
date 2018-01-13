package ru.velkomfood.mrp2.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class StartUI extends Application {

    private Stage window;

    @Override
    public void init() {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;
        showMainWindow(window);

    }

    // Command line starter
    public static void main(String[] args) {
        launch(args);
    }

    // PRIVATE SECTION
    private void showMainWindow(Stage window) throws Exception {

        window.setTitle("MRP2 statistics");
        window.show();

    }

}
