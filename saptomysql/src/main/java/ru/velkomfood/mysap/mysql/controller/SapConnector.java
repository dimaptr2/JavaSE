package ru.velkomfood.mysap.mysql.controller;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import ru.velkomfood.mysap.mysql.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.Inflater;

/**
 * Created by dpetrov on 12.07.16.
 */

public class SapConnector {

    final static String sapDestination = "PRD500";

    private Thread  t_sap;
    private String threadName;
    private JCoDestination jcoDest;
    private Queue<CustomerEntity> customers;
    private Queue<VendorEntity> vendors;
    private Queue<MaterialEntity> materials;
    private Queue<MaterialPriceEntity> materialPrices;
    private Queue<StocksEntity> stocks;
    private Queue<MrpItemsEntity> mrpitems;
    private Queue<LogisticInfoRecord> logisticInfoRecords;

    private static SapConnector instance;

    static {

        Properties connectProperties = new Properties();
        createDestinationFile(sapDestination, connectProperties);

    }

    static void createDestinationFile(String destName, Properties connPros) {

        File destCfg = new File(destName + ".jcoDestination");

        if (destCfg.exists() && !destCfg.isDirectory()) {
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(destCfg, false);
            connPros.store(fos, "SAP PRD info");
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException("Cannot open file", e);
        }

    }

    // Constructor
    private SapConnector(String threadName) {

        this.threadName = threadName;

        customers = new ConcurrentLinkedQueue<>();
        vendors = new ConcurrentLinkedQueue<>();
        materials = new ConcurrentLinkedQueue<>();
        materialPrices = new ConcurrentLinkedQueue<>();
        stocks = new ConcurrentLinkedQueue<>();
        mrpitems = new ConcurrentLinkedQueue<>();
        logisticInfoRecords = new ConcurrentLinkedQueue<>();

    }

    // Create singleton
    public static SapConnector getInstance(String threadName) {

        if (instance == null) instance = new SapConnector(threadName);

        return instance;
    }

    // SAP destionation
    public JCoDestination getSapDestionation() throws JCoException {
        return JCoDestinationManager.getDestination(sapDestination);
    }

    public Queue<CustomerEntity> getCustomers() {
        return customers;
    }

    public Queue<VendorEntity> getVendors() {
        return vendors;
    }

    public Queue<MaterialEntity> getMaterials() {
        return materials;
    }

    public Queue<MaterialPriceEntity> getMaterialPrices() {
        return materialPrices;
    }

    public Queue<StocksEntity> getStocks() {
        return stocks;
    }

    public Queue<MrpItemsEntity> getMrpitems() {
        return mrpitems;
    }

    public Queue<LogisticInfoRecord> getLogisticInfoRecords() {
        return logisticInfoRecords;
    }

    public Thread getT_sap() {
        return t_sap;
    }


    public void run() {

        try {

            jcoDest = getSapDestionation();

            JCoContext.begin(jcoDest);

            receiveCustomersAndVendors(jcoDest);
            receiveMaterials(jcoDest);
            receiveMaterialPricesAndMrpItems(jcoDest);
            receiveLogisticInfoRecords(jcoDest);

            JCoContext.end(jcoDest);

        } catch (JCoException | ParseException se) {

            se.printStackTrace();

        }

    }

    private void receiveCustomersAndVendors(JCoDestination destination) throws JCoException {

        JCoFunction bapi;
        JCoTable kna1, lfa1;

        if (destination.isValid()) {

            bapi = destination.getRepository().getFunction("Z_FM_DMS_DICT_READ");

            if (bapi == null) {
                throw new RuntimeException("Function Z_FM_DMS_DICT_READ not found");
            } else {

                bapi.execute(destination);

                if (!customers.isEmpty()) customers.clear();
                if (!vendors.isEmpty()) vendors.clear();

                lfa1 = bapi.getTableParameterList().getTable("T_LFA1");

                for (int i = 0; i < lfa1.getNumRows(); i++) {
                    lfa1.setRow(i);
                    VendorEntity ve = new VendorEntity();
                    ve.setLifnr(lfa1.getString("LIFNR"));
                    ve.setName1(lfa1.getString("NAME1"));
                    vendors.add(ve);
                }

                kna1 = bapi.getTableParameterList().getTable("T_KNA1");

                for (int j = 0; j < kna1.getNumRows(); j++) {
                    kna1.setRow(j);
                    CustomerEntity ce = new CustomerEntity();
                    ce.setKunnr(kna1.getString("KUNNR"));
                    ce.setName1(kna1.getString("NAME1"));
                    customers.add(ce);
                }

            }

        }

    } // receive customers and vendors

    private void receiveMaterials(JCoDestination destination) throws JCoException {

        JCoFunction bapiGetMatList;
        JCoTable matselection, mara;

        if (destination.isValid()) {

            if (!materials.isEmpty()) materials.clear();

            bapiGetMatList = destination.getRepository().getFunction("BAPI_MATERIAL_GETLIST");

            if (bapiGetMatList == null) {
                throw new RuntimeException("Function BAPI_MATERIAL_GETLIST not found");
            }
            // Set selection criteria
            matselection = bapiGetMatList.getTableParameterList().getTable("MATNRSELECTION");
            matselection.appendRow();
            matselection.setValue("SIGN", "I");
            matselection.setValue("OPTION", "BT");
            matselection.setValue("MATNR_LOW", "0");
            matselection.setValue("MATNR_HIGH", "999999999999999999");
            // Execute the Remote Function Call in the ERP.
            bapiGetMatList.execute(destination);
            mara = bapiGetMatList.getTableParameterList().getTable("MATNRLIST");

            for (int j = 0; j < mara.getNumRows(); j++) {
                mara.setRow(j);
                MaterialEntity me = new MaterialEntity();
                me.setMatnr(mara.getString("MATERIAL"));
                me.setDescription(mara.getString("MATL_DESC"));
                materials.add(me);
            }


        } // destination
    } // receive materials

    // Prices and items of planning

    private void receiveMaterialPricesAndMrpItems(JCoDestination destination) throws JCoException, ParseException {

        JCoFunction bapiGetDetails, bapiMatReq;
        JCoStructure matdata, matplantdata, matdatambew, mrpHeader;
        JCoTable items;

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        if (!materialPrices.isEmpty()) materialPrices.clear();
        if (!stocks.isEmpty()) stocks.clear();
        if (!mrpitems.isEmpty()) mrpitems.clear();

        bapiGetDetails = destination.getRepository().getFunction("BAPI_MATERIAL_GET_DETAIL");

        if (bapiGetDetails == null) {
            throw new RuntimeException("Function BAPI_MATERIAL_GET_DETAIL not found");
        }

        bapiMatReq = destination.getRepository().getFunction("BAPI_MATERIAL_STOCK_REQ_LIST");

        if (bapiMatReq == null) {
            throw new RuntimeException("Function BAPI_MATERIAL_STOCK_REQ_LIST not found");
        }

        for (MaterialEntity me: materials) {

            MaterialPriceEntity mp = new MaterialPriceEntity();
            // Set parameters of the searching
            bapiGetDetails.getImportParameterList().setValue("MATERIAL", me.getMatnr());
            bapiGetDetails.getImportParameterList().setValue("PLANT", 1000);
            bapiGetDetails.getImportParameterList().setValue("VALUATIONAREA", 1000);
            mp.setMatnr(me.getMatnr());
            mp.setWerks(1000);
            mp.setValuation_area(1000);

            // Execute BAPI
            bapiGetDetails.execute(destination);
            // Get results
            matdata = bapiGetDetails.getExportParameterList().getStructure("MATERIAL_GENERAL_DATA");
            matplantdata = bapiGetDetails.getExportParameterList().getStructure("MATERIALPLANTDATA");
            matdatambew = bapiGetDetails.getExportParameterList().getStructure("MATERIALVALUATIONDATA");
            // Read values from fields of structures
            mp.setPur_group(matplantdata.getString("PUR_GROUP"));
            mp.setBase_unit(matdata.getString("BASE_UOM"));
            mp.setPrice_control(matdatambew.getChar("PRICE_CTRL"));
            mp.setCurrency(matdatambew.getString("CURRENCY"));

            // Convert prices to single unit of measurements.
            // weighted price for base unit
            double price1 = 0.00;
            double price2 = 0.00;
            double value1 = matdatambew.getDouble("MOVING_PR");
            double value2 = matdatambew.getDouble("STD_PRICE");
            double price_unit = matdatambew.getDouble("PRICE_UNIT");

            if (price_unit != 0.00) {
                price1 = value1 / price_unit;
                price2 = value2 / price_unit;
            }

            BigDecimal weighted = BigDecimal.valueOf(price1).setScale(20, 2);
            BigDecimal fixed = BigDecimal.valueOf(price2).setScale(20, 2);

            mp.setMoving_price(weighted);
            mp.setStandard_price(fixed);

            materialPrices.add(mp);

            // Get MRP items from ERP
            bapiMatReq.getImportParameterList().setValue("MATERIAL", me.getMatnr());
            bapiMatReq.getImportParameterList().setValue("PLANT", "1000");
            bapiMatReq.getImportParameterList().setValue("PERIOD_INDICATOR", "M");
            bapiMatReq.getImportParameterList().setValue("GET_IND_LINES", " ");
            bapiMatReq.getImportParameterList().setValue("GET_TOTAL_LINES", "X");
            // Call BAPI
            bapiMatReq.execute(destination);

            mrpHeader = bapiMatReq.getExportParameterList().getStructure("MRP_LIST");
            items = bapiMatReq.getTableParameterList().getTable("MRP_TOTAL_LINES");

            // Get data about stocks
            StocksEntity se = new StocksEntity();

            for (JCoField field: mrpHeader) {
                switch (field.getName()) {
                    case "MATERIAL":
                        se.setMatnr(field.getString());
                        break;
                    case "PLANT":
                        se.setWerks(field.getInt());
                        break;
                    case "MRP_DATE":
                        se.setAvailDate(field.getString());
                        break;
                    case "PUR_GROUP":
                        se.setPurchaseGroup(field.getString());
                        break;
                    case "BASE_UOM":
                        se.setBaseUnit(field.getString());
                        break;
                    case "SAFETY_STK":
                        se.setSafetyStock(field.getBigDecimal());
                        break;
                    case "PLNT_STOCK":
                        se.setPlantStock(field.getBigDecimal());
                        break;
                }
            }

            stocks.add(se);

            // MRP Items
            for (int i = 0; i < items.getNumRows(); i++) {

                items.setRow(i);
                MrpItemsEntity mrpe = new MrpItemsEntity();

                for (JCoField field: mrpHeader) {
                    switch (field.getName()) {
                        case "MATERIAL":
                            mrpe.setMatnr(field.getString());
                            break;
                        case "PLANT":
                            mrpe.setPlant(field.getInt());
                            break;
                        case "BASE_UOM":
                            mrpe.setBaseUnit(field.getString());
                            break;
                        case "PUR_GROUP":
                            mrpe.setPurchaseGroup(field.getString());
                            break;
                    } // switch
                } // mrpHeader

                mrpe.setMrpDate(items.getString("AVAIL_DATE"));
                mrpe.setPer_segmt(items.getString("PER_SEGMT"));
                mrpe.setPquantity(items.getBigDecimal("PLD_IND_REQS"));
                mrpe.setSquantity(items.getBigDecimal("REQMTS"));
                mrpe.setAvailquantity(items.getBigDecimal("AVAIL_QTY"));
                mrpitems.add(mrpe);

            } // items

        } // materials

    } // receive material prices

    // Get info-records of LO
    private void receiveLogisticInfoRecords(JCoDestination destination) throws JCoException {

        JCoFunction bapiInfoGetList;
        JCoTable infoRecordHeaders;

        if (destination.isValid()) {

            if (!logisticInfoRecords.isEmpty()) logisticInfoRecords.clear();

            bapiInfoGetList = destination.getRepository().getFunction("BAPI_INFORECORD_GETLIST");

            if (bapiInfoGetList == null) {
                throw new RuntimeException("Function BAPI_INFORECORD_GETLIST not found");
            }

            bapiInfoGetList.getImportParameterList().setValue("PURCH_ORG", "1000");
            bapiInfoGetList.getImportParameterList().setValue("PLANT", "1000");

            bapiInfoGetList.getTableParameterList().setActive("INFORECORD_PURCHORG", false);
            bapiInfoGetList.getTableParameterList().setActive("RETURN", false);
            bapiInfoGetList.getTableParameterList().setActive("INFORECORD_GENERAL", true);

            bapiInfoGetList.execute(destination);

            infoRecordHeaders = bapiInfoGetList.getTableParameterList().getTable("INFORECORD_GENERAL");

            for (int l = 0; l < infoRecordHeaders.getNumRows(); l++) {

                infoRecordHeaders.setRow(l);

                LogisticInfoRecord infoRecord = new LogisticInfoRecord();
                infoRecord.setInfoRecordNumber(infoRecordHeaders.getString("INFO_REC"));
                infoRecord.setMaterial(infoRecordHeaders.getString("MATERIAL"));
                infoRecord.setVendor(infoRecordHeaders.getString("VENDOR"));
                infoRecord.setDateCreation(infoRecordHeaders.getString("CREATED_AT"));
                logisticInfoRecords.add(infoRecord);

            }

        } // The destination is valid

    }  // receive logistic info-records

}
