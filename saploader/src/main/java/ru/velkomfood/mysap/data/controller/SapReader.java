package ru.velkomfood.mysap.data.controller;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

public class SapReader {


    private LocalDate currentDate;
    private boolean flag;
    final static String DEST = "";
    final static String SUFFIX = ".jcoDestination";

    private Properties connProperties;
    private JCoDestination destination;

    public SapReader(boolean flag) {
        this.flag = flag;
        connProperties = new Properties();
        connProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "");
        connProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "");
        connProperties.setProperty(DestinationDataProvider.JCO_R3NAME, "");
        connProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "");
        connProperties.setProperty(DestinationDataProvider.JCO_USER, "");
        connProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "");
        connProperties.setProperty(DestinationDataProvider.JCO_LANG, "");
        createDestinationDataFile(DEST, connProperties);
    }

    private void createDestinationDataFile(String destName, Properties props) {

        File destCfg = new File(destName + SUFFIX);

        if (destCfg.isDirectory() || !destCfg.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(destCfg, false);
                props.store(fos, "Productive environment");
                fos.close();
            } catch (IOException fe) {
                fe.printStackTrace();
            }
        }

    }

    public boolean isFlag() {
        return flag;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public void run() {

        try {

            initSAPDestination();
            try {
                JCoContext.begin(destination);
            } finally {
                JCoContext.end(destination);
            }

        } catch (JCoException jcoe) {
            jcoe.printStackTrace();
        }

        System.out.println("SAP reader");
    }


    private void initSAPDestination() throws JCoException {

        destination = JCoDestinationManager.getDestination(DEST);

    }

}
