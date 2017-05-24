package ru.velkomfood.fin.journals.cash;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by dpetrov on 24.05.2017.
 */
public class StartUI extends Application {

    private Stage window;

    @Override
    public void init() {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("CashJournal");
        window.show();
    }

    @Override
    public void stop() {

    }

    public static void main(String[] args) {
        launch(args);
    }

}
