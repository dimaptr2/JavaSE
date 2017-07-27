package ru.velkomfood.fin.cash.logon;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.velkomfood.fin.cash.logon.controller.DbReader;
import ru.velkomfood.fin.cash.logon.controller.FiscalDevice;
import ru.velkomfood.fin.cash.logon.model.CashDocView;
import ru.velkomfood.fin.cash.logon.model.CashDocument;
import ru.velkomfood.fin.cash.logon.model.FiscalHead;
import ru.velkomfood.fin.cash.logon.model.FiscalItem;
import ru.velkomfood.fin.cash.logon.view.ReceiptUI;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by dpetrov on 18.07.17.
 */
public class StartUIController {

    private Stage stage;
    private FiscalDevice device;
    private DbReader db;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setDevice(FiscalDevice device) {
        this.device = device;
    }
    public void setDb(DbReader db) {
        this.db = db;
    }

    @FXML
    private HBox topHBox;
    @FXML
    private VBox leftVBox;

    @FXML
    private DatePicker atDate;
    @FXML
    private Button btnReadDocs;
    @FXML
    private Button btnOpenSession;
    @FXML
    private Button btnCloseSession;
    @FXML
    private Button btnXReport;
    @FXML
    private Button btnAboutDevice;
    @FXML
    private Button btnQuit;

    private ObservableList<CashDocView> docsData = FXCollections.observableArrayList();

    @FXML
    private TableView<CashDocView> contentCashDocs;
    // Table fields
    @FXML
    private TableColumn<CashDocView, String> counter;
    @FXML
    private TableColumn<CashDocView, String> id;
    @FXML
    private TableColumn<CashDocView, String> postingDate;
    @FXML
    private TableColumn<CashDocView, String> positionText;
    @FXML
    private TableColumn<CashDocView, String> deliveryId;
    @FXML
    private TableColumn<CashDocView, String> amount;
    @FXML
    private TableColumn<CashDocView, String> receiptAmount;
    @FXML
    private TableColumn<CashDocView, String> difference;


    // Methods

    @FXML
    private void initialize() {
        counter.setCellValueFactory(cell -> cell.getValue().counterProperty());
        id.setCellValueFactory(cell -> cell.getValue().idProperty());
        postingDate.setCellValueFactory(cell -> cell.getValue().postingDateProperty());
        positionText.setCellValueFactory(cell -> cell.getValue().positionTextProperty());
        deliveryId.setCellValueFactory(cell -> cell.getValue().deliveryIdProperty());
        amount.setCellValueFactory(cell -> cell.getValue().amountProperty());
        receiptAmount.setCellValueFactory(cell -> cell.getValue().receiptAmountProperty());
        difference.setCellValueFactory(cell -> cell.getValue().differenceProperty());
        contentCashDocs.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    // Press on the reading push button
    public void readCashDocuments(ActionEvent event) {

        String moment = String.valueOf(atDate.getValue());

        if (!docsData.isEmpty()) {
            docsData.clear();
        }

        try {
            List<CashDocument> docs = db.readCashDocumentsByDate(moment);
            if (docs != null && !docs.isEmpty()) {
                int index = 0;
                for (CashDocument doc: docs) {
                    index++;
                    CashDocView view = new CashDocView();
                    view.setCounter(String.valueOf(index));
                    view.setId(String.valueOf(doc.getId()));
                    view.setPostingDate(String.valueOf(doc.getPostingDate()));
                    view.setPositionText(doc.getPositionText());
                    long key = doc.getDeliveryId();
                    view.setDeliveryId(String.valueOf(key));
                    view.setAmount(String.valueOf(doc.getAmount().doubleValue()));
                    FiscalHead head = db.readFiscalGeneralDataByDeliveryId(key);
                    if (head != null) {
                        view.setReceiptAmount(String.valueOf(head.getAmount()));
                    }
                    BigDecimal diff = head.getAmount().subtract(doc.getAmount())
                            .setScale(2, BigDecimal.ROUND_FLOOR);
                    view.setDifference(String.valueOf(diff));
                    docsData.add(view);
                }
                contentCashDocs.setItems(docsData);
            }
        } catch (SQLException sqe) {

            showErrorDialog(sqe.getMessage());

        }

    }

    public void onLoadDocument(ActionEvent event) {
        if (event.getSource().equals(atDate)) {

        }
    }

    public void openFiscalSession(ActionEvent event) {
        device.printZReport();
    }

    public void closeFiscalSession(ActionEvent event) {

    }

    public void getReportWithoutClearing(ActionEvent event) {
        device.printXReport();
    }

    public void testFiscalDevice(ActionEvent event) {
        device.showDeviceProperties();
    }
    // Close the window
    public void closeWindow(ActionEvent event) {
        quit();
    }

    @FXML
    public void mouseClickHandleOnTableRow(MouseEvent mouseEvent) {

        CashDocView cashDocView = contentCashDocs.getSelectionModel().getSelectedItem();

        switch (mouseEvent.getClickCount()) {
            case 1:
                break;
            case 2:
                displayReceiptView(cashDocView);
                break;
        }

    }

    @FXML
    public void pressKeyHandleEvent(KeyEvent event) {
        handleKey(event);
    }

    // Process the key codes (ENTER, SPACE and etc)
    private void handleKey(KeyEvent event) {

        Object source = event.getSource();
        KeyCode code = event.getCode();

        if (source.equals(btnQuit)) {

            if (code.equals(KeyCode.ENTER)) {
                quit();
            }

        } else if (source.equals(contentCashDocs)) {

            if (code.equals(KeyCode.ENTER)) {
                CashDocView cashDocView = contentCashDocs.getSelectionModel().getSelectedItem();
                displayReceiptView(cashDocView);
            }

        } else if (source.equals(btnAboutDevice)) {

            if (code.equals(KeyCode.ENTER)) {
                device.showDeviceProperties();
            }

        }

    } // handle key codes

    // Quit the application
    private void quit() {
        stage.close();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error dialog");
        alert.setHeaderText("Сбой в работе программы");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Display the selected document
    private void displayReceiptView(CashDocView view) {

        ReceiptUI receiptUI = new ReceiptUI();
        long cashDocId = Long.parseLong(view.getId());
        long delivery = Long.parseLong(view.getDeliveryId());

        try {

            CashDocument cDoc = db.readCashDocumentById(cashDocId);
            if (cDoc != null) {
                receiptUI.setCashDocument(cDoc);
                FiscalHead hd = db.readFiscalGeneralDataByDeliveryId(delivery);
                if (hd != null) {
                    receiptUI.setDevice(device);
                    receiptUI.setDbReader(db);
                    receiptUI.setHead(hd);
                    List<FiscalItem> its = db.buildFiscalDocumentByDeliveryId(delivery);
                    if (its != null && !its.isEmpty()) {
                        receiptUI.setItems(its);
                    }
                    receiptUI.display();
                }
            }

        } catch (Exception e) {

            showErrorDialog(e.getMessage());

        }
    }

}
