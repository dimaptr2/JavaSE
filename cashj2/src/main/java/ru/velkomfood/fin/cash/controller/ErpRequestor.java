package ru.velkomfood.fin.cash.controller;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import ru.velkomfood.fin.cash.model.CashDoc;
import ru.velkomfood.fin.cash.model.ItemReceiptOrder;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by dpetrov on 10.03.2017.
 */
public class ErpRequestor {

    final static String DEST_NAME = "PRD500RUPS11";
    private static ErpRequestor instance;
    private Properties connectionProperties;
    private JCoDestination destination;

    private List<CashDoc> heads;
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

    public void getCashDocs(String dt) throws JCoException {

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

        if (!heads.isEmpty()) {
            heads.clear();
        }

        // Define RFC function in the SAP Repository, if it exists, then we can invoke it
        JCoFunction rfcGetReceipts = destination.getRepository().getFunction("Z_RFC_GET_CASHDOC");
        if (rfcGetReceipts == null) {
            throw new RuntimeException("Cannot found function Z_RFC_GET_CASHDOC");
        }
        rfcGetReceipts.getImportParameterList().setValue("I_COMP_CODE", "1000");
        rfcGetReceipts.getImportParameterList().setValue("I_CAJO_NUMBER", "1000");
        rfcGetReceipts.getImportParameterList().setValue("FROM_DATE", sb.toString());
        rfcGetReceipts.getImportParameterList().setValue("TO_DATE", sb.toString());
        rfcGetReceipts.execute(destination);
        JCoTable docs = rfcGetReceipts.getTableParameterList().getTable("T_CJ_DOCS");
        if (docs.getNumRows() > 0) {
            for (int i = 0; i < docs.getNumRows(); i++) {
                docs.setRow(i);
                String posText = docs.getString("POSITION_TEXT");
                if (posText == null) {
                    continue;
                }
                if (!posText.equals("") && posText.charAt(0) == '8') {
                    CashDoc cashDoc = new CashDoc();
                    cashDoc.setCajoNumber(docs.getString("CAJO_NUMBER"));
                    cashDoc.setCompanyCode(docs.getInt("COMP_CODE"));
                    cashDoc.setFiscalYear(docs.getInt("FISC_YEAR"));
                    cashDoc.setPostingNumber(docs.getLong("POSTING_NUMBER"));
                    cashDoc.setPostingDate(docs.getString("POSTING_DATE"));
                    cashDoc.setPositionText(posText);
                    String[] textElems = posText.split(" ");
                    long deliveryNumber = Long.parseLong(textElems[0]);
                    cashDoc.setDeliveryId(deliveryNumber);
                    cashDoc.setAmount(docs.getBigDecimal("H_NET_AMOUNT"));
                    heads.add(cashDoc);
                }
            }
            JCoTable likp = rfcGetReceipts.getTableParameterList().getTable("T_LIKP");
            getReceiptOrders(likp);
            JCoTable lips = rfcGetReceipts.getTableParameterList().getTable("T_LIPS");
            getReceiptItems(lips);
        }

    } // get heads



    // Main getters
    public List<CashDoc> getHeads() {
        return heads;
    }

    public List<ItemReceiptOrder> getItems() {
        return items;
    }

    // Additional methods
    private void getReceiptOrders(JCoTable rItem) throws JCoException {

    }

    private void getReceiptItems(JCoTable rItem) throws JCoException {

        if (!items.isEmpty()) {
            items.clear();
        }

        if (rItem.getNumRows() > 0) {
            for (int q = 0; q < rItem.getNumRows(); q++) {
                rItem.setRow(q);
            }
        }
    } // get items

}
