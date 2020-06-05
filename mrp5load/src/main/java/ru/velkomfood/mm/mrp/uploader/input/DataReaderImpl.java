package ru.velkomfood.mm.mrp.uploader.input;

import com.sap.conn.jco.*;
import ru.velkomfood.mm.mrp.uploader.data.EventBus;
import ru.velkomfood.mm.mrp.uploader.data.model.md.*;
import ru.velkomfood.mm.mrp.uploader.data.model.td.Requirement;
import ru.velkomfood.mm.mrp.uploader.data.model.td.Stock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class DataReaderImpl implements DataReader {

    private final EventBus eventBus;
    private final Properties parameters;
    private final ExecutorService executor;

    public DataReaderImpl(EventBus eventBus, Properties parameters, ExecutorService executor) {
        this.eventBus = eventBus;
        this.parameters = parameters;
        this.executor = executor;
    }

    @Override
    public JCoDestination createDestination() {
        try {
            return JCoDestinationManager.getDestination(parameters.getProperty("sap.destination"));
        } catch (JCoException ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public CompletableFuture<Void> readUnitsOfMeasure() {
        return CompletableFuture.runAsync(() -> {
            var destination = this.createDestination();
            if (destination != null && destination.isValid()) {
                try {
                    // Z_RFC_GET_UNITS_MEASURE this is the old RFM;
                    // Z_RFC_GET_UOMS this is the new RFM.
                    var rfcUom = destination.getRepository().getFunction("Z_RFC_GET_UOMS");
                    rfcUom.getImportParameterList().setValue("LANGUAGE", "RU");
                    rfcUom.execute(destination);
                    var uomTable = rfcUom.getTableParameterList().getTable("UNITS");
                    if (uomTable.getNumRows() > 0) {
                        do {
                            var id = uomTable.getString("MSEHI");
                            var description = uomTable.getString("MSEHL");
                            var uom = new Measure(id, description);
                            eventBus.push("uom.queue", uom);
                        } while (uomTable.nextRow());
                    }
                } catch (JCoException ex) {
                    log.error(ex.getMessage());
                }
            }
        }, this.executor);
    }

    @Override
    public CompletableFuture<Void> readPurchaseGroups() {
        return CompletableFuture.runAsync(() -> {
            var destination = this.createDestination();
            if (destination != null && destination.isValid()) {
                try {
                    var rfcReader = destination.getRepository().getFunction("RFC_READ_TABLE");
                    rfcReader.getImportParameterList().setValue("QUERY_TABLE", "T024");
                    rfcReader.getImportParameterList().setValue("DELIMITER", ";");
                    var fieldsTable = rfcReader.getTableParameterList().getTable("FIELDS");
                    fieldsTable.appendRow();
                    this.addRfcTableField(fieldsTable, "FIELDNAME", "EKGRP");
                    fieldsTable.appendRow();
                    this.addRfcTableField(fieldsTable, "FIELDNAME", "EKNAM");
                    rfcReader.execute(destination);
                    var results = rfcReader.getTableParameterList().getTable("DATA");
                    if (results.getNumRows() > 0) {
                        do {
                            var workArea = results.getString("WA");
                            var dataRow = workArea.split(";");
                            var purGroup = new PurchaseGroup(dataRow[0].trim(), dataRow[1].trim());
                            eventBus.push("purchase.group.queue", purGroup);
                        } while (results.nextRow());
                    }
                } catch (JCoException ex) {
                    log.error(ex.getMessage());
                }
            }
        }, this.executor);
    }

    @Override
    public CompletableFuture<Void> readStorePlaces() {
        return CompletableFuture.runAsync(() -> {
            var destination = this.createDestination();
            if (destination != null && destination.isValid()) {
                try {
                    var rfcReader = destination.getRepository().getFunction("RFC_READ_TABLE");
                    rfcReader.getImportParameterList().setValue("QUERY_TABLE", "T001L");
                    rfcReader.getImportParameterList().setValue("DELIMITER", ";");
                    var queryTable = rfcReader.getTableParameterList().getTable("OPTIONS");
                    queryTable.appendRow();
                    var query = String.format("WERKS = '%s'", parameters.getProperty("sap.plant"));
                    this.addRfcTableField(queryTable, "TEXT", query);
                    var fieldsTable = rfcReader.getTableParameterList().getTable("FIELDS");
                    fieldsTable.appendRow();
                    this.addRfcTableField(fieldsTable, "FIELDNAME", "LGORT");
                    fieldsTable.appendRow();
                    this.addRfcTableField(fieldsTable, "FIELDNAME", "LGOBE");
                    rfcReader.execute(destination);
                    var results = rfcReader.getTableParameterList().getTable("DATA");
                    if (results.getNumRows() > 0) {
                        do {
                            var wa = results.getString("WA");
                            var entity = wa.split(";");
                            var storePlace = new StorePlace(entity[0].trim(), entity[1].trim());
                            eventBus.push("store.place.queue", storePlace);
                        } while (results.nextRow());
                    }
                } catch (JCoException ex) {
                    log.error(ex.getMessage());
                }
            }
        }, this.executor);
    }

    @Override
    public CompletableFuture<Void> readMaterials(int rangeNumber) {
        return CompletableFuture.runAsync(() -> {
            var destination = this.createDestination();
            if (destination != null && destination.isValid()) {
                try {
                    JCoContext.begin(destination);
                    var bapiMatList = destination.getRepository().getFunction("BAPI_MATERIAL_GETLIST");
                    var selection = bapiMatList.getTableParameterList().getTable("MATNRSELECTION");
                    this.appendMaterialBapiSelectionRow(selection, rangeNumber);
                    bapiMatList.execute(destination);
                    var matListTable = bapiMatList.getTableParameterList().getTable("MATNRLIST");
                    if (matListTable.getNumRows() > 0) {
                        var rfcMatInfo = destination.getRepository().getFunction("Z_RFC_GET_MATERIAL_INFO");
                        do {
                            var idTxt = matListTable.getString("MATERIAL");
                            var id = Long.parseLong(idTxt);
                            var description = matListTable.getString("MATL_DESC").trim();
                            eventBus.push("material.queue", new Material(id, description));
                            var plant = parameters.getProperty("sap.plant");
                            rfcMatInfo
                                    .getImportParameterList()
                                    .setValue("I_PLANT", plant);
                            rfcMatInfo
                                    .getImportParameterList()
                                    .setValue("I_MATERIAL", idTxt);
                            rfcMatInfo.execute(destination);
                            var uom = ""; var unit = BigDecimal.valueOf(1.000);
                            var mara = rfcMatInfo.getExportParameterList().getStructure("E_MARA");
                            for (JCoField f : mara) {
                                if ("MEINS".equals(f.getName())) {
                                    uom = f.getString();
                                    break;
                                }
                            }
                            eventBus.addMaterial(idTxt, uom);
                            var mbew = rfcMatInfo.getExportParameterList().getStructure("E_MBEW");
                            for (JCoField f : mbew) {
                                if ("PEINH".equals(f.getName())) {
                                    unit = f.getBigDecimal();
                                    break;
                                }
                            }
                            eventBus.push("material.unit.queue", new MaterialUnit(id, plant, uom, unit));
                        } while (matListTable.nextRow());
                    }
                    JCoContext.end(destination);
                } catch (JCoException ex) {
                    log.error(ex.getMessage());
                }
            }
        }, this.executor);
    }

    // Z_RFC_GET_MARD2 an old RFM
    // Z_RFC_GET_MARD3 a new RFM
    @Override
    public CompletableFuture<Void> readStocks() {
        return CompletableFuture.runAsync(() -> {
            final JCoDestination destination = this.createDestination();
            if (destination != null && destination.isValid()) {
                try {
                    var rfcMard3 = destination.getRepository().getFunction("Z_RFC_GET_MARD3");
                    final String plant = this.parameters.getProperty("sap.plant");
                    final String[] warehouses = this.parameters
                            .getProperty("sap.store.place.range1")
                            .split(";");
                    final List<String> ids = this.buildIdSequence(this.eventBus.getMaterialCache());
                    final String lowMaterial = this.alphaTransformation(18, ids.get(0));
                    final String highMaterial = this.alphaTransformation(18, ids.get(ids.size() - 1));
                    ids.clear();
                    LocalDate currentDate = LocalDate.now();
                    int period = currentDate.getMonthValue();
                    int currentYear = currentDate.getYear();
                    for (int idx = 1; idx <= period; idx++) {
                        rfcMard3.getImportParameterList().setValue("I_WERKS", plant);
                        rfcMard3.getImportParameterList().setValue("I_YEAR", currentYear);
                        String monthTextValue = this.alphaTransformation(2, String.valueOf(idx));
                        rfcMard3.getImportParameterList().setValue("I_MONTH", monthTextValue);
                        rfcMard3.getImportParameterList().setValue("FROM_MATNR", lowMaterial);
                        rfcMard3.getImportParameterList().setValue("TO_MATNR", highMaterial);
                        var tabStorePlaces = rfcMard3.getTableParameterList().getTable("T_STORE_PLACE");
                        for (String whs : warehouses) {
                            tabStorePlaces.appendRow();
                            tabStorePlaces.setValue("SIGN", "I");
                            tabStorePlaces.setValue("OPTION", "EQ");
                            tabStorePlaces.setValue("LOW", whs);
                        }
                        rfcMard3.execute(destination);
                        var tabMard = rfcMard3.getTableParameterList().getTable("T_MARD");
                        if (tabMard.getNumRows() > 0) {
                            do {
                                var id = tabMard.getString("MATNR");
                                var storeId = tabMard.getString("LGORT");
                                var fiscalYear = tabMard.getInt("LFGJA");
                                var periodId = tabMard.getInt("LFMON");
                                var uomId = this.eventBus.getMaterialCache().get(id).getUom();
                                var free = tabMard.getBigDecimal("LABST");
                                var block = tabMard.getBigDecimal("SPEME");
                                var quality = tabMard.getBigDecimal("INSME");
                                var stock = new Stock(
                                        Long.parseLong(id),
                                        plant,
                                        storeId,
                                        fiscalYear,
                                        periodId,
                                        uomId,
                                        free,
                                        block,
                                        quality
                                );
                                this.eventBus.push("stock.queue", stock);
                            } while (tabMard.nextRow());
                            tabMard.clear();
                        } // if the number rows > 0
                        tabStorePlaces.clear();
                    } // cycle by periods
                } catch (JCoException ex) {
                    log.error(ex.getMessage());
                }
            }
        }, this.executor);
    }

    @Override
    public CompletableFuture<Void> readRequirements() {
        return CompletableFuture.runAsync(() -> {
            final JCoDestination destination = this.createDestination();
            if (destination != null && destination.isValid()) {
                try {
                    final JCoFunction bapiMrp = destination
                            .getRepository()
                            .getFunction("BAPI_MATERIAL_MRP_LIST");
                    final String plant = this.parameters.getProperty("sap.plant");
                    final String periodIndicator = this.parameters.getProperty("sap.bapi.mrp.period.indicator");
                    final List<String> materials = this.buildIdSequence(this.eventBus.getMaterialCache());
                    // for the sign inversion
                    final BigDecimal minusOne = BigDecimal.valueOf(-1.000);
                    // Read all Id of material and get the MRP list
                    materials.forEach(id -> {
                        try {
                            bapiMrp.getImportParameterList().setValue("MATERIAL", id);
                            bapiMrp.getImportParameterList().setValue("PLANT", plant);
                            bapiMrp.getImportParameterList().setValue("PERIOD_INDICATOR", periodIndicator);
                            // Without individual lines
                            bapiMrp.getImportParameterList().setValue("GET_IND_LINES", " ");
                            // With total line items
                            bapiMrp.getImportParameterList().setValue("GET_TOTAL_LINES", "X");
                            bapiMrp.execute(destination);
                            JCoTable totalLines = bapiMrp.getTableParameterList().getTable("MRP_TOTAL_LINES");
                            if (totalLines.getNumRows() > 0) {
                                var mrpHeader = bapiMrp.getExportParameterList().getStructure("MRP_LIST");
                                var matId = 0L; var uom = ""; var purGroup = "";
                                for (JCoField hdField : mrpHeader) {
                                    if ("MATERIAL".equals(hdField.getName())) {
                                        matId = hdField.getLong();
                                    } else if ("BASE_UOM".equals(hdField.getName())) {
                                        uom = hdField.getString();
                                    } else if ("PUR_GROUP".equals(hdField.getName())) {
                                        purGroup = hdField.getString();
                                    }
                                }
                                do {
                                    if (totalLines.getString("PER_SEGMT").trim().startsWith(periodIndicator)) {
                                        var availableDate = LocalDate.parse(totalLines.getString("AVAIL_DATE"));
                                        var fiscalYear = availableDate.getYear();
                                        var period = availableDate.getMonthValue();
                                        var quantity = totalLines.getBigDecimal("REQMTS").multiply(minusOne);
                                        var requirement = new Requirement(
                                                matId, plant, purGroup, fiscalYear, period, uom, quantity
                                        );
                                        this.eventBus.push("requirement.queue", requirement);
                                    }
                                } while (totalLines.nextRow());
                                totalLines.clear();
                            }
                        } catch (JCoException jcoEx) {
                            log.error(jcoEx.getMessage());
                        }
                    });
                } catch (JCoException ex) {
                    log.error(ex.getMessage());
                }
            }
        }, this.executor);
    }

    // private section

    private void appendMaterialBapiSelectionRow(JCoTable table, int rangeNumber) {

        table.appendRow();
        this.addRfcTableField(table, "SIGN", "I");
        this.addRfcTableField(table, "OPTION", "BT");
        String lowBound = "";
        String highBound = "";
        switch (rangeNumber) {
            case 1:
                lowBound = this.alphaTransformation(18, parameters.getProperty("sap.material.low1"));
                highBound = this.alphaTransformation(18, parameters.getProperty("sap.material.high1"));
                break;
            case 2:
                lowBound = this.alphaTransformation(18, parameters.getProperty("sap.material.low2"));
                highBound = this.alphaTransformation(18, parameters.getProperty("sap.material.high2"));
                break;
            case 3:
                lowBound = this.alphaTransformation(18, parameters.getProperty("sap.material.low3"));
                highBound = this.alphaTransformation(18, parameters.getProperty("sap.material.high3"));
                break;
            case 4:
                lowBound = this.alphaTransformation(18, parameters.getProperty("sap.material.low4"));
                highBound = this.alphaTransformation(18, parameters.getProperty("sap.material.high4"));
                break;
            case 5:
                lowBound = this.alphaTransformation(18, parameters.getProperty("sap.material.low5"));
                highBound = this.alphaTransformation(18, parameters.getProperty("sap.material.high5"));
                break;
            case 6:
                lowBound = this.alphaTransformation(18, parameters.getProperty("sap.material.low6"));
                highBound = this.alphaTransformation(18, parameters.getProperty("sap.material.high6"));
                break;
        }
        this.addRfcTableField(table, "MATNR_LOW", lowBound);
        this.addRfcTableField(table, "MATNR_HIGH", highBound);

    }

    private void addRfcTableField(JCoTable table, String fieldName, String fieldValue) {
        table.setValue(fieldName, fieldValue);
    }

    private String alphaTransformation(int sz, String value) {
        int delta = sz - value.length();
        StringBuilder tempStringBuilder = new StringBuilder(0);
        if (delta > 0) {
            tempStringBuilder.append("0".repeat(delta));
        }
        tempStringBuilder.append(value);
        return tempStringBuilder.toString();
    }

    private List<String> buildIdSequence(Map<String, MaterialUomPair> materialData) {
        List<String> ids = new ArrayList<>(materialData.keySet());
        Collections.sort(ids);
        return ids;
    }

}
