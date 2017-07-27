package ru.velkomfood.fin.cash.logon.controller;

import com.atol.drivers.DriverException;
import com.atol.drivers.fptr.Fptr;
import com.atol.drivers.fptr.IFptr;
import javafx.scene.control.Alert;
import ru.velkomfood.fin.cash.logon.model.CashDocument;
import ru.velkomfood.fin.cash.logon.model.FiscalHead;
import ru.velkomfood.fin.cash.logon.model.FiscalItem;

import java.util.List;

/**
 * Created by dpetrov on 18.07.17.
 */
public class FiscalDevice {

    private static FiscalDevice instance;
    private IFptr fptr;
    private DeviceStatus deviceStatus;

    private FiscalDevice() {
        fptr = new Fptr();
        fptr.create();
    }

    public void setDeviceStatus(DeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    // Run before instantiating this class
    public void initFiscalDevice() {

        int rc;

        try {
            rc = fptr.put_DeviceSingleSetting(IFptr.SETTING_DEVICENAME, "MYATOL25F");
            if (rc < 0) {
                checkError(fptr);
            } else {
                deviceStatus.doOkStatus();
            }

            rc = fptr.put_DeviceSingleSetting(IFptr.SETTING_PORT, IFptr.SETTING_PORT_USB);
            if (rc < 0) {
                checkError(fptr);
            } else {
                deviceStatus.doOkStatus();
            }

            rc = fptr.put_DeviceSingleSetting(IFptr.SETTING_VID, 0x2912);
            if (rc < 0) {
                checkError(fptr);
            } else {
                deviceStatus.doOkStatus();
            }

            rc = fptr.put_DeviceSingleSetting(IFptr.SETTING_PID, 0x0005);
            if (rc < 0) {
                checkError(fptr);
            } else {
                deviceStatus.doOkStatus();
            }

            rc = fptr.put_DeviceSingleSetting(IFptr.SETTING_MODEL, IFptr.MODEL_ATOL_25F);
            if (rc < 0) {
                checkError(fptr);
            } else {
                deviceStatus.doOkStatus();
            }

            rc = fptr.put_DeviceSingleSetting(IFptr.SETTING_BAUDRATE, 115200);
            if (rc < 0) {
                checkError(fptr);
            } else {
                deviceStatus.doOkStatus();
            }

            rc = fptr.ApplySingleSettings();
            if (rc < 0) {
                checkError(fptr);
            } else {
                deviceStatus.doOkStatus();
            }

            // Connect to the fiscal device
            rc = fptr.put_DeviceEnabled(true);
            if (rc < 0) {
                checkError(fptr);
            } else {
                deviceStatus.doOkStatus();
            }

            rc = fptr.GetStatus();
            if (rc < 0) {
                checkError(fptr);
            } else {
                deviceStatus.doOkStatus();
            }

        } catch (DriverException e) {

            e.printStackTrace();

        }
    }

    public static FiscalDevice getInstance() {
        if (instance == null) {
            instance = new FiscalDevice();
        }
        return instance;
    }

    public DeviceStatus getDeviceStatus() {
        return deviceStatus;
    }

    public void showDeviceProperties() {

        if (deviceStatus != null && deviceStatus.getCurrentState() < 0) {
            showDeviceStatus(deviceStatus);
        } else if (deviceStatus != null && deviceStatus.getCurrentState() == 0) {
            if (fptr.ShowProperties() < 0) {
                try {
                    checkError(fptr);
                } catch (DriverException e) {
                    showMessage(Alert.AlertType.ERROR, e.getMessage());
                }
            }
        }

    }

    public void openFNSession() {

    }

    public void printFiscalDocument(List<FiscalItem> items) {

    }

    public void openFiscalReceipt() {

    }

    public void createFiscalItems(CashDocument cashDoc, FiscalHead head, List<FiscalItem> items) {

    }

    public void closeFiscalReceipt() {

    }

    public void changeReceiptStatus(CashDocument cashDoc) {

        if (deviceStatus != null && deviceStatus.getCurrentState() == 0) {

        }

    }

   // Z report
    public void printZReport() {

    }

    // X report
    public void printXReport() {

    }

    // Operations with the receipts

    private void checkError(IFptr fptr) throws DriverException {
        String rd = "";
        int rc = fptr.get_ResultCode();
        deviceStatus.setCurrentState(rc);
        if (rc < 0) {
            rd = fptr.get_ResultDescription();
            deviceStatus.setStatus(rd);
            String badParameter = null;
            if (rc == -6) {
                badParameter = fptr.get_BadParamDescription();
            }

        }
        showConsoleMessage(rd);
    }

    private void showConsoleMessage(String message) {
        System.out.println(message);
    }

    private void openCheck(IFptr fptr, int type) throws DriverException {

        if (fptr.put_Mode(IFptr.MODE_REGISTRATION) < 0)
            checkError(fptr);
        else
            deviceStatus.doOkStatus();

        if (fptr.SetMode() < 0)
            checkError(fptr);
        else
            deviceStatus.doOkStatus();

        if (fptr.put_CheckType(type) < 0)
            checkError(fptr);
        else
            deviceStatus.doOkStatus();

        if (fptr.OpenCheck() < 0)
            checkError(fptr);
        else
            deviceStatus.doOkStatus();

    }

    private void closeCheck(IFptr fptr, int type) throws DriverException {

        if (fptr.put_TypeClose(type) < 0)
            checkError(fptr);
        else
            deviceStatus.doOkStatus();

        if (fptr.CloseCheck() < 0)
            checkError(fptr);
        else
            deviceStatus.doOkStatus();

    }

    private void readFiscalMemoryByKey(long key) {

    }

    // Build a message of errors
    private void processError(int runCode) {

        String line;

        if (runCode < 0) {
            line = fptr.get_ResultDescription();
            System.out.println(line);
        }

    }

    private void showDeviceStatus(DeviceStatus devStat) {

        Alert.AlertType messageType;

        if (devStat.getCurrentState() < 0) {
            messageType = Alert.AlertType.ERROR;
        } else {
            messageType = Alert.AlertType.INFORMATION;
        }

        showMessage(messageType, devStat.getStatus());

    }

    private void showMessage(Alert.AlertType mType, String message) {

        Alert alert = new Alert(mType);

        if (mType.equals(Alert.AlertType.CONFIRMATION)) {
            alert.setTitle("Confirmation");
            alert.setHeaderText("Confirmation!");
        } else if (mType.equals(Alert.AlertType.ERROR)) {
            alert.setTitle("Error");
            alert.setHeaderText("Error!");
        } else if (mType.equals(Alert.AlertType.INFORMATION)) {
            alert.setTitle("Information");
            alert.setHeaderText("Information!");
        } else if (mType.equals(Alert.AlertType.WARNING)) {
            alert.setTitle("Warning");
            alert.setHeaderText("Warning!");
        }

        alert.setContentText(message);
        alert.showAndWait();

    }


}
