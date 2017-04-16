package ru.velkomfood.fin.cash.controller;

import com.shtrih.fiscalprinter.SMPrinterDeviceImpl;
import com.shtrih.fiscalprinter.command.FiscalPrinterParams;
import com.shtrih.fiscalprinter.port.SerialPrinterPort;
import drvfr_print.JavaApplication;
import drvfr_print.jpos.res.JVelPrinter;
import ru.velkomfood.fin.data.Receipt;

/**
 * Created by dpetrov on 03.04.2017.
 */
public class PrintEngine {

    private static PrintEngine instance;
    private SerialPrinterPort port;
    private JVelPrinter printer;
    private SMPrinterDeviceImpl dev;
    private FiscalPrinterParams params;

    private PrintEngine() {
        port = new SerialPrinterPort();
    }

    public static PrintEngine getInstance() {
        if (instance == null) {
            instance = new PrintEngine();
        }
        return instance;
    }

    public void openDevice() throws Exception {

        port.setPortName(JavaApplication.SPorts.COM1.name());
        port.setBaudRate(JavaApplication.SSpeed.S19200.getVal());
        port.open();
        dev = new SMPrinterDeviceImpl(port);
        params = new FiscalPrinterParams();
        printer = new JVelPrinter(dev, params);
        printer.connect();

    }

    public Receipt calculateSums() {

        Receipt receipt = new Receipt();

        if (isReady()) {

        }

        return receipt;
    }

    // getters
    public SerialPrinterPort getPort() {
        return port;
    }

    public JVelPrinter getPrinter() {
        return printer;
    }

    // Private section
    // What is a status?
    private boolean isReady() {

        boolean flag = false;

        if (port != null) {
            flag = true;
        }
        if (dev != null) {
            flag = true;
        }
        if (params != null) {
            flag = true;
        }
        return flag;

    }

}
