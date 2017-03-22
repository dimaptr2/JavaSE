package ru.velkomfood.fin.cash.controller;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created by dpetrov on 10.03.2017.
 */
public class ErpRequestor {

    final static String DEST_NAME = "PRD500RUPS15";
    private static ErpRequestor instance;
    private Properties connectionProperties;
    private JCoDestination destination;

    private ErpRequestor() {
        connectionProperties = new Properties();
        connectionProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "rups15.eatmeat.ru");
        connectionProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "02");
        connectionProperties.setProperty(DestinationDataProvider.JCO_R3NAME, "PRD");
        connectionProperties.setProperty(DestinationDataProvider.JCO_USER, "BGD_ADMIN");
        connectionProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "123qweASD");
        connectionProperties.setProperty(DestinationDataProvider.JCO_LANG, "RU");
        createDestinationDataFile(DEST_NAME, connectionProperties);
    }

    // Create a file with the connection properties
    private void createDestinationDataFile(String destName, Properties props) {

        File destCfg = new File(destName + ".jcoDestination");

        try {
            FileOutputStream fos = new FileOutputStream(destCfg, false);
            props.store(fos, "Production server");
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create the destination file", e);
        }

    }

    public static ErpRequestor getInstance() {
        if(instance == null) {
            instance = new ErpRequestor();
        }
        return instance;
    }

    // Main logic

    public void initSAPconnection() throws JCoException {
        destination = JCoDestinationManager.getDestination(DEST_NAME);
    }

}
