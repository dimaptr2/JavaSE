package ru.velkomfood.mysap.synchronizer.controller;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import ru.velkomfood.mysap.synchronizer.model.Material;
import ru.velkomfood.mysap.synchronizer.model.MaterialValuation;
import ru.velkomfood.mysap.synchronizer.model.Unit;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by dpetrov on 02.12.16.
 */
public class SapReader implements Runnable {

    private Thread myThread;
    private String threadName;
    private String flag;
    private Range range;
    private String message;

    private MySqlWriter sqlWriter;

    // SAP Connection
    static String DEST_NAME = "P500";
    JCoDestination destination;

    // Data containers
    private Queue<Unit> units;
    private Queue<Material> materials;
    private Queue<MaterialValuation> valuations;

    public SapReader(String threadName) {

        this.threadName = threadName;
        units = new ConcurrentLinkedQueue<>();
        materials = new ConcurrentLinkedQueue<>();
        valuations = new ConcurrentLinkedQueue<>();

    }

    static {
        Properties props = new Properties();
        createDestinationDataFile(DEST_NAME, props);
    }

    static void createDestinationDataFile(String destName, Properties connectProperties) {

        File destCfg = new File(destName + ".jcoDestination");

        if (!destCfg.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(destCfg, false);
                connectProperties.store(fos, "Poduction server");
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException("Unable to create the destionation file", e);
            }
        }

    }

    // Starting a thread ...
    public void start() {
        if (myThread == null) {
            myThread = new Thread(this, threadName);
//            myThread.start();
        }
    }

    // This is a main processing of this thread (master data)

    @Override
    public void run() {

        synchronized (sqlWriter) {

            try {
                createErpDestionation();
                try {

                    JCoContext.begin(destination);

                    readUoms();
                    readMaterial();

                    try {

                        long start = new Date().getTime();
                        sqlWriter.openDbConnection();
                        sqlWriter.refreshDatabase();
                        sqlWriter.setUnits(units);
                        System.out.printf("Number of UOMs is %d\r\n", units.size());
                        sqlWriter.createUoms();
                        sqlWriter.setMaterials(materials);
                        System.out.printf("Number of materials is %d\r\n", materials.size());
                        sqlWriter.createMaterials();
                        sqlWriter.setValuations(valuations);
                        System.out.printf("Number of material valuations is %d\r\n", valuations.size());
                        sqlWriter.createValuations();
                        sqlWriter.closeDbConnection();
                        Thread.sleep(10);
                        long finish = new Date().getTime();
                        System.out.printf("Additional thread: time of execution is %d minutes\r\n",
                                ((finish - start) / 60000));

                    } catch (InterruptedException | SQLException sqe) {
                        sqe.printStackTrace();
                    }

                } finally {
                    JCoContext.end(destination);
                }

            } catch (JCoException jcoe) {

                message = jcoe.getMessage();

            } // process tasks in the Java Connector Context

        }  // synchronization

    } // end of run-method

    // setters and getters
    public void setSqlWriter(MySqlWriter sqlWriter) {
        this.sqlWriter = sqlWriter;
    }

    public void setFlag(String flag) {

        this.flag = flag;

        range = new Range();

        range.setSign("I");  // include range
        range.setOption("BT");  // build a statement with BETWEEN clauses

        // FG = finished goods
        // AM = all materials
        switch (this.flag) {
            case "FG":
                range.setLow("000000000000070000");
                range.setHigh("000000000000079999");
                break;
            case "AM":
                range.setLow("0");
                range.setHigh("999999999999999999");
                break;
        }

    }

    // Addition public methods

    // BOM
    public void readBomByMaterial(int matnr) throws JCoException {

        // CSAP_MAT_BOM_READ SAP RFC module
        // CS_BOM_EXPL_MAT_V2_RFC BOM explotion

        String mat;

        if (matnr >= 70000 && matnr <= 79999) mat = "0000000000000";
        else mat = "0000000000";

        mat = mat + matnr;

    }

    // Parameters
    public String getMessage() {
        return message;
    }

    public Queue<Material> getMaterials() {
        return materials;
    }

    // Private methods
    // Create a destination (connection tom the SAP)
    private void createErpDestionation() throws JCoException {
        destination = JCoDestinationManager.getDestination(DEST_NAME);
    }

    // Units of measurements
    private void readUoms() throws JCoException {

        // Z_RFC_GET_UNITS_MEASURE RFC module in SAP

        if (!units.isEmpty()) units.clear();

        JCoFunction rfcUnits = destination.getRepository().getFunction("Z_RFC_GET_UNITS_MEASURE");
        if (rfcUnits == null)
            throw new RuntimeException("Cannot find function Z_RFC_GET_UNITS_MEASURE");

        rfcUnits.getImportParameterList().setValue("LANGUAGE", "RU");
        rfcUnits.execute(destination);

        JCoTable itab = rfcUnits.getTableParameterList().getTable("UNITS");
        if (itab.getNumRows() > 0) {
            itab.firstRow();
            do {
                Unit u = new Unit();
                u.setUomSAP(itab.getString("UOM_SAP"));
                u.setUomISO(itab.getString("UOM_ISO"));
                u.setDescription(itab.getString("UOM_DESCLONG"));
                units.add(u);
            } while (itab.nextRow());
        }

    }

    // Material master data
    private void readMaterial() throws JCoException {

        if (!materials.isEmpty()) materials.clear();
        if (!valuations.isEmpty()) valuations.clear();

        JCoFunction bapiMatList = destination.getRepository().getFunction("BAPI_MATERIAL_GETLIST");
        if (bapiMatList == null)
            throw new RuntimeException("Cannot find function BAPI_MATERIAL_GETLIST");

        JCoTable msels =bapiMatList.getTableParameterList().getTable("MATNRSELECTION");
        msels.appendRow();
        msels.setValue("SIGN", range.getSign());
        msels.setValue("OPTION", range.getOption());
        msels.setValue("MATNR_LOW", range.getLow());
        msels.setValue("MATNR_HIGH", range.getHigh());

        bapiMatList.execute(destination);
        // Read all material numbers and names
        JCoTable matList = bapiMatList.getTableParameterList().getTable("MATNRLIST");
        if (matList.getNumRows() > 0) {
            matList.firstRow();
            do {
                Material material = new Material(matList.getInt("MATERIAL"),
                        matList.getString("MATL_DESC"));
                materials.add(material);
                MaterialValuation mv = readMaterialValuation(material.getMatnr());
                valuations.add(mv);
            } while (matList.nextRow());
        }

    }

    // Read MBEW record
    public MaterialValuation readMaterialValuation(int i_matnr) throws JCoException {

        MaterialValuation mv = new MaterialValuation();

        JCoFunction bapiMatDetail = destination.getRepository().getFunction("BAPI_MATERIAL_GET_DETAIL");
        if (bapiMatDetail == null)
            throw new RuntimeException("Cannot find function BAPI_MATERIAL_GET_DETAIL");

        mv.setMatnr(i_matnr);
        mv.setPlant(1000);

        String strMaterial;

        if (i_matnr >= 70000 && i_matnr <= 79999) strMaterial = "0000000000000";
        else strMaterial = "0000000000";

        strMaterial = strMaterial + i_matnr;

        bapiMatDetail.getImportParameterList().setValue("MATERIAL", strMaterial);
        bapiMatDetail.getImportParameterList().setValue("PLANT", "1000");
        bapiMatDetail.getImportParameterList().setValue("VALUATIONAREA", "1000");

        bapiMatDetail.execute(destination);
        JCoStructure row = bapiMatDetail.getExportParameterList().getStructure("MATERIAL_GENERAL_DATA");

        for (JCoField f: row) {
            if (f.getName().equals("BASE_UOM")) mv.setBaseUom(f.getString());
        }

        row = bapiMatDetail.getExportParameterList().getStructure("MATERIALVALUATIONDATA");

        for (JCoField f: row) {
            switch (f.getName()) {
                case "PRICE_CTRL":
                    mv.setPriceControl(f.getString());
                    break;
                case "MOVING_PR":
                    mv.setWeightedPrice(f.getBigDecimal());
                    break;
                case "STD_PRICE":
                    mv.setFixedPrice(f.getBigDecimal());
                    break;
                case "PRICE_UNIT":
                    mv.setPriceUnit(f.getBigDecimal());
                    break;
            }
        }

        return mv;

    }

    // Internal class for ranges building
    private class Range {

        private String sign;
        private String option;
        private String low;
        private String high;

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public String getLow() {
            return low;
        }

        public void setLow(String low) {
            this.low = low;
        }

        public String getHigh() {
            return high;
        }

        public void setHigh(String high) {
            this.high = high;
        }

    } // end of class Range

}
