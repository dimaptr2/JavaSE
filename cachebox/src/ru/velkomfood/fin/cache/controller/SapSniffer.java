package ru.velkomfood.fin.cache.controller;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import ru.velkomfood.fin.cache.model.SAP.CashJournal;
import ru.velkomfood.fin.cache.model.SAP.DeliveryHead;
import ru.velkomfood.fin.cache.model.SAP.DeliveryItem;
import ru.velkomfood.fin.cache.model.SAP.Material;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by dpetrov on 28.12.2016.
 *
 * SAP "Sniffer" - he makes singleton of utility,
 * that can get an information about outgoing deliveries from SAP.
 *
 *  <table>
 *      <tr>
 *         <th>Name</th><th>Description</th>
 *      </tr>
 *      <tr>
 *          <td></td>
 *      </tr>
 *  </table>
 */
public class SapSniffer {

    final String DEST_NAME = "PRD500";
    final String SUFFIX = "jcoDestination";

    private static SapSniffer instance;
    private Properties connectSap;
    private JCoDestination jCoDestination;

    private final String MATNR_LOW = "000000000000070000";
    private final String MATNR_HIGH = "000000000000079999";

    // Data collections
    CacheEngine cache = CacheEngine.getInstance();

    private SapSniffer() {
        connectSap = new Properties();
        connectSap.setProperty(DestinationDataProvider.JCO_ASHOST, "rups15.eatmeat.ru");
        connectSap.setProperty(DestinationDataProvider.JCO_SYSNR, "02");
        connectSap.setProperty(DestinationDataProvider.JCO_R3NAME, "PRD");
        connectSap.setProperty(DestinationDataProvider.JCO_CLIENT, "500");
        connectSap.setProperty(DestinationDataProvider.JCO_USER, "BGD_ADMIN");
        connectSap.setProperty(DestinationDataProvider.JCO_PASSWD, "123qweASD");
        connectSap.setProperty(DestinationDataProvider.JCO_LANG, "RU");
        createDestinationDataFile(DEST_NAME, SUFFIX, connectSap);
    }

    public static SapSniffer getInstance() {
        if (instance == null) {
            instance = new SapSniffer();
        }
        return instance;
    }
    // create destination file
    private void createDestinationDataFile(String destName, String suffix, Properties props) {
        File cfgDest = new File(destName + "." + suffix);
        if (!cfgDest.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(cfgDest, false);
                props.store(fos, "productive connection");
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException("Unable to create the file", e);
            }
        }
    }

    // SAP connection initialization
    public void initSAPConnection() throws JCoException {
        jCoDestination = JCoDestinationManager.getDestination(DEST_NAME);
    }

    // Setters and getters

    public JCoDestination getjCoDestination() {
        return jCoDestination;
    }

    // read a dictionary of materials master data
    public void getMaterialInfo() throws JCoException {

        // BAPI_MATERIAL_GETLIST for retrieving list of materials
        // BAPI_MATERIAL_GET_DETAIL for retrieving of details

        if (!cache.getMaterials().isEmpty()) {
            cache.getMaterials().clear();
        }

        JCoFunction bapiMatList = jCoDestination.getRepository().getFunction("BAPI_MATERIAL_GETLIST");
        if (bapiMatList == null) {
            throw new RuntimeException("Function BAPI_MATERIAL_GETLIST not found");
        }

        JCoFunction bapiMatDetails = jCoDestination.getRepository().getFunction("BAPI_MATERIAL_GET_DETAIL");
        if (bapiMatDetails == null) {
            throw new RuntimeException("Function BAPI_MATERIAL_GET_DETAIL not found");
        }

        // Execute RFC functions in the multiple context
        try {

            JCoContext.begin(jCoDestination);

            JCoTable matSelection = bapiMatList.getTableParameterList().getTable("MATNRSELECTION");
            // Set table parameters
            matSelection.appendRow();
            matSelection.setValue("SIGN", "I"); // include
            matSelection.setValue("OPTION", "BT"); // between
            matSelection.setValue("MATNR_LOW", MATNR_LOW); // from number
            matSelection.setValue("MATNR_HIGH", MATNR_HIGH); // to number
            // execute BAPI function in SAP
            bapiMatList.execute(jCoDestination);

            JCoTable matList = bapiMatList.getTableParameterList().getTable("MATNRLIST");
            if (matList.getNumRows() > 0) {

                do {
                    Material mat = new Material();
                    mat.setId(matList.getLong("MATERIAL"));
                    mat.setDescription(matList.getString("MATL_DESC"));
                    bapiMatDetails.getImportParameterList()
                            .setValue("MATERIAL", matList.getString("MATERIAL"));
                    bapiMatDetails.execute(jCoDestination);
                    JCoStructure matGeneralData = bapiMatDetails.getExportParameterList()
                            .getStructure("MATERIAL_GENERAL_DATA");
                    for (JCoField f: matGeneralData) {
                        switch (f.getName()) {
                            case "BASE_UOM":
                                mat.setBaseUom(f.getString());
                                break;
                            case "NET_WEIGHT":
                                mat.setNetBaseWeight(f.getBigDecimal());
                                break;
                        }
                    }
                    cache.getMaterials().put(mat.getId(), mat);
                } while (matList.nextRow());
            }

        } finally {

            JCoContext.end(jCoDestination);

        }

    } // get data about materials

    // Read credit slips
    public void readCreditSlips(String fromDate, String toDate) throws JCoException {

        JCoFunction rfcCashJournal = jCoDestination.getRepository().getFunction("Z_RFC_GET_CASHDOC");
        // set a company code
        rfcCashJournal.getImportParameterList().setValue("I_COMP_CODE", "1000");
        // set a code of cash journal
        rfcCashJournal.getImportParameterList().setValue("I_CAJO_NUMBER", "1000");

        if (!fromDate.equals("") && !toDate.equals("")) {
            cache.getDates().setFromDate(fromDate);
            cache.getDates().setToDate(toDate);
        } else {
            Date now = new Date();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            String[] strDates = fmt.format(now).split("-");
            String s1 = strDates[0] + strDates[1] + strDates[2];
            cache.getDates().setFromDate(s1);
            cache.getDates().setToDate(s1);
        }

        rfcCashJournal.getImportParameterList()
                .setValue("FROM_DATE", cache.getDates().getFromDate());
        rfcCashJournal.getImportParameterList()
                .setValue("TO_DATE", cache.getDates().getToDate());

        // execute the RFC function
        rfcCashJournal.execute(jCoDestination);
        // Read a cash journal (We are reading only credit slips with outgoing deliveries)
        JCoTable tcj = rfcCashJournal.getTableParameterList().getTable("T_CJ_DOCS");
        if (tcj.getNumRows() > 0) {
           readCashJournalData(tcj);
            // Read heads of outgoing deliveries
            JCoTable likp = rfcCashJournal.getTableParameterList().getTable("T_LIKP");
            if (likp.getNumRows() > 0) {
                readDeliveryHeads(likp);
            }
            // Read items of outgoing deliveries
            JCoTable lips = rfcCashJournal.getTableParameterList().getTable("T_LIPS");
            if (lips.getNumRows() > 0) {
                readDeliveryItems(lips);
            }
        }
    }

    // Return the receipt for the printing
    // Before the calling this method we must invoke next methods:
    // readCreditSlips, buildReceipt and calculateReceiptSums.

    private void readCashJournalData(JCoTable itab) throws JCoException {

        if (!cache.getJournalList().isEmpty()) {
            cache.getJournalList().clear();
        }
        // we will get only outgoing deliveries
        do {
            CashJournal cj = new CashJournal();
            String posText = itab.getString("POSITION_TEXT");
            cj.setPositionText(posText);
            // splitting the string by spaces
            if (posText.equals("")) {
                continue;
            } else {
                String[] elems = posText.split(" ");
                // Any delivery has a number starting at 8
                if (elems[0].charAt(0) == '8') {
                    cj.setDeliveryId(Long.parseLong(elems[0]));
                    cj.setCajoNumber(itab.getString("CAJO_NUMBER"));
                    cj.setCompanyCode(itab.getString("COMP_CODE"));
                    cj.setYear(itab.getInt("FISC_YEAR"));
                    cj.setPostingNumber(itab.getLong("POSTING_NUMBER"));
                    cj.setAmountReceipt(itab.getBigDecimal("H_RECEIPTS"));
                    cj.setAmountPayments(itab.getBigDecimal("H_PAYMENTS"));
                    cj.setNetAmount(itab.getBigDecimal("H_NET_AMOUNT"));
                    cj.setPartnerName(itab.getString("BP_NAME"));
                    cj.setDocumentDate(java.sql.Date.valueOf(itab.getString("DOCUMENT_DATE")));
                    cj.setPostingDate(java.sql.Date.valueOf(itab.getString("POSTING_DATE")));
                    cj.setDocumentNumber(itab.getString("DOCUMENT_NUMBER"));
                    cache.getJournalList().add(cj);
                }
            }
        } while (itab.nextRow());
    }

    // heads of deliveries
    private void readDeliveryHeads(JCoTable itab) throws JCoException {

        if (!cache.getHeads().isEmpty()) {
            cache.getHeads().clear();
        }

        do {
            DeliveryHead head = new DeliveryHead();
            head.setId(itab.getLong("VBELN"));
            head.setUser(itab.getString("ERNAM"));
            head.setDeliveryDate(java.sql.Date.valueOf(itab.getString("WADAT")));
            head.setCustomer(itab.getString("KUNNR"));
            cache.getHeads().add(head);
        } while (itab.nextRow());

    }

    // items of deliveries
    private void readDeliveryItems(JCoTable itab) throws JCoException {

        JCoFunction rfcUnitConversion = jCoDestination.getRepository()
                .getFunction("Z_RFC_MATERIAL_UNIT_CONV");
        if (rfcUnitConversion == null) {
            throw new RuntimeException("Cannot found function Z_RFC_MATERIAL_UNIT_CONV");
        }

        if (!cache.getItems().isEmpty()) {
            cache.getItems().clear();
        }

        do {
            DeliveryItem item = new DeliveryItem();
            item.setDeliveryId(itab.getLong("VBELN"));
            item.setPosId(itab.getLong("POSNR"));
            item.setMaterialId(itab.getLong("MATNR"));
            item.setMaterialName(itab.getString("ARKTX"));
            item.setUom(itab.getString("MEINS"));
            item.setQuantity(itab.getBigDecimal("LFIMG"));
            // Invoke RFC function Z_RFC_MATERIAL_UNIT_CONV
            rfcUnitConversion.getImportParameterList().setValue("I_MATNR", item.getMaterialId());
            rfcUnitConversion.getImportParameterList().setValue("I_MEINH", item.getUom());
            rfcUnitConversion.getImportParameterList().setValue("I_MENGE", item.getQuantity());
            rfcUnitConversion.execute(jCoDestination);
            // Quantity UOM into KG
            BigDecimal quantity = rfcUnitConversion.getExportParameterList().getBigDecimal("E_MENGE");
            item.setQuantityKG(quantity);
            cache.getItems().add(item);
        } while (itab.nextRow());

    }

}
