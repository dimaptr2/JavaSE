package ru.velkomfood.fin.cash.controller;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import ru.velkomfood.fin.cash.model.CashJournal;
import ru.velkomfood.fin.cash.model.Material;
import ru.velkomfood.fin.cash.model.Receipt;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by dpetrov on 13.02.2017.
 */
public class DbManager {

    private static DbManager instance;
    private DataSource myds;
    private Connection connection;

    CacheEngine cache = CacheEngine.getInstance();

    private DbManager() { }

    public static DbManager getInstance() {
        if (instance == null) {
            instance = new DbManager();
        }
        return instance;
    }

    public void createDataSource() {
      MysqlDataSource ds = new MysqlDataSource();
      ds.setServerName("srv-sapapp.eatmeat.ru");
      ds.setPort(3306);
      ds.setDatabaseName("cache");
      ds.setUser("paymaster");
      ds.setPassword("12345678");
      this.myds = ds;
    }

    // Opening and closing connections to databases (global and local)
    public void openDbConnection() throws SQLException {
        connection = myds.getConnection();
    }

    public void closeDbConnection() throws SQLException {

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }

    }

    // DB initialization
    public void initDatabase() throws SQLException {

        Statement stmt = connection.createStatement();
        String[] sql = buildInitSql();

        try {
            for (String line : sql) {
                stmt.addBatch(line);
            }
            stmt.executeBatch();
        } catch (SQLException sqex) {
            sqex.printStackTrace();
        } finally {
            stmt.close();
        }

    }

    // Upload master data
    public boolean uploadMaterialMasterData() {

        boolean errorCode = false;
        PreparedStatement pstmt = null;
        StringBuilder sb = new StringBuilder(0);
        sb.append("INSERT INTO materials VALUES (?, ?)");

        Iterator<Map.Entry<Long, Material>> it = cache.getMaterials().entrySet().iterator();
        try {
            pstmt = connection.prepareStatement(sb.toString());
        } catch (SQLException e) {
            errorCode = true;
        }

        try {

            while (it.hasNext()) {
                Map.Entry<Long, Material> entry = it.next();
                try {
                    if (existsMaterial(entry.getKey())) {
                        continue;
                    }
                    pstmt.setLong(1, entry.getKey());
                    pstmt.setString(2, entry.getValue().getDescription());
                    pstmt.addBatch();
                } catch (SQLException se) {
                    continue;
                }
            } // while

            try {
                pstmt.executeBatch();
            } catch (SQLException e) {
                errorCode = true;
            }

        }
        finally {

            try {
                pstmt.close();
            } catch (SQLException e) {
                errorCode = true;
            }

        }

        return errorCode;
    }

    // Checking the existence of the key in the database
    public boolean existsMaterial(long materialId) throws SQLException {

        boolean flag = false;

        StringBuilder sb = new StringBuilder(0);
        sb.append("SELECT id FROM materials");
        sb.append(" WHERE id = ").append(materialId);

        Statement stmt = connection.createStatement();

        try {
            ResultSet rs = stmt.executeQuery(sb.toString());
            if (rs.first()) {
                flag = true;
            }
            rs.close();
        } finally {
            stmt.close();
        }

        return flag;
    }

    // create cash journal rows
    public void uploadCashJournal() {

    }

    // Get latest receipt number
    public long readTheLatestCashJournalKey() throws SQLException {

        long index = 0;
        StringBuilder sb = new StringBuilder(0);
        sb.append("SELECT MAX( id ) AS value FROM cash_journal");

        Statement stmt = connection.createStatement();

        try {
            ResultSet rs = stmt.executeQuery(sb.toString());
            if (rs.next()) {
                index = rs.getLong("value");
            }
            rs.close();
        } finally {
            stmt.close();
        }

        return index;
    }

    // Find the row that was printed
    public CashJournal findCashJournalRowById(Long rowId) throws SQLException {

        CashJournal cj = new CashJournal();
        Statement stmt = connection.createStatement();

        try {
            StringBuilder sb = new StringBuilder(0);
            sb.append("SELECT * FROM cash_journal WHERE id = ").append(rowId);
            ResultSet rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {

            }
            rs.close();
        } finally {
            stmt.close();
        }

        return cj;
    }

    // Find all receipts
    public void findCashJournalRowsByDate(String dt) throws SQLException {

        if (!cache.getJournalList().isEmpty()) {
            cache.getJournalList().clear();
        }

        StringBuilder sb = new StringBuilder(0);

        sb.append("SELECT t1.id, t1.cajo_number, t1.posting_number, t1.company_code, t1.year,").append(" ");
        sb.append("t1.post_date, t1.pos_text, t1.delivery_id, t2.status_id").append(" ");
        sb.append("FROM cash_journal AS t1").append(" ");
        sb.append("INNER JOIN cs_status AS t2").append(" ");
        sb.append("ON t2.cj_id = t1.id").append(" ");
        sb.append("WHERE t1.post_date = \'" + dt + "\'").append(" ");
        sb.append("ORDER BY t1.id");

        Statement stmt = connection.createStatement();

        try {
            ResultSet rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                CashJournal cj = new CashJournal();
                cj.setId(rs.getLong("id"));
                cj.setPostingNumber(rs.getLong("posting_number"));
                cj.setCompanyCode(rs.getString("company_code"));
                cj.setCajoNumber(rs.getString("cajo_number"));
                cj.setYear(rs.getInt("year"));
                cj.setPostingDate(rs.getString("post_date"));
                cj.setDeliveryId(rs.getLong("delivery_id"));
                cj.setPositionText(rs.getString("pos_text"));
                cj.setStatus(rs.getString("status_id"));
                cache.getJournalList().add(cj);
            }
            rs.close();
        } finally {
            stmt.close();
        }


    }

    // PRIVATE SECTION

    private String[] buildInitSql() {

        String[] commands = new String[4];
        StringBuilder sb = new StringBuilder(0);

        sb.append("CREATE TABLE IF NOT EXISTS materials").
                append(" (id INT PRIMARY KEY, description VARCHAR(50),");
        sb.append(" base_uom VARCHAR(3), net_weight DECIMAL(20,3))");
        commands[0] = sb.toString();
        sb.delete(0, sb.length());

        sb.append("CREATE TABLE IF NOT EXISTS cash_journal");
        sb.append(" (id INT PRIMARY KEY AUTO_INCREMENT, cajo_number VARCHAR(4),");
        sb.append(" company_code VARCHAR(4), year INT, posting_number INT,");
        sb.append(" amount_receipt DECIMAL(20,2), amount_payments DECIMAL(20,2),");
        sb.append(" net_amount DECIMAL(20,2), partner_name VARCHAR(35),");
        sb.append(" doc_date DATE, post_date DATE, doc_number VARCHAR(30),");
        sb.append(" pos_text VARCHAR(50), delivery_id INT, in_date DATETIME NOT NULL)");
        commands[1] = sb.toString();
        sb.delete(0, sb.length());

        sb.append("CREATE TABLE IF NOT EXISTS cj_items");
        sb.append(" (cj_id INT NOT NULL, delivery_id INT NOT NULL, posnr INT NOT NULL,");
        sb.append(" matnr INT, mat_text VARCHAR(40),");
        sb.append(" uom VARCHAR(3),  quantity DECIMAL(20,3), quantity_kg DECIMAL(20,3),");
        sb.append(" PRIMARY KEY (cj_id, delivery_id, posnr))");
        commands[2] = sb.toString();
        sb.delete(0, sb.length());

        sb.append("CREATE TABLE IF NOT EXISTS cs_status");
        sb.append(" (cj_id INT PRIMARY KEY, status_id VARCHAR(8) NOT NULL,");
        sb.append(" FOREIGN KEY (cj_id) REFERENCES cash_journal(id))");
        commands[3] = sb.toString();
        sb.delete(0, sb.length());

        return commands;
    }

}
