package ru.velkomfood.mrp.client.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.velkomfood.mrp.client.config.ConfigGenerator;
import ru.velkomfood.mrp.client.model.CrossTable;
import ru.velkomfood.mrp.client.model.MaterialCache;
import ru.velkomfood.mrp.client.model.MaterialRequirements;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by DPetrov on 15.08.2016.
 */
public class StartForm extends Application {

    private static StartForm instance;
    private Connection connection;
    private GridPane grid;
    private DatePicker datePicker;
    private TextField fromMatnr, toMatnr, purchaseGroup;
    private Button runButton;
    private MaterialCache cache;
    private DialogBoxListMaterials matSearchDialog;
    private int indexOfList;
    private final Text actiontTarget = new Text();

    private List<MaterialRequirements> requirements;
    private Map<Integer, CrossTable> outputs;

    private StartForm() {

        requirements = new ArrayList<>();
        outputs = new ConcurrentSkipListMap<>();

    }

    public static StartForm getInstance() {
        if (instance == null) instance = new StartForm();
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Stage secondWindow = primaryStage;

        secondWindow.initModality(Modality.APPLICATION_MODAL);
        secondWindow.setMaxWidth(450.00);
        secondWindow.setMaxHeight(500.00);

        ConfigGenerator configGenerator = ConfigGenerator.getInstance();
        cache = configGenerator.materialCache();

        grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 400, 375);

        Text sceneTitle = new Text("Выбор данных ППМ");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        sceneTitle.setId("scene-text");
        grid.add(sceneTitle, 0, 0, 2, 1);


        // Add basic controls
        grid.add(addLabel("Начальная дата"), 0, 1);
        datePicker = addDatePicker();
        grid.add(datePicker, 1, 1);

        grid.add(addLabel("С материала"), 0, 2);
        fromMatnr = addTextField("000000000010000000");
        fromMatnr.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            matSearchDialog = new DialogBoxListMaterials(connection);
            try {
                actiontTarget.setText("");
                matSearchDialog.display();
                indexOfList = cache.getIndexOfMaterial();
                fromMatnr.setText(cache.getMaterialList().get(indexOfList).getId());
            } catch (Exception mdiaex) {
                actiontTarget.setText(mdiaex.getMessage());
            }
        });
        grid.add(fromMatnr, 1, 2);

        grid.add(addLabel("По материал"), 0, 3);
        toMatnr = addTextField("000000000019999999");
        grid.add(toMatnr, 1, 3);
        toMatnr.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            matSearchDialog = new DialogBoxListMaterials(connection);
            try {
                actiontTarget.setText("");
                matSearchDialog.display();
                indexOfList = cache.getIndexOfMaterial();
                toMatnr.setText(cache.getMaterialList().get(indexOfList).getId());
            } catch (Exception mdiaex) {
                actiontTarget.setText(mdiaex.getMessage());
            }
        });

        grid.add(addLabel("Группа закупок"), 0, 4);
        purchaseGroup = addTextField("003");
        grid.add(purchaseGroup, 1, 4);

        runButton = addRunButton("Выполнить");
        grid.add(runButton, 1, 5);

        actiontTarget.setId("action-target");
        grid.add(actiontTarget, 0, 7);

        secondWindow.setScene(scene);
        scene.getStylesheets().add
                (StartForm.class.getResource("/css/window.css").toExternalForm());
        secondWindow.setTitle("Отчет ППМ");
        secondWindow.show();

    } // end of the starting method

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void stop() {

        try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
            }
        } catch (SQLException sqex) {
            sqex.printStackTrace();
        }
    }

    private DatePicker addDatePicker() {

        DatePicker dp = new DatePicker();
        DropShadow shadow = new DropShadow();

        dp.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
           dp.setEffect(shadow);
        });

        return dp;
    }

    private Label addLabel(String labelText) {
        return new Label(labelText);
    }
    private TextField addTextField(String initialText) {

        TextField textField = new TextField();

        DropShadow shadow = new DropShadow();
        textField.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            textField.setEffect(shadow);
        });

        textField.setText(initialText);

        return textField;
    }

    private Button addRunButton(String btnText) {

        Button btnRun = new Button(btnText);
        btnRun.setId("enter-button");

        DropShadow shadow = new DropShadow();
        btnRun.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            btnRun.setEffect(shadow);
        });

        btnRun.setOnAction(event -> {

            actiontTarget.setFill(Color.FIREBRICK);
            try {
                actiontTarget.setText("");
                executeDatabaseQuery();
                if (!outputs.isEmpty()) {
                    actiontTarget.setFill(Color.AQUAMARINE);
                    actiontTarget.setText("Выбрано объектов: " + outputs.size());
                } else {
                    actiontTarget.setText("Выбрано объектов: 0");
                }
                OutputForm outputForm = new OutputForm(datePicker.getValue().getYear(),
                        outputs);
                outputForm.display();
            } catch (Exception sqex) {
                actiontTarget.setText(sqex.getMessage());
            }

        });

        return btnRun;
    }

    // Query to the database called "sapdata" on the host "srv-sapapp.eatmeat.ru"
    private void executeDatabaseQuery() throws SQLException {

        if (!requirements.isEmpty()) requirements.clear();
        if (!outputs.isEmpty()) outputs.clear();

        Statement stmt = connection.createStatement();

        try {
            for (int m = 1; m <= 12; m++) {
                String sql = "SELECT matnr, maktx, werks, year, month, pur_group,";
                sql = sql + " description, base_unit, plantstk, quantity";
                sql = sql + " FROM requirements";
                sql = sql + " WHERE matnr BETWEEN " + "\'" + fromMatnr.getText() + "\'";
                sql = sql + " AND " + "\'" + toMatnr.getText() + "\'";
                String purGroup = purchaseGroup.getText();
                if (purGroup.equals("") || purGroup == null) {
                    purGroup = " AND pur_group IS NULL";
                    sql = sql + purGroup;
                } else {
                    sql = sql + " AND pur_group = " + "\'" + purchaseGroup.getText() + "\'";
                }
                sql = sql + " AND year = " + datePicker.getValue().getYear();
                sql = sql + " AND month = " + m;
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    CrossTable crossTable = new CrossTable();
                    crossTable.setMaterial(rs.getString("matnr"));
                    crossTable.setMaterialDescription(rs.getString("maktx"));
                    crossTable.setWerks(rs.getInt("werks"));
                    crossTable.setYear(rs.getInt("year"));
                    crossTable.setPurchaseGroup(rs.getString("pur_group"));
                    crossTable.setNameOfPurchaseGroup(rs.getString("description"));
                    crossTable.setBaseUnit(rs.getString("base_unit"));
                    crossTable.setStocks(rs.getBigDecimal("plantstk"));
                    switch (rs.getInt("month")) {
                        case 1:
                            crossTable.setReq01(rs.getBigDecimal("quantity"));
                            break;
                        case 2:
                            crossTable.setReq02(rs.getBigDecimal("quantity"));
                            break;
                        case 3:
                            crossTable.setReq03(rs.getBigDecimal("quantity"));
                            break;
                        case 4:
                            crossTable.setReq04(rs.getBigDecimal("quantity"));
                            break;
                        case 5:
                            crossTable.setReq05(rs.getBigDecimal("quantity"));
                            break;
                        case 6:
                            crossTable.setReq06(rs.getBigDecimal("quantity"));
                            break;
                        case 7:
                            crossTable.setReq07(rs.getBigDecimal("quantity"));
                            break;
                        case 8:
                            crossTable.setReq08(rs.getBigDecimal("quantity"));
                            break;
                        case 9:
                            crossTable.setReq09(rs.getBigDecimal("quantity"));
                            break;
                        case 10:
                            crossTable.setReq10(rs.getBigDecimal("quantity"));
                            break;
                        case 11:
                            crossTable.setReq11(rs.getBigDecimal("quantity"));
                            break;
                        case 12:
                            crossTable.setReq12(rs.getBigDecimal("quantity"));
                            break;
                    }

                    int materialNumber = Integer.parseInt(crossTable.getMaterial().replaceFirst("^0*", ""));

                    if (outputs.containsKey(materialNumber)) {
                        switch (rs.getInt("month")) {
                            case 1:
                                outputs.get(materialNumber).setReq01(crossTable.getReq01());
                                break;
                            case 2:
                                outputs.get(materialNumber).setReq02(crossTable.getReq02());
                                break;
                            case 3:
                                outputs.get(materialNumber).setReq03(crossTable.getReq03());
                                break;
                            case 4:
                                outputs.get(materialNumber).setReq04(crossTable.getReq04());
                                break;
                            case 5:
                                outputs.get(materialNumber).setReq05(crossTable.getReq05());
                                break;
                            case 6:
                                outputs.get(materialNumber).setReq06(crossTable.getReq06());;
                                break;
                            case 7:
                                outputs.get(materialNumber).setReq07(crossTable.getReq07());
                                break;
                            case 8:
                                outputs.get(materialNumber).setReq08(crossTable.getReq08());
                                break;
                            case 9:
                                outputs.get(materialNumber).setReq09(crossTable.getReq09());
                                break;
                            case 10:
                                outputs.get(materialNumber).setReq10(crossTable.getReq10());
                                break;
                            case 11:
                                outputs.get(materialNumber).setReq11(crossTable.getReq11());
                                break;
                            case 12:
                                outputs.get(materialNumber).setReq12(crossTable.getReq12());
                                break;
                        }
                    } else {
                        outputs.put(materialNumber, crossTable);
                    }
                } // end of result set
                rs.close();
            } // end of cycle by periods
        } finally {
            stmt.close();
        }

//        char debug = 'X';
//        if (outputs.isEmpty()) debug = 'Y';
//        else debug = 'N';

    }

}
