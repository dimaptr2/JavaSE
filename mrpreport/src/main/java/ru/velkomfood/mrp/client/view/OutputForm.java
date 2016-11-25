package ru.velkomfood.mrp.client.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.velkomfood.mrp.client.controller.ExcelExporter;
import ru.velkomfood.mrp.client.model.CrossTable;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by DPetrov on 17.08.2016.
 */
public class OutputForm extends Application {

    private Stage window;
    private int year;
    private Map<Integer, CrossTable> totalData;
    private ObservableList<OutputView> observableList;
    private TableView<OutputView> table;
    private final Button buttonExport = new Button("Export");
    private final Text actiontTarget = new Text();

    public OutputForm(int year, Map<Integer, CrossTable> totalData) {

        this.year = year;
        this.totalData = totalData;
        window = new Stage();
        window.setTitle("Планирование потребностей в материалах");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setFullScreen(true);
        observableList = FXCollections.observableArrayList();

    }

    public void display() throws Exception {
        start(window);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(new Group());
        final Label TOPLABEL = new Label("Данные за " + year + " год");
        TOPLABEL.setFont(new Font("Arial", 20));

        // Fill the table for this window
        table = new TableView();
        observableList = buildOutputTable();
        table.setItems(observableList);
        // end of table building

        // Output columns of table
        TableColumn<OutputView, String> colMaterial = new TableColumn<>("Материал");
        colMaterial.setMinWidth(18);
        colMaterial.setCellValueFactory(new PropertyValueFactory<OutputView, String>("material"));
        table.getColumns().add(colMaterial);

        TableColumn<OutputView, String> colMaterialDescription = new TableColumn<>("Наименование");
        colMaterialDescription.setMinWidth(45);
        colMaterialDescription.
                setCellValueFactory(new PropertyValueFactory<OutputView, String>("materialDescription"));
        table.getColumns().add(colMaterialDescription);

        TableColumn<OutputView, Integer> colWerks = new TableColumn<>("Завод");
        colWerks.setMinWidth(4);
        colWerks.setCellValueFactory(new PropertyValueFactory<OutputView, Integer>("werks"));
        table.getColumns().add(colWerks);

        TableColumn<OutputView, Integer> colYear = new TableColumn<>("Год");
        colYear.setMinWidth(4);
        colYear.setCellValueFactory(new PropertyValueFactory<OutputView, Integer>("year"));
        table.getColumns().add(colYear);

        TableColumn<OutputView, String> colPurchaseGroup = new TableColumn<>("Группа закупок");
        colPurchaseGroup.setMinWidth(3);
        colPurchaseGroup.setCellValueFactory(new PropertyValueFactory<OutputView, String>("purchaseGroup"));
        table.getColumns().add(colPurchaseGroup);

        TableColumn<OutputView, String> colNameOfPurchaseGroup = new TableColumn<>("Описание группы закупок");
        colNameOfPurchaseGroup.setMinWidth(15);
        colNameOfPurchaseGroup.
                setCellValueFactory(new PropertyValueFactory<OutputView, String>("nameOfPurchaseGroup"));
        table.getColumns().add(colNameOfPurchaseGroup);

        TableColumn<OutputView, String> colBaseUnit = new TableColumn<>("БЕИ");
        colBaseUnit.setMinWidth(4);
        colBaseUnit.setCellValueFactory(new PropertyValueFactory<OutputView, String>("baseUnit"));
        table.getColumns().add(colBaseUnit);

        TableColumn<OutputView, String> colStocks = new TableColumn<>("Запас");
        colStocks.setMinWidth(10);
        colStocks.setCellValueFactory(new PropertyValueFactory<OutputView, String>("stocks"));
        table.getColumns().add(colStocks);

        TableColumn<OutputView, String> colReq01 = new TableColumn<>("Январь");
        colReq01.setMinWidth(10);
        colReq01.setCellValueFactory(new PropertyValueFactory<OutputView, String>("req01"));
        table.getColumns().add(colReq01);

        TableColumn<OutputView, String> colReq02 = new TableColumn<>("Февраль");
        colReq02.setMinWidth(10);
        colReq02.setCellValueFactory(new PropertyValueFactory<OutputView, String>("req02"));
        table.getColumns().add(colReq02);

        TableColumn<OutputView, String> colReq03 = new TableColumn<>("Март");
        colReq03.setMinWidth(10);
        colReq03.setCellValueFactory(new PropertyValueFactory<OutputView, String>("req03"));
        table.getColumns().add(colReq03);

        TableColumn<OutputView, String> colReq04 = new TableColumn<>("Апрель");
        colReq04.setMinWidth(10);
        colReq04.setCellValueFactory(new PropertyValueFactory<OutputView, String>("req04"));
        table.getColumns().add(colReq04);

        TableColumn<OutputView, String> colReq05 = new TableColumn<>("Май");
        colReq05.setMinWidth(10);
        colReq05.setCellValueFactory(new PropertyValueFactory<OutputView, String>("req05"));
        table.getColumns().add(colReq05);

        TableColumn<OutputView, String> colReq06 = new TableColumn<>("Июнь");
        colReq06.setMinWidth(10);
        colReq06.setCellValueFactory(new PropertyValueFactory<OutputView, String>("req06"));
        table.getColumns().add(colReq06);

        TableColumn<OutputView, String> colReq07 = new TableColumn<>("Июль");
        colReq07.setMinWidth(10);
        colReq07.setCellValueFactory(new PropertyValueFactory<OutputView, String>("req07"));
        table.getColumns().add(colReq07);

        TableColumn<OutputView, String> colReq08 = new TableColumn<>("Август");
        colReq08.setMinWidth(10);
        colReq08.setCellValueFactory(new PropertyValueFactory<OutputView, String>("req08"));
        table.getColumns().add(colReq08);

        TableColumn<OutputView, String> colReq09 = new TableColumn<>("Сентябрь");
        colReq09.setMinWidth(10);
        colReq09.setCellValueFactory(new PropertyValueFactory<OutputView, String>("req09"));
        table.getColumns().add(colReq09);

        TableColumn<OutputView, String> colReq10 = new TableColumn<>("Октябрь");
        colReq10.setMinWidth(10);
        colReq10.setCellValueFactory(new PropertyValueFactory<OutputView, String>("req10"));
        table.getColumns().add(colReq10);

        TableColumn<OutputView, String> colReq11 = new TableColumn<>("Ноябрь");
        colReq11.setMinWidth(10);
        colReq11.setCellValueFactory(new PropertyValueFactory<OutputView, String>("req11"));
        table.getColumns().add(colReq11);

        TableColumn<OutputView, String> colReq12 = new TableColumn<>("Декабрь");
        colReq12.setMinWidth(10);
        colReq12.setCellValueFactory(new PropertyValueFactory<OutputView, String>("req12"));
        table.getColumns().add(colReq12);

        // Semaphore of table rows
        table.setRowFactory(tableView -> changeColorsRow());
        // End of table populating

        final VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 0, 0, 10));

        final HBox hBox = new HBox();

        buttonExport.setId("enter-button");
        buttonExport.addEventHandler(MouseEvent.MOUSE_MOVED,
                event -> addMovedEvents(buttonExport));
        // Click mouse on the export button.
        buttonExport.setOnAction(event -> clickMouseOnTheExportButton());

        hBox.setSpacing(5);
        hBox.setPadding(new Insets(10, 0, 0, 10));
        hBox.getChildren().addAll(buttonExport);

        vBox.getChildren().addAll(TOPLABEL, table, hBox);

        ((Group) scene.getRoot()).getChildren().addAll(vBox);
        primaryStage.setScene(scene);
        scene.getStylesheets().add
                (OutputForm.class.getResource("/css/window.css").toExternalForm());
        primaryStage.showAndWait();

    } // end Stop

    private ObservableList<OutputView> buildOutputTable() {

        ObservableList<OutputView> result = FXCollections.observableArrayList();

        Iterator<Map.Entry<Integer, CrossTable>> it = totalData.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry<Integer, CrossTable> entry = it.next();
            OutputView crossTable = new OutputView();
            crossTable.setMaterial(entry.getValue().getMaterial());
            crossTable.setMaterialDescription(entry.getValue().getMaterialDescription());

            crossTable.setWerks(entry.getValue().getWerks());
            crossTable.setYear(entry.getValue().getYear());

            if (entry.getValue().getPurchaseGroup() == null)
                crossTable.setPurchaseGroup("gag");
            else
                crossTable.setPurchaseGroup(entry.getValue().getPurchaseGroup());

            if (entry.getValue().getNameOfPurchaseGroup() == null)
                crossTable.setNameOfPurchaseGroup("gag");
            else
                crossTable.setNameOfPurchaseGroup(entry.getValue().getNameOfPurchaseGroup());

            crossTable.setBaseUnit(entry.getValue().getBaseUnit());

            if (entry.getValue().getStocks() == null)
                crossTable.setStocks("0.000");
            else crossTable.setStocks(entry.getValue().getStocks().toString());

            // Requirements
            if (entry.getValue().getReq01() == null) crossTable.setReq01("0.000");
            else crossTable.setReq01(entry.getValue().getReq01().toString());

            if (entry.getValue().getReq02() == null) crossTable.setReq02("0.000");
            else crossTable.setReq02(entry.getValue().getReq02().toString());

            if (entry.getValue().getReq03() == null) crossTable.setReq03("0.000");
            else crossTable.setReq03(entry.getValue().getReq03().toString());

            if (entry.getValue().getReq04() == null) crossTable.setReq04("0.000");
            else crossTable.setReq04(entry.getValue().getReq04().toString());

            if (entry.getValue().getReq05() == null) crossTable.setReq05("0.000");
            else crossTable.setReq05(entry.getValue().getReq05().toString());

            if (entry.getValue().getReq06() == null) crossTable.setReq06("0.000");
            else crossTable.setReq06(entry.getValue().getReq06().toString());

            if (entry.getValue().getReq07() == null) crossTable.setReq07("0.000");
            else crossTable.setReq07(entry.getValue().getReq07().toString());

            if (entry.getValue().getReq08() == null) crossTable.setReq08("0.000");
            else crossTable.setReq08(entry.getValue().getReq08().toString());

            if (entry.getValue().getReq09() == null) crossTable.setReq09("0.000");
            else crossTable.setReq09(entry.getValue().getReq09().toString());

            if (entry.getValue().getReq10() == null) crossTable.setReq10("0.000");
            else crossTable.setReq10(entry.getValue().getReq10().toString());

            if (entry.getValue().getReq11() == null) crossTable.setReq11("0.000");
            else crossTable.setReq11(entry.getValue().getReq11().toString());

            if (entry.getValue().getReq12() == null) crossTable.setReq12("0.000");
            else crossTable.setReq12(entry.getValue().getReq12().toString());

            result.add(crossTable);
        }

        return result;
    } // build output list

    private TableRow<OutputView> changeColorsRow() {
        TableRow<OutputView> row = new TableRow<>();
        return row;
    }

    // Visual effect
    private ActionEvent addMovedEvents(Button btn) {

        ActionEvent event = new ActionEvent();
        DropShadow shadow = new DropShadow();
        btn.setEffect(shadow);

        return event;
    }

    private void clickMouseOnTheExportButton() {

        ExcelExporter exporter = ExcelExporter.getInstance();
        String fileName, tableName;

        Date now = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        try {

            fileName = "export-" + fmt.format(now) + ".xls";
            tableName = fmt.format(now);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Экспорт данных в Excel");
            alert.setHeaderText("Подтвердите действие");
            alert.setContentText("Сохранить данные в файл " + fileName + "?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                exporter.exportDataToExcelFile(fileName, tableName, observableList);
            }

        } catch (IOException | ClassNotFoundException fmex) {

            callExeptionAlertBox(fmex.getMessage());

        }

    }

    private void callExeptionAlertBox(String message) {

    }

    // click and enter event on the button
//    private EventHandler<ActionEvent> addEnterEvent(Button btn) {
//
//        EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//
//            }
//        };
//
//      return handler;
//    }

}
