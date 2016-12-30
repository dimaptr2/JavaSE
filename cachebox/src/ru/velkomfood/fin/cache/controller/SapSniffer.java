package ru.velkomfood.fin.cache.controller;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import ru.velkomfood.fin.cache.model.CashJournal;
import ru.velkomfood.fin.cache.model.DeliveryHead;
import ru.velkomfood.fin.cache.model.DeliveryItem;
import ru.velkomfood.fin.cache.model.RangeDates;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

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
    private Map<Integer, String> materials;
    private List<CashJournal> journalList;
    private List<DeliveryHead> heads;
    private List<DeliveryItem> items;

    private SapSniffer() {
        materials = new ConcurrentHashMap<>();
        journalList = new ArrayList<>();
        heads = new ArrayList<>();
        items = new ArrayList<>();
        connectSap = new Properties();
        connectSap.setProperty(DestinationDataProvider.JCO_ASHOST, "XXXXXXX");
        connectSap.setProperty(DestinationDataProvider.JCO_SYSNR, "XX");
        connectSap.setProperty(DestinationDataProvider.JCO_R3NAME, "XXX");
        connectSap.setProperty(DestinationDataProvider.JCO_CLIENT, "XXX");
        connectSap.setProperty(DestinationDataProvider.JCO_USER, "XXXXXX");
        connectSap.setProperty(DestinationDataProvider.JCO_PASSWD, "XXXXXX");
        connectSap.setProperty(DestinationDataProvider.JCO_LANG, "XX");
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

    // read a dictionary of materials master data
    public void getAllMaterials() throws JCoException {

        if (!materials.isEmpty()) {
            materials.clear();
        }

        JCoFunction bapiMatList = jCoDestination.getRepository().
                getFunction("BAPI_MATERIAL_GETLIST");
        if (bapiMatList == null) {
            throw new RuntimeException("Function BAPI_MATERIAL_GETLIST not found");
        }

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
                materials.put(matList.getInt("MATERIAL"), matList.getString("MATL_DESC"));
            } while (matList.nextRow());
        }
    }

    // Read credit slips
    public void readCreditSlips(String fromDate, String toDate) throws JCoException {

        JCoFunction rfcCashJournal = jCoDestination.getRepository().getFunction("Z_RFC_GET_CASHDOC");
        // set a company code
        rfcCashJournal.getImportParameterList().setValue("I_COMP_CODE", "1000");
        // set a code of cash journal
        rfcCashJournal.getImportParameterList().setValue("I_CAJO_NUMBER", "1000");

        if (!fromDate.equals("") && !toDate.equals("")) {
            RangeDates rd = new RangeDates(fromDate, toDate);
            rfcCashJournal.getImportParameterList().setValue("FROM_DATE", rd.getFromDate());
            rfcCashJournal.getImportParameterList().setValue("TO_DATE", rd.getToDate());
        } else {
            Date now = new Date();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            String[] strDates = fmt.format(now).split("-");
            String s1 = strDates[0] + strDates[1] + strDates[2];
            rfcCashJournal.getImportParameterList().setValue("FROM_DATE", s1);
            rfcCashJournal.getImportParameterList().setValue("TO_DATE", s1);
        }
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

    // setters and getters
    public Map<Integer, String> getMaterials() {
        return materials;
    }

    public List<CashJournal> getJournalList() {
        return journalList;
    }

    public List<DeliveryHead> getHeads() {
        return heads;
    }

    public List<DeliveryItem> getItems() {
        return items;
    }

    // PRIVATE SECTION FOR METHODS
    private void readCashJournalData(JCoTable itab) throws JCoException {

        if (!journalList.isEmpty()) {
            journalList.clear();
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
                    cj.setPostingNumber(itab.getInt("POSTING_NUMBER"));
                    cj.setAmountReceipt(itab.getBigDecimal("H_RECEIPTS"));
                    cj.setAmountPayments(itab.getBigDecimal("H_PAYMENTS"));
                    cj.setNetAmount(itab.getBigDecimal("H_NET_AMOUNT"));
                    cj.setPartnerName(itab.getString("BP_NAME"));
                    cj.setDocumentDate(java.sql.Date.valueOf(itab.getString("DOCUMENT_DATE")));
                    cj.setPostingDate(java.sql.Date.valueOf(itab.getString("POSTING_DATE")));
                    cj.setDocumentNumber(itab.getString("DOCUMENT_NUMBER"));
                    journalList.add(cj);
                }
            }
        } while (itab.nextRow());
    }

    // heads of deliveries
    private void readDeliveryHeads(JCoTable itab) throws JCoException {

        if (!heads.isEmpty()) {
            heads.clear();
        }

        do {
            DeliveryHead head = new DeliveryHead();
            head.setId(itab.getInt("VBELN"));
            head.setUser(itab.getString("ERNAM"));
            head.setDeliveryDate(java.sql.Date.valueOf(itab.getString("WADAT")));
            head.setCustomer(itab.getString("KUNNR"));
            heads.add(head);
        } while (itab.nextRow());

    }

    // items of deliveries
    private void readDeliveryItems(JCoTable itab) throws JCoException {

        if (!items.isEmpty()) {
            items.clear();
        }

        do {
            DeliveryItem item = new DeliveryItem();
            item.setDeliveryId(itab.getInt("VBELN"));
            item.setPosId(itab.getInt("POSNR"));
            item.setMaterialId(itab.getInt("MATNR"));
            item.setQuantity(itab.getBigDecimal("LFIMG"));
            item.setUom(itab.getString("MEINS"));
            item.setMaterialName(itab.getString("ARKTX"));
            items.add(item);
        } while (itab.nextRow());

    }
}
