package ru.velkomfood.fin.cash.controller;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import ru.velkomfood.fin.cash.model.CashDoc;
import ru.velkomfood.fin.data.Receipt;
import ru.velkomfood.fin.data.ReceiptItem;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dpetrov on 10.03.2017.
 */
public class ErpRequestor {

    final static String DEST_NAME = "PRD500RUPS11";
    private static ErpRequestor instance;
    private Properties connectionProperties;
    private JCoDestination destination;
    // Materials
    private Map<Long, String> materials;
    // Customers
    // Outgoing delivery (head and items)
    private Map<Long, Receipt> receipts;
    // Cash Journal documents
    private List<CashDoc> heads;

    private ErpRequestor() {
        materials =  new ConcurrentHashMap<>();
        heads = new ArrayList<>();
        receipts = new ConcurrentHashMap<>();
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

        // Multiple remote function calls
        try {
            JCoContext.begin(destination);
            // Get materials and customers master records
            getMaterialNames();
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
                JCoTable lips = rfcGetReceipts.getTableParameterList().getTable("T_LIPS");
                getReceiptOrders(likp, lips);
            }
        } finally {
            JCoContext.end(destination);
        }

    } // get heads

    // Main getters
    public List<CashDoc> getHeads() {
        return heads;
    }

    public Map<Long, Receipt> getReceipts() {
        return receipts;
    }

// PRIVATE SECTION

    private void getMaterialNames() throws JCoException {

        JCoFunction bapiMatList = destination.getRepository().getFunction("BAPI_MATERIAL_GETLIST");
        if (bapiMatList == null) {
            throw new RuntimeException("Function BAPI_MATERIAL_GETLIST not found");
        }

        if (!materials.isEmpty()) {
            materials.clear();
        }

        JCoTable matSelect = bapiMatList.getTableParameterList().getTable("MATNRSELECTION");
        // Initialize selection parameters
        matSelect.appendRow();
        matSelect.setValue("SIGN", "I");
        matSelect.setValue("OPTION", "BT");
        matSelect.setValue("MATNR_LOW", "000000000000070002");
        matSelect.setValue("MATNR_HIGH", "000000000000079999");
        matSelect.appendRow();
        matSelect.setValue("SIGN", "I");
        matSelect.setValue("OPTION", "BT");
        matSelect.setValue("MATNR_LOW", "000000000010000000");
        matSelect.setValue("MATNR_HIGH", "000000000019999999");
        matSelect.appendRow();
        matSelect.setValue("SIGN", "I");
        matSelect.setValue("OPTION", "BT");
        matSelect.setValue("MATNR_LOW", "000000000020000000");
        matSelect.setValue("MATNR_HIGH", "000000000029999999");
        // Execute the function
        bapiMatList.execute(destination);
        JCoTable matList = bapiMatList.getTableParameterList().getTable("MATNRLIST");
        if (matList.getNumRows() > 0) {
            for (int i = 0; i < matList.getNumRows(); i++) {
                matList.setRow(i);
                materials.put(matList.getLong("MATERIAL"), matList.getString("MATL_DESC"));
            }
        }
    }

    // Data from outgoing deliveries
    private void getReceiptOrders(JCoTable tabLikp, JCoTable tabLips) throws JCoException {

        if (tabLikp.getNumRows() > 0 && tabLips.getNumRows() > 0) {
            for (int i = 0; i < tabLikp.getNumRows(); i++) {
                tabLikp.setRow(i);
                Receipt receipt = new Receipt();
                long deliveryId = tabLikp.getLong("VBELN");
                receipt.setId(deliveryId);
                for (int j = 0; j < tabLips.getNumRows(); j++) {
                    tabLips.setRow(j);
                    long key = tabLips.getLong("VBELN");
                    if (key != deliveryId) {
                        continue;
                    }
                    ReceiptItem ri = new ReceiptItem();
                    ri.setReceiptId(tabLips.getLong("VBELN"));
                    ri.setPosition(tabLips.getLong("POSNR"));
                    String txtMaterial = tabLips.getString("MATNR");
                    ri.setMaterial(txtMaterial);
                    ri.setMaterialName(materials.get(tabLips.getLong("MATNR")));
                    ri.setQuantity(tabLips.getBigDecimal("NTGEW"));
                    // Amount initialization
                    ri.setAmount(new BigDecimal(0.00));
                    receipt.addItem(ri);

                } // for
                receipts.put(receipt.getId(), receipt);
            } // for
            // After not need it
            materials.clear();
        }

    } // get the head of delivery

}
