package ru.velkomfood.mysap.copa.download;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.velkomfood.mysap.copa.download.controller.ErpConnector;

public class Launcher extends Application {

	final String DEST = "PRDCLNT500";
	private Stage window;
	private ErpConnector erpConnector;

	@Override
	public void init() {
		erpConnector = ErpConnector.getInstance(DEST);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		window = primaryStage;
		window.setTitle("Download CO-PA data");
		window.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}