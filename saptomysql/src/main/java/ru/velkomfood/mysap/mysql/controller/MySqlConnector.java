package ru.velkomfood.mysap.mysql.controller;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import ru.velkomfood.mysap.mysql.model.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by dpetrov on 12.07.16.
 */
public class MySqlConnector {

    private static MySqlConnector instance;
    private String threadName;
    private Thread t_mysql;
    private DataSource dataSource;
    private SapConnector sapConnector;

    private Queue<CustomerEntity> customers;
    private Queue<VendorEntity> vendors;
    private Queue<MaterialEntity> materials;
    private Queue<MaterialPriceEntity> materialPrices;
    private Queue<StocksEntity> stocks;
    private Queue<MrpItemsEntity> mrpitems;
    private Queue<MrpTotalsEntity> mrptotals;
    private Queue<LogisticInfoRecord> logisticInfoRecords;
    private Queue<PivotTable> pivotTables;

    private MySqlConnector(String threadName) {

        this.threadName = threadName;
        mrptotals = new ConcurrentLinkedQueue<>();
        pivotTables = new ConcurrentLinkedQueue<>();

        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("XXXXXX");
        ds.setPort(3306);
        ds.setDatabaseName("XXXXXX");
        ds.setUser("XXXXXX");
        ds.setPassword("XXXXXXX");
        dataSource = (DataSource) ds;

    }

    public static MySqlConnector getInstance(String threadName) {

        if (instance == null) instance = new MySqlConnector(threadName);

        return instance;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setSapConnector(SapConnector sapConnector) {
        this.sapConnector = sapConnector;
    }

    public Queue<CustomerEntity> getCustomers() {
        return customers;
    }

    public void setCustomers(Queue<CustomerEntity> customers) {
        this.customers = customers;
    }

    public Queue<VendorEntity> getVendors() {
        return vendors;
    }

    public void setVendors(Queue<VendorEntity> vendors) {
        this.vendors = vendors;
    }

    public Queue<MaterialEntity> getMaterials() {
        return materials;
    }

    public void setMaterials(Queue<MaterialEntity> materials) {
        this.materials = materials;
    }

    public Queue<MaterialPriceEntity> getMaterialPrices() {
        return materialPrices;
    }

    public void setMaterialPrices(Queue<MaterialPriceEntity> materialPrices) {
        this.materialPrices = materialPrices;
    }

    public Queue<StocksEntity> getStocks() {
        return stocks;
    }

    public void setStocks(Queue<StocksEntity> stocks) {
        this.stocks = stocks;
    }

    public Queue<MrpItemsEntity> getMrpitems() {
        return mrpitems;
    }

    public void setMrpitems(Queue<MrpItemsEntity> mrpitems) {
        this.mrpitems = mrpitems;
    }

    public Queue<MrpTotalsEntity> getMrptotals() {
        return mrptotals;
    }

    public void setMrptotals(Queue<MrpTotalsEntity> mrptotals) {
        this.mrptotals = mrptotals;
    }

    public void setLogisticInfoRecords(Queue<LogisticInfoRecord> logisticInfoRecords) {
        this.logisticInfoRecords = logisticInfoRecords;
    }

    public Queue<LogisticInfoRecord> getLogisticInfoRecords() {
        return logisticInfoRecords;
    }


    public void run() {

        try {

            refreshDatabase(dataSource);
            insertMaterials(dataSource);
            insertCustomersAndVendors(dataSource);
            insertMaterialPrices(dataSource);
            insertStocks(dataSource);
            insertLogisticInfoRecords(dataSource);
            insertMrpItems(dataSource);
            calculateTotals(dataSource);

        } catch (SQLException dex) {

            dex.printStackTrace();
        }

    }

    // Private section with methods for database loading

    private void refreshDatabase(DataSource ds) throws SQLException {

        Connection connection = ds.getConnection();

        String sql = "DELETE FROM lfa1";

        Statement stmt = connection.createStatement();

        try {

            stmt.addBatch(sql);

            sql = "DELETE FROM kna1";
            stmt.addBatch(sql);

            sql = "DELETE FROM lfa1";
            stmt.addBatch(sql);

            sql = "DELETE FROM mara";
            stmt.addBatch(sql);

            sql = "DELETE FROM mbew";
            stmt.addBatch(sql);

            sql = "DELETE FROM mminforecords";
            stmt.addBatch(sql);

            sql = "DELETE FROM stocks";
            stmt.addBatch(sql);

            sql = "DELETE FROM mrpitems";
            stmt.addBatch(sql);

            sql = "DELETE FROM availtotals";
            stmt.addBatch(sql);

            sql = "DELETE FROM pritotals";
            stmt.addBatch(sql);

            sql = "DELETE FROM sectotals";
            stmt.addBatch(sql);

            stmt.executeBatch();

        } finally {

            stmt.close();
            connection.close();

        }
    }

    private void insertMaterials(DataSource ds) throws SQLException {

        Connection connection = ds.getConnection();
        final String sql = "INSERT INTO mara VALUES (?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);

        try {

            materials.forEach(me -> {

                try {
                    pstmt.setString(1, me.getMatnr());
                    pstmt.setString(2, me.getDescription());
                    pstmt.addBatch();
                } catch (SQLException sqex) {
                    sqex.printStackTrace();
                }

            });

            pstmt.executeBatch();

        } finally {

            pstmt.close();
            connection.close();

        }

    }

    private void insertMaterialPrices(DataSource ds) throws SQLException {

        Connection connection = ds.getConnection();
        final String sql = "INSERT INTO mbew VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);

        try {

            materialPrices.forEach(mp -> {

                try {
                    pstmt.setString(1, mp.getMatnr());
                    pstmt.setInt(2, mp.getWerks());
                    pstmt.setInt(3, mp.getValuation_area());
                    pstmt.setString(4, mp.getPur_group());
                    pstmt.setString(5, String.valueOf(mp.getPrice_control()));
                    pstmt.setBigDecimal(6, mp.getMoving_price());
                    pstmt.setBigDecimal(7, mp.getStandard_price());
                    pstmt.setString(8, mp.getCurrency());
                    pstmt.setString(9, mp.getBase_unit());
                    pstmt.addBatch();
                } catch (SQLException sqex) {
                    sqex.printStackTrace();
                }

            });

            pstmt.executeBatch();

        } finally {

            pstmt.close();
            connection.close();

        }
    }

    private void insertStocks(DataSource ds) throws SQLException {

        Connection connection = ds.getConnection();
        final String sql = "INSERT INTO stocks VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);

        try {

            stocks.forEach(se -> {

                try {
                    pstmt.setInt(1, se.getWerks());
                    pstmt.setString(2, se.getMatnr());
                    pstmt.setString(3, se.getAvailDate());
                    pstmt.setString(4, se.getPurchaseGroup());
                    pstmt.setString(5, se.getBaseUnit());
                    pstmt.setBigDecimal(6, se.getSafetyStock());
                    pstmt.setBigDecimal(7, se.getPlantStock());
                    pstmt.addBatch();
                } catch (SQLException sqex) {
                    sqex.printStackTrace();
                }

            });

            pstmt.executeBatch();

        } finally {

            pstmt.close();
            connection.close();

        }

    }

    private void insertCustomersAndVendors(DataSource ds) throws SQLException {

        Connection connection = ds.getConnection();
        final String sql_kna = "INSERT INTO kna1 VALUES (?, ?)";
        final String sql_lfa = "INSERT INTO lfa1 VALUES (?, ?)";
        PreparedStatement pstmt_kna = connection.prepareStatement(sql_kna);
        PreparedStatement pstmt_lfa = connection.prepareStatement(sql_lfa);

        try {

            // Customers table
            customers.forEach(ce -> {

                try {
                    pstmt_kna.setString(1, ce.getKunnr());
                    pstmt_kna.setString(2, ce.getName1());
                    pstmt_kna.addBatch();
                } catch (SQLException sqex) {
                    sqex.printStackTrace();
                }

            });

            pstmt_kna.executeBatch();

            // Vendors table
            vendors.forEach(ve -> {

                try {
                    pstmt_lfa.setString(1, ve.getLifnr());
                    pstmt_lfa.setString(2, ve.getName1());
                    pstmt_lfa.addBatch();
                } catch (SQLException sqex) {
                    sqex.printStackTrace();
                }

            });

            pstmt_lfa.executeBatch();

        } finally {

            pstmt_kna.close();
            pstmt_lfa.close();
            connection.close();

        }

    }

    // Insert logistic info-records
    private void insertLogisticInfoRecords(DataSource ds) throws SQLException {

        Connection connection = ds.getConnection();

        final String sql = "INSERT INTO mminforecords VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = connection.prepareStatement(sql);

        try {

            logisticInfoRecords.forEach(record -> {

                try {

                    pstmt.setString(1, record.getInfoRecordNumber());
                    pstmt.setString(2, record.getMaterial());
                    pstmt.setString(3, record.getVendor());

                    pstmt.setString(4, record.getDateCreation());
                    String[] temp = record.getDateCreation().split("-");
                    pstmt.setInt(5, Integer.parseInt(temp[1]));
                    pstmt.setInt(6, Integer.parseInt(temp[0]));
                    pstmt.addBatch();

                } catch (SQLException sqex) {
                    sqex.printStackTrace();
                }

            });

            pstmt.executeBatch();

        } finally {

            if (pstmt != null) pstmt.close();
            if (connection != null) connection.close();

        }

    } // insert logistic info-records

    private void insertMrpItems(DataSource ds) throws SQLException {

//        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Connection connection = ds.getConnection();

        String sql = "INSERT INTO mrpitems ";
        sql = sql + "(werks, matnr, avail_date, pur_group, per_segmt, base_unit, pquantity, squantity, availquantity)";
        sql = sql + " VALUES ";
        sql = sql + "(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = connection.prepareStatement(sql);

        try {

            mrpitems.forEach(mrpi -> {

                try {
                    pstmt.setInt(1, mrpi.getPlant());
                    pstmt.setString(2, mrpi.getMatnr());
                    pstmt.setString(3, mrpi.getMrpDate());
                    pstmt.setString(4, mrpi.getPurchaseGroup());
                    pstmt.setString(5, mrpi.getPer_segmt());
                    pstmt.setString(6, mrpi.getBaseUnit());
                    BigDecimal minus = BigDecimal.valueOf((-1.00));
                    BigDecimal pq = mrpi.getPquantity().multiply(minus);
                    BigDecimal sq = mrpi.getSquantity().multiply(minus);
                    BigDecimal aq = mrpi.getAvailquantity().multiply(minus);
                    pstmt.setBigDecimal(7, pq);
                    pstmt.setBigDecimal(8, sq);
                    pstmt.setBigDecimal(9, aq);
                    pstmt.addBatch();
                } catch (SQLException sqex) {
                    sqex.printStackTrace();
                }

            });

            pstmt.executeBatch();

        } finally {

            pstmt.close();
            connection.close();

        }

    }

    private void calculateTotals(DataSource ds) throws SQLException {

        Map<String, String> currcodes = new ConcurrentHashMap<>();
        Map<String, String> matUnits = new ConcurrentHashMap<>();
        Map<String, String> purGroups = new ConcurrentHashMap<>();
        Map<String, BigDecimal[]> prices = new ConcurrentHashMap<>();

        // For sums gathering

        Connection connection = ds.getConnection();
        String sql = "";
        Statement stmt = connection.createStatement();
        ResultSet rs_mat, rs_q;

        try {

            // Search currency codes
            sql = "SELECT DISTINCT matnr, currency FROM mbew ORDER BY matnr";
            rs_mat = stmt.executeQuery(sql);

            while (rs_mat.next()) {
                currcodes.put(rs_mat.getString("matnr"), rs_mat.getString("currency"));
            }
            rs_mat.close();

            // Search base units
            sql = "SELECT DISTINCT matnr, base_unit FROM mbew ORDER BY matnr";

            rs_mat = stmt.executeQuery(sql);

            while (rs_mat.next()) {
                matUnits.put(rs_mat.getString("matnr"), rs_mat.getString("base_unit"));
            }
            rs_mat.close();

            // Search prices
            sql = "SELECT DISTINCT matnr, moving_price, standard_price FROM mbew ORDER BY matnr";
            rs_mat = stmt.executeQuery(sql);

            while (rs_mat.next()) {
                BigDecimal[] values = {rs_mat.getBigDecimal("moving_price"),
                                        rs_mat.getBigDecimal("standard_price")};

                prices.put(rs_mat.getString("matnr"), values);
            }
            rs_mat.close();

            // Search purchase groups, if they are exist in the items
            sql = "SELECT DISTINCT matnr, avail_date, pur_group FROM mrpitems ";
            sql = sql + "WHERE pur_group <> \'\' ORDER BY matnr, avail_date";

            rs_mat = stmt.executeQuery(sql);

            while (rs_mat.next()) {
                String key = rs_mat.getString("matnr") + "-" + rs_mat.getString("avail_date");
                purGroups.put(key, rs_mat.getString("pur_group"));
            }
            rs_mat.close();

            // Primary requirements
            sql = "SELECT werks, matnr, avail_date, SUM( pquantity ) AS pq ";
            sql = sql + "FROM mrpitems WHERE pquantity <> 0.000 GROUP BY werks, matnr, avail_date";
            rs_q = stmt.executeQuery(sql);

            sql = "INSERT INTO pritotals VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);

            while (rs_q.next()) {

                String material = rs_q.getString("matnr");
                String avail_date = rs_q.getString("avail_date");
                String key = material + "-" + avail_date;

                pstmt.setString(1, material);
                pstmt.setInt(2, rs_q.getInt("werks"));
                String[] datum = avail_date.split("-");
                pstmt.setInt(3, Integer.parseInt(datum[0]));
                pstmt.setInt(4, Integer.parseInt(datum[1]));
                pstmt.setString(5, purGroups.get(key));
                pstmt.setString(6, matUnits.get(material));
                pstmt.setString(7, currcodes.get(material));
                pstmt.setBigDecimal(8, rs_q.getBigDecimal("pq"));
                BigDecimal[] values = prices.get(rs_q.getString("matnr"));
                BigDecimal weighted = values[0].multiply(rs_q.getBigDecimal("pq"));
                BigDecimal fixed = values[1].multiply(rs_q.getBigDecimal("pq"));
                pstmt.setBigDecimal(9, weighted);
                pstmt.setBigDecimal(10, fixed);
                pstmt.addBatch();

            }
            rs_q.close();
            pstmt.executeBatch();

            // Secondary requirements
            sql = "SELECT werks, matnr, avail_date, SUM( squantity ) AS sq ";
            sql = sql + "FROM mrpitems WHERE squantity <> 0.000 GROUP BY werks, matnr, avail_date";
            rs_q = stmt.executeQuery(sql);

            sql = "INSERT INTO sectotals VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(sql);

            while (rs_q.next()) {

                String material = rs_q.getString("matnr");
                String avail_date = rs_q.getString("avail_date");
                String key = material + "-" + avail_date;

                pstmt.setString(1, material);
                pstmt.setInt(2, rs_q.getInt("werks"));
                String[] datum = avail_date.split("-");
                pstmt.setInt(3, Integer.parseInt(datum[0]));
                pstmt.setInt(4, Integer.parseInt(datum[1]));
                pstmt.setString(5, purGroups.get(key));
                pstmt.setString(6, matUnits.get(material));
                pstmt.setString(7, currcodes.get(material));
                pstmt.setBigDecimal(8, rs_q.getBigDecimal("sq"));
                BigDecimal[] values = prices.get(rs_q.getString("matnr"));
                BigDecimal weighted = values[0].multiply(rs_q.getBigDecimal("sq"));
                BigDecimal fixed = values[1].multiply(rs_q.getBigDecimal("sq"));
                pstmt.setBigDecimal(9, weighted);
                pstmt.setBigDecimal(10, fixed);
                pstmt.addBatch();

            }
            rs_q.close();
            pstmt.executeBatch();

            // Available stocks
            sql = "SELECT werks, matnr, avail_date, SUM( availquantity ) AS aq ";
            sql = sql + "FROM mrpitems WHERE availquantity <> 0.000 GROUP BY werks, matnr, avail_date";
            rs_q = stmt.executeQuery(sql);

            sql = "INSERT INTO availtotals VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(sql);

            while (rs_q.next()) {

                String material = rs_q.getString("matnr");
                String avail_date = rs_q.getString("avail_date");
                String key = material + "-" + avail_date;

                pstmt.setString(1, material);
                pstmt.setInt(2, rs_q.getInt("werks"));
                String[] datum = avail_date.split("-");
                pstmt.setInt(3, Integer.parseInt(datum[0]));
                pstmt.setInt(4, Integer.parseInt(datum[1]));
                pstmt.setString(5, purGroups.get(key));
                pstmt.setString(6, matUnits.get(material));
                pstmt.setString(7, currcodes.get(material));
                pstmt.setBigDecimal(8, rs_q.getBigDecimal("aq"));
                BigDecimal[] values = prices.get(rs_q.getString("matnr"));
                BigDecimal weighted = values[0].multiply(rs_q.getBigDecimal("aq"));
                BigDecimal fixed = values[1].multiply(rs_q.getBigDecimal("aq"));
                pstmt.setBigDecimal(9, weighted);
                pstmt.setBigDecimal(10, fixed);
                pstmt.addBatch();

            }
            rs_q.close();
            pstmt.executeBatch();

        } finally {

            stmt.close();
            connection.close();

        } // try-catch-finally block

    } // calculate totals


}
