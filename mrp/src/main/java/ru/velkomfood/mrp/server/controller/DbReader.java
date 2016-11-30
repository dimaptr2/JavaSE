package ru.velkomfood.mrp.server.controller;

import ru.velkomfood.mrp.server.model.Stock;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dpetrov on 28.11.16.
 */
public class DbReader {

    private String dbPath;
    private Connection connection;

    // Data containers and parameters for searching
    private int plant;
    private String purchaseGroup;
    private Map<String, List<Integer>> periods;

    private Map<Integer, Stock> mstocks;
    List<Map<Integer, List<BigDecimal>>> requirements;

    public DbReader(String dbPath) {
        this.dbPath = dbPath;
        mstocks = new ConcurrentHashMap<>();
        requirements = new ArrayList<>();
    }

    // Open and close a connection to the database.
    public void openConnection() throws SQLException {

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);

        String dbName = "jdbc:sqlite:" + dbPath + "/mrpdata-" + year + ".db";
        connection = DriverManager.getConnection(dbName);

    }

    public void closeDbConnection() throws SQLException {
        if (connection != null && !connection.isClosed())
            connection.close();
    }

    // Checking that a connection is valid
    public boolean connectionIsValid() throws SQLException {
        if (connection == null) return false;
        else if (connection != null && connection.isClosed()) return false;
        else return true;
    }

    // setters and getters

    public void setPlant(int plant) {
        this.plant = plant;
    }

    public void setPurchaseGroup(String purchaseGroup) {
        this.purchaseGroup = purchaseGroup;
    }

    public void setPeriods(Map<String, List<Integer>> periods) {
        this.periods = periods;
    }

    // Here is a main logic
    public void searchAllData() throws SQLException {
        readStocks();
        readRequirements();
    }

    // Private section
    private void readStocks() throws SQLException {

        if (!mstocks.isEmpty()) mstocks.clear();

        Statement statement = connection.createStatement();

        StringBuilder sb = new StringBuilder(0);
        sb.append("SELECT m.matnr, m.description, s.werks, s.lgort,");
        sb.append(" b.base_uom, s.free_stock, s.quality_stock, s.blocked_stock");
        sb.append(" FROM mara AS m ").append("INNER JOIN mbew AS b ");
        sb.append("ON m.matnr = b.matnr ").append("INNER JOIN stocks AS s ");
        sb.append("ON m.matnr = s.matnr ORDER BY m.matnr");

        try {
            ResultSet rs = statement.executeQuery(sb.toString());
            while (rs.next()) {
                Stock stock = new Stock();
                stock.setMaterial(rs.getInt("matnr"));
                stock.setDescription(rs.getString("description"));
                stock.setStoragePlace(rs.getInt("lgort"));
                stock.setFreeStock(rs.getBigDecimal("free_stock"));
                stock.setQualityStock(rs.getBigDecimal("quality_stock"));
                stock.setBlockedStock(rs.getBigDecimal("blocked_stock"));
                mstocks.put(stock.getMaterial(), stock);
            }
            rs.close();
        } finally {
            statement.close();
        }

    }

    private void readRequirements() throws SQLException {

        if (!requirements.isEmpty()) requirements.clear();

        Statement stmt = connection.createStatement();

        try {
            ResultSet rs = stmt.executeQuery("SELECT matnr FROM mara ORDER BY matnr");
            while (rs.next()) {
                Map<Integer, List<BigDecimal>> rq = readSingleRequirement(rs.getInt("matnr"));
                if (rq != null) requirements.add(rq);
            }
            rs.close();
        } finally {
            stmt.close();
        }

    }

    // Read a quantity by a single row.
    private Map<Integer, List<BigDecimal>> readSingleRequirement(int i_mat) throws SQLException {

        // Local variables for calculations
        Map<Integer, List<BigDecimal>> total = new LinkedHashMap<>();

        StringBuilder sb = new StringBuilder(0);
        sb.append("SELECT matnr, pur_group, year, month, req_total");
        sb.append(" FROM totals ").append(" WHERE matnr = ").append(i_mat).append(" AND werks = ");
        sb.append(plant).append(" AND pur_group = ");
        sb.append("\'").append(purchaseGroup).append("\'").append(" AND year = ");

        Iterator<Map.Entry<String, List<Integer>>> it = periods.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, List<Integer>> entry = it.next();
            String key = entry.getKey();
            List<Integer> values = entry.getValue();
            String sql = sb.toString();
            sql = sql + key + " AND month IN (";
            for (int i = 0; i < values.size(); i++) {
                if (i == (values.size() - 1)) {
                    sql = sql + values.get(i) + ")";
                } else {
                    sql = sql + values.get(i) + ", ";
                }
            }
            Statement stmt = connection.createStatement();
            try {

                ResultSet result = stmt.executeQuery(sql);
                while (result.next()) {

                }
                result.close();
            } finally {
                stmt.close();
            }
        } // while

        return total;
    }

}
