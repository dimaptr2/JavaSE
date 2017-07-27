package ru.velkomfood.fin.cash.logon.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.BigDecimalStringConverter;
import ru.velkomfood.fin.cash.logon.controller.DbReader;
import ru.velkomfood.fin.cash.logon.controller.DeviceStatus;
import ru.velkomfood.fin.cash.logon.controller.FiscalDevice;
import ru.velkomfood.fin.cash.logon.model.CashDocument;
import ru.velkomfood.fin.cash.logon.model.FiscalHead;
import ru.velkomfood.fin.cash.logon.model.FiscalItem;
import ru.velkomfood.fin.cash.logon.model.FiscalItemView;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ReceiptUI extends Application {

    private Stage window;
    private FiscalDevice device;
    private CashDocument cashDocument;
    private FiscalHead head;
    private DbReader dbReader;
    private VBox contentVBox;
    private HBox bottomHBox, btnHBox;


    // Horizontal box elements
    private Label labelReceiptAmount;
    private TextField txtReceiptAmount;
    private Label labelDeliveryAmount;
    private TextField txtDeliveryAmount;
    private Label labelDelta;
    private TextField txtDelta;
    private Button btnPrint, btnReturn, btnQuit;

    private Scene scene;
    private List<FiscalItem> items;
    private TableView<FiscalItemView> tableView;

    // Table columns
    private TableColumn<FiscalItemView, String> colCounter, colMaterial, colDescription,
            colQuantity, colPrice, colVatRate;

    public ReceiptUI() {

        window = new Stage();
        window.setTitle("Кассовый чек");
        window.setWidth(800);
        window.setHeight(650);
//        window.setMaximized(true);
        window.setResizable(false);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setFullScreen(false);

    }


    public void setDevice(FiscalDevice device) {
        this.device = device;
    }

    public void setCashDocument(CashDocument cashDocument) {
        this.cashDocument = cashDocument;
    }

    public void setHead(FiscalHead head) {
        this.head = head;
    }

    public void setDbReader(DbReader dbReader) {
        this.dbReader = dbReader;
    }

    public void setItems(List<FiscalItem> items) {
        this.items = items;
    }

    public void display() throws Exception {
        start(window);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        showView(primaryStage);
    }

    public void showView(Stage stage) {

        scene = new Scene(new Group());
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        String txtValue = "ПКО № " + cashDocument.getId() + " Накладная № " + head.getId();

        final Label sign = new Label(txtValue);
        sign.getStyleClass().add("top-label");

        tableView = new TableView<>();
        tableView.setPrefWidth(700);
        ObservableList<FiscalItemView> observableList = fillObservableList();
        createTableColumns(tableView);
        tableView.setItems(observableList);

        contentVBox = new VBox();
        contentVBox.setSpacing(5);
        contentVBox.setPadding(new Insets(5, 10, 5, 10));

        bottomHBox = new HBox();
        bottomHBox.setSpacing(5);
        bottomHBox.setPadding(new Insets(5, 10, 5, 10));
        bottomHBox.setAlignment(Pos.CENTER_LEFT);

        btnHBox = new HBox();
        btnHBox.setSpacing(5);
        btnHBox.setPadding(new Insets(5, 10, 5, 10));
        btnHBox.setAlignment(Pos.CENTER_LEFT);

        createScreenElements();
        bottomHBox.getChildren().addAll(
                labelReceiptAmount, txtReceiptAmount,
                labelDeliveryAmount, txtDeliveryAmount,
                labelDelta, txtDelta
        );
        btnHBox.getChildren().addAll(btnPrint, btnReturn, btnQuit);

        contentVBox.getChildren().addAll(sign, tableView, bottomHBox, btnHBox);

        ((Group) scene.getRoot()).getChildren().add(contentVBox);
        stage.setScene(scene);
        stage.showAndWait();

    }

    private ObservableList<FiscalItemView> fillObservableList() {

        ObservableList<FiscalItemView> observableListItems = FXCollections.observableArrayList();

        int counter = 0;
        Iterator<FiscalItem> it = items.iterator();

        while (it.hasNext()) {
            FiscalItem fi = it.next();
            counter++;
            FiscalItemView view = new FiscalItemView(
                    String.valueOf(counter), String.valueOf(fi.getMaterialId()),
                    fi.getDescription(), String.valueOf(fi.getQuantity()),
                    String.valueOf(fi.getPrice()), String.valueOf(fi.getVatRate())

            );
            observableListItems.add(view);
        }

        return observableListItems;
    }

    private void createTableColumns(TableView<FiscalItemView> table) {

        colCounter = new TableColumn<>("Номер");
        colCounter.setMinWidth(18);
        colCounter.setCellValueFactory(new PropertyValueFactory<>("counter"));
        table.getColumns().add(colCounter);

        colMaterial = new TableColumn<>("Артикул");
        colMaterial.setMinWidth(18);
        colMaterial.setCellValueFactory(new PropertyValueFactory<>("materialId"));
        table.getColumns().add(colMaterial);

        colDescription = new TableColumn<>("Описание");
        colDescription.setMinWidth(40);
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        table.getColumns().add(colDescription);

        colQuantity = new TableColumn<>("Количество");
        colQuantity.setMinWidth(18);
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        table.getColumns().add(colQuantity);

        colPrice = new TableColumn<>("Цена за ЕИ");
        colPrice.setMinWidth(18);
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        table.getColumns().add(colPrice);

        colVatRate = new TableColumn<>("НДС");
        colVatRate.setMinWidth(10);
        colVatRate.setCellValueFactory(new PropertyValueFactory<>("vatRate"));
        table.getColumns().add(colVatRate);

    }

    private void createScreenElements() {

        final Font ROMAN14 = new Font("LM Roman 8", 14);
        final double WIDTH = 128.0;

        labelReceiptAmount = new Label("Сумма наличными: ");
        labelReceiptAmount.getStyleClass().add("sign-label");

        txtReceiptAmount = new TextField();
        txtReceiptAmount.setEditable(true);
        txtReceiptAmount.setPrefWidth(WIDTH);
        txtReceiptAmount.setText(String.valueOf(cashDocument.getAmount()));
        BigDecimal sum = cashDocument.getAmount();
        if (sum == null) {
            sum = new BigDecimal(0.00);
        }
        txtReceiptAmount.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter()));
        txtReceiptAmount.setText(String.valueOf(sum));


        labelDeliveryAmount = new Label("Итого: ");
        labelDeliveryAmount.getStyleClass().add("sign-label");

        txtDeliveryAmount = new TextField();
        txtDeliveryAmount.setEditable(false);
        txtDeliveryAmount.setPrefWidth(WIDTH);
        txtDeliveryAmount.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter()));
        txtDeliveryAmount.setText(String.valueOf(head.getAmount()));

        labelDelta = new Label("Сальдо: ");
        labelDelta.getStyleClass().add("sign-label");

        txtDelta = new TextField();
        txtDelta.setEditable(false);
        txtDelta.setPrefWidth(WIDTH);
        txtDelta.setTextFormatter(new TextFormatter<>(new BigDecimalStringConverter()));
        BigDecimal delta = head.getAmount().subtract(sum);
        txtDelta.setText(String.valueOf(delta));

        btnPrint = new Button("Печать");
        btnPrint.setOnKeyPressed(event -> handleKeyEvent(event));
        btnPrint.setOnAction(event -> printReceipt());

        btnReturn = new Button("Возврат");
        btnReturn.setOnAction(event -> doReturnFiscalDocumentById(cashDocument.getId()));

        btnQuit = new Button("Закрыть");
        btnQuit.setOnKeyPressed(event -> handleKeyEvent(event));
        btnQuit.setOnAction(event -> closeWindow(window));

    }

    private void handleKeyEvent(KeyEvent event) {

        Object source = event.getSource();
        KeyCode keyCode = event.getCode();

        if (source.equals(btnPrint)) {

            if (keyCode.equals(KeyCode.ENTER)) {
                printReceipt();
            }

        } else if (source.equals(btnReturn)) {

            if (keyCode.equals(KeyCode.ENTER)) {
                doReturnFiscalDocumentById(cashDocument.getId());
            }

        }
        else if (source.equals(btnQuit)) {

            if (keyCode.equals(KeyCode.ENTER)) {
                closeWindow(window);
            }

        }

    } // handle key events

    private void printReceipt() {

        int counter;

        if (device.getDeviceStatus().getCurrentState() == 0) {
            device.openFiscalReceipt();
            device.createFiscalItems(cashDocument, head, items);
            device.closeFiscalReceipt();
            try {
                counter = dbReader.updateCashDocumentStatus(cashDocument);
                showInformationDialog("Обновлено " + counter + " документов");
            } catch (SQLException sqex) {
                showErrorMessage(sqex.getMessage());
            }
        }

    }

    private void doReturnFiscalDocumentById(long id) {

        showInformationDialog("Ща! Как фикализируюсь!");
        if (device.getDeviceStatus().getCurrentState() == 0) {
            try {
                dbReader.recoverStatusByDocId(id);
            } catch (SQLException sqe) {
                showErrorMessage(sqe.getMessage());
            }
        } else {
           showErrorMessage(device.getDeviceStatus().getStatus());
        }
    }

    // Exit from the window and close it
    private void closeWindow(Stage stage) {
        stage.close();
    }

    private void showInformationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Information!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error!");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
