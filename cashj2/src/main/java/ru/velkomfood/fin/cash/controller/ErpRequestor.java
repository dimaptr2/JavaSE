package ru.velkomfood.fin.cash.controller;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.ext.DestinationDataProvider;
import ru.velkomfood.fin.cash.model.HeadReceiptOrder;
import ru.velkomfood.fin.cash.model.ItemReceiptOrder;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by dpetrov on 10.03.2017.
 */
public class ErpRequestor {

    final static String DEST_NAME = "PRD500";
    private static ErpRequestor instance;
    private Properties connectionProperties;
    private JCoDestination destination;

    private List<HeadReceiptOrder> heads;
    private List<ItemReceiptOrder> items;

    private ErpRequestor() {
        heads = new ArrayList<>();
        items = new ArrayList<>();
        connectionProperties = new Properties();
        connectionProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "");
        connectionProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "");
        connectionProperties.setProperty(DestinationDataProvider.JCO_R3NAME, "");
        connectionProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "");
        connectionProperties.setProperty(DestinationDataProvider.JCO_USER, "");
        connectionProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "");
        connectionProperties.setProperty(DestinationDataProvider.JCO_LANG, "");
        createDestinationDataFile(DEST_NAME, connectionProperties);
    }

    // Create a file with the connection properties
    private void createDestinationDataFile(String destName, Properties props) {

        File destCfg = new File(destName + ".jcoDestination");
        if  (!destCfg.isDirectory() && !destCfg.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(destCfg, false);
                props.store(fos, "Production server");
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException("Unable to create the destination file", e);
            }
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

    public void getReceiptHeaders(String dt) throws JCoException {

        StringBuilder sb = new StringBuilder(0);

        // Build SAP date in the internal format (YYYYMMDD)
        if (dt == null) {
            Date now = new Date();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            String[] temp = fmt.format(now).split("-");
            sb.append(temp[0]).append(temp[1]).append(temp[2]);
        } else {
            String[] dateSplit = dt.split("-");
            sb.append(dateSplit[2]).append(dateSplit[1]).append(dateSplit[0]);
        }


        JCoFunction rfcGetReceipts = destination.getRepository().getFunction("Z_RFC_GET_CASHDOC");
        if (rfcGetReceipts == null) {
            throw new RuntimeException("Cannot found function Z_RFC_GET_CASHDOC");
        }
        

        if (!heads.isEmpty()) {
            heads.clear();
        }

        if (!heads.isEmpty()) {
            getReceiptItems();
        }
    }



    // Main getters
    public List<HeadReceiptOrder> getHeads() {
        return heads;
    }

    public List<ItemReceiptOrder> getItems() {
        return items;
    }

    // Additional methods
    private void getReceiptItems() throws JCoException {
        if (!items.isEmpty()) {
            items.clear();
        }
    }

}
