package ru.velkomfood.fin.cache.controller;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import ru.velkomfood.fin.cache.model.CashJournal;
import ru.velkomfood.fin.cache.model.RangeDates;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dpetrov on 28.12.2016.
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

    private SapSniffer() {
        materials = new ConcurrentHashMap<>();
        journalList = new ArrayList<>();
        connectSap = new Properties();
        connectSap.setProperty(DestinationDataProvider.JCO_ASHOST, "XXXXXXX");
        connectSap.setProperty(DestinationDataProvider.JCO_SYSNR, "XX");
        connectSap.setProperty(DestinationDataProvider.JCO_R3NAME, "XXX");
        connectSap.setProperty(DestinationDataProvider.JCO_SAPROUTER,
                ROUTER_STRING);
        connectSap.setProperty(DestinationDataProvider.JCO_CLIENT, "000");
        connectSap.setProperty(DestinationDataProvider.JCO_USER, "XXXXXX");
        connectSap.setProperty(DestinationDataProvider.JCO_PASSWD, "XXXXXXX");
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
        // Read a cash journal
        JCoTable tcj = rfcCashJournal.getTableParameterList().getTable("T_CJ_DOCS");
        if (tcj.getNumRows() > 0) {
           readCashJournalData(tcj);
        }

    }

    // setters and getters
    public Map<Integer, String> getMaterials() {
        return materials;
    }

    public List<CashJournal> getJournalList() {
        return journalList;
    }

    // PRIVATE SECTION FOR METHODS
    private void readCashJournalData(JCoTable itab) throws JCoException {

        if (!journalList.isEmpty()) {
            journalList.clear();
        }

        do {
            CashJournal cj = new CashJournal();
            journalList.add(cj);
        } while (itab.nextRow());

    }
}
