package ru.velkomfood.dms.info.controller;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import ru.velkomfood.dms.info.model.Document;
import ru.velkomfood.dms.info.model.Status;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DataProcessor extends Thread {


    private String threadName;
    private boolean firstStep;
    private long step;

    private final String DB_URl = "jdbc:sqlite:./dms.db";
    final String DEST_NAME1 = "R15";
    final String DEST_NAME2 = "R14";
    final String SUFFIX = ".jcoDestination";
    private Properties sap1, sap2;

    private JCoDestination destination1, destination2;
    private Connection connection;

    public DataProcessor(String threadName, boolean firstStep) {

        this.threadName = threadName;
        this.firstStep = firstStep;
        step = 0;
        sap1 = new Properties();
        createDestinationDataFile(DEST_NAME1, sap1);

        sap2 = new Properties();
        createDestinationDataFile(DEST_NAME2, sap2);

    }

    @Override
    public void run() {

        step++;

        try {

            openDbConnection();
            if (firstStep) {
                initDatabaseStructure();
                initSAPConnection();
            }
            uploadData();
            closeDbConnection();

            if (step > 1000000) {
                step = 0;
            }

        } catch (JCoException | SQLException e) {

            System.out.append(e.getMessage() + "\n");

        }

    }

    private void initSAPConnection() throws JCoException {
        destination1 = JCoDestinationManager.getDestination(DEST_NAME1);
        destination2 = JCoDestinationManager.getDestination(DEST_NAME2);
    }

    private void openDbConnection() throws SQLException {
        connection = DriverManager.getConnection(DB_URl);
    }

    private void closeDbConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void uploadData() throws JCoException {

        long counter = 0;

        long t1 = new java.util.Date().getTime();
        System.out.append("Start DMS service\n");

        JCoDestination destination;

        System.out.printf("Start the thread called %s\n", threadName);

        if ((step % 2) == 0) {
            destination = destination1;
        } else {
            destination = destination2;
        }

        JCoFunction bapiDocList = destination
                .getRepository().getFunction("BAPI_DOCUMENT_GETLIST");
        if (bapiDocList == null) {
            throw new RuntimeException("Function BAPI_DOCUMENT_GETLIST not found");
        }

        bapiDocList.getImportParameterList().setValue("DOCUMENTTYPE", "ZDO");
        bapiDocList.getImportParameterList().setValue("STATUSINTERN", "DB");
        bapiDocList.execute(destination);
        JCoTable docs = bapiDocList.getTableParameterList().getTable("DOCUMENTLIST");
        if (docs.getNumRows() > 0) {
            do {
                counter++;
                Document document = new Document();
                document.setId(docs.getLong("DOCUMENTNUMBER"));
                document.setExternalNumber(docs.getString("DOCUMENTNUMBER"));
                document.setDocType(docs.getString("DOCUMENTTYPE"));
                document.setDocPart(docs.getString("DOCUMENTPART"));
                document.setVersion(docs.getString("DOCUMENTVERSION"));
                document.setDescription(docs.getString("DESCRIPTION"));
                document.setUser(docs.getString("USERNAME"));
                if ((counter % 2000) == 0) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
                if (!isDocumentExists(document.getId())) {
                    createDocument(document);
                }
            } while (docs.nextRow());

        } // docs

        System.out.append("Finish DMS service\n");
        long t2 = new java.util.Date().getTime();
        showMessageAboutExecution(t1, t2);

    }

    private void createDestinationDataFile(String destName, Properties props) {

        File destCfg = new File(destName + SUFFIX);
        if (destCfg.isDirectory() || !destCfg.exists()) {
            try {
                OutputStream fos = new FileOutputStream(destCfg, false);
                props.store(fos, "Productive environment");
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void initDatabaseStructure() throws SQLException {

        StringBuilder sb = new StringBuilder(0);

        sb.append("CREATE TABLE IF NOT EXISTS documents(id INTEGER PRIMARY KEY, ext_number VARCHAR(25) NOT NULL,")
                .append(" doc_type VARCHAR(3) NOT NULL,")
                .append(" doc_part VARCHAR(3), version VARCHAR(3),")
                .append(" description VARCHAR(50), user VARCHAR(50))");

        Statement stmt = connection.createStatement();
        stmt.execute(sb.toString());
        sb.delete(0, sb.length());
        sb.append("CREATE TABLE IF NOT EXISTS doc_status(id INTEGER PRIMARY KEY,")
                .append(" vendor_id VARCHAR(10), vendor_name VARCHAR(50),")
                .append(" contract VARCHAR(50),")
                .append(" term1 DATE, term2 DATE, term3 DATE, term4 DATE, term5 DATE,")
                .append(" status1 VARCHAR(3), status2 VARCHAR(3), status3 VARCHAR(3),")
                .append(" status4 VARCHAR(3), status5 VARCHAR(3))");
        stmt.execute(sb.toString());
        stmt.close();

        System.out.println("All tables was created");

    }

    private void getDetailedInformationAboutDocuments() throws JCoException {

        long counter = 0;
        JCoFunction bapiDocDetail = destination2
                .getRepository().getFunction("BAPI_DOCUMENT_GETDETAIL2");
        if (bapiDocDetail == null) {
            throw new RuntimeException("Function BAPI_DOCUMENT_GETDETAIL2 not found");
        }
        Document document = new Document();
        String txtId = "";
        bapiDocDetail.getImportParameterList().setValue("DOCUMENTTYPE", document.getDocType());
        bapiDocDetail.getImportParameterList().setValue("DOCUMENTNUMBER", txtId);
        bapiDocDetail.getImportParameterList().setValue("DOCUMENTPART", document.getDocPart());
        bapiDocDetail.getImportParameterList().setValue("DOCUMENTVERSION", document.getVersion());
        bapiDocDetail.getImportParameterList().setValue("GETACTIVEFILES", " ");
        bapiDocDetail.getImportParameterList().setValue("GETDOCDESCRIPTIONS", " ");
        bapiDocDetail.getImportParameterList().setValue("GETDOCFILES", " ");
        bapiDocDetail.getImportParameterList().setValue("GETCLASSIFICATION", "X");
        if ((counter % 2) == 0) {
            bapiDocDetail.execute(destination1);
        } else {
            bapiDocDetail.execute(destination2);
        }
        JCoTable characteristics = bapiDocDetail.getTableParameterList()
                .getTable("CHARACTERISTICVALUES");
        if (characteristics.getNumRows() > 0) {
            do {
                String key = characteristics.getString("CHARNAME");
                String value = characteristics.getString("CHARVALUE");

            } while (characteristics.nextRow());
        }
    }

    private Map<String, String> getVendorInformation(String vendor, JCoDestination destination) throws JCoException {

        Map<String, String> vendorInfo = new HashMap<>();

        JCoFunction bapiVendor = destination1.getRepository().getFunction("BAPI_VENDOR_GETDETAIL");
        if (bapiVendor == null) {
            throw new RuntimeException("Function BAPI_VENDOR_GETDETAIL not found");
        }

        bapiVendor.getImportParameterList().setValue("VENDORNO", vendor);
        bapiVendor.execute(destination);

        JCoStructure generalData = bapiVendor.getExportParameterList()
                .getStructure("GENERALDETAIL");
        for (JCoField field: generalData) {
            switch (field.getName()) {
                case "NAME":
                    vendorInfo.put(vendor, field.getString());
                    break;
            }
        }

        return vendorInfo;
    }

    private java.sql.Date convertSapDateToSqlDate(String dateValue) {

        java.util.Date dt = null;
        SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
        try {
            dt = fmt.parse(dateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new java.sql.Date(dt.getTime());
    }

    private boolean isDocumentExists(long id) {

        boolean isExists = false;

        String sql = "SELECT id FROM documents WHERE id = " + id;
        Statement stmt = null;

        try {

            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    if (rs.isFirst()) {
                        isExists = true;
                        break;
                    }
                }
                rs.close();
            }

        } catch (SQLException sqex1) {

            sqex1.getMessage();

        } finally {

            try {
                stmt.close();
            } catch (SQLException sqex2) {
                sqex2.getMessage();
            }

        }

        return isExists;
    }

    private void createDocument(Document doc) {

        StringBuilder sb = new StringBuilder(0);

        sb.append("INSERT INTO documents")
                .append(" VALUES (?, ?, ?, ?, ?, ?, ?)");

        try {
            PreparedStatement pstmt = connection.prepareStatement(sb.toString());
            pstmt.setLong(1, doc.getId());
            pstmt.setString(2, doc.getExternalNumber());
            pstmt.setString(3, doc.getDocType());
            pstmt.setString(4, doc.getDocPart());
            pstmt.setString(5, doc.getVersion());
            pstmt.setString(6, doc.getDescription());
            pstmt.setString(7, doc.getUser());
            pstmt.executeUpdate();
            String success = String.format("The document number %d created\n", doc.getId());
            System.out.append(success);
            pstmt.close();
        } catch (SQLException e1) {
            e1.getMessage();
        }

    }

    private void createDocumentStatus(Status doc) {

        StringBuilder sb = new StringBuilder(0);

        sb.append("INSERT INTO doc_status")
                .append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement pstmt = null;

        try {
            pstmt = connection.prepareStatement(sb.toString());
            pstmt.setLong(1, doc.getId());
            pstmt.setString(2, doc.getVendor());
            pstmt.setString(3, doc.getVendorName());
            pstmt.setString(4, doc.getContract());
            pstmt.setDate(5, doc.getTerm1());
            pstmt.setDate(6, doc.getTerm2());
            pstmt.setDate(7, doc.getTerm3());
            pstmt.setDate(8, doc.getTerm4());
            pstmt.setDate(9, doc.getTerm5());
            pstmt.setString(10, doc.getStatus1());
            pstmt.setString(11, doc.getStatus2());
            pstmt.setString(12, doc.getStatus3());
            pstmt.setString(13, doc.getStatus4());
            pstmt.setString(14, doc.getStatus5());
            pstmt.executeUpdate();
            String success = String.format("%d status created\n", doc.getId());
            System.out.append(success);
        } catch (SQLException e1) {
            e1.getMessage();
        } finally {
            try {
                pstmt.close();
            } catch (SQLException e2) {
                e2.getMessage();
            }
        }

    }

    private void updateDocument(Document doc) {

        StringBuilder sb = new StringBuilder(0);

        sb.append("UPDATE documents")
                .append(" SET term1 = ?, term2 = ?, term3 = ?, term4 = ?, term5 = ?,")
                .append(" status1 = ?, status2 = ?, status3 = ?, status4 = ?, status5 = ?")
                .append(" WHERE id = ?");

        PreparedStatement pstmt = null;

    }

    private void showMessageAboutExecution(long v1, long v2) {

        long delta = (v2 - v1) / 1000;
        String unit = "sec";

        if (delta >= 60) {
            delta /= 60;
            if (delta < 60) {
                unit = "min";
            } else {
                delta /= delta;
                unit = "hours";
            }
        }

        String message = String.format("Time of execution is %d %s\n", delta, unit);
        System.out.append(message);

    }


}
