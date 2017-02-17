package ru.velkomfood.fin.cache.controller;

import ru.velkomfood.fin.cache.model.SAP.CashJournal;
import ru.velkomfood.fin.cache.model.SAP.Material;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by dpetrov on 28.12.2016.
 */
public class DbManager {

    private static DbManager instance;
//    private DataSource myds;
//    private Connection connection;
    private Connection localConnection;

    CacheEngine cache = CacheEngine.getInstance();

    private DbManager() { }

    public static DbManager getInstance() {
        if (instance == null) {
            instance = new DbManager();
        }
        return instance;
    }

  public void createDataSource() {
//      MysqlDataSource ds = new MysqlDataSource();
//      ds.setServerName("srv-sapapp.eatmeat.ru");
//      ds.setPort(3306);
//      ds.setDatabaseName("cache");
//      ds.setUser("paymaster");
//      ds.setPassword("12345678");
//      this.myds = ds;
  }

  // Opening and closing connections to databases (global and local)
  public void openDbConnection() throws SQLException {

      String databaseUrl;
      java.util.Date now = new Date();
      SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

      String[] temp = fmt.format(now).split("-");
      databaseUrl = "jdbc:sqlite:DB/cj-" + temp[0] + ".db";
      localConnection = DriverManager.getConnection(databaseUrl);
//      connection = myds.getConnection();
  }

    public void closeDbConnection() throws SQLException {

        //        if (connection != null && !connection.isClosed()) {
//            connection.close();
//        }

        if (localConnection != null && !localConnection.isClosed()) {
            localConnection.close();
        }

  }

  // DB initialization
  public void initLocalDatabase() throws SQLException {

      Statement stmt = localConnection.createStatement();
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
    public void uploadMaterialMasterData() throws SQLException {

        PreparedStatement pstmt = null;
        StringBuilder sb = new StringBuilder(0);
        sb.append("INSERT INTO materials VALUES (?, ?)");

        Iterator<Map.Entry<Long, Material>> it = cache.getMaterials().entrySet().iterator();
        pstmt = localConnection.prepareStatement(sb.toString());
        try {
            while (it.hasNext()) {
                Map.Entry<Long, Material> entry = it.next();
                if (existsMaterial(entry.getKey())) {
                    continue;
                }
                pstmt.setLong(1, entry.getKey());
                pstmt.setString(2, entry.getValue().getDescription());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } finally {
            pstmt.close();
        }

    }

    // Checking the existence of the key in the database
    public boolean existsMaterial(long materialId) throws SQLException {

        boolean flag = false;

        StringBuilder sb = new StringBuilder(0);
        sb.append("SELECT id FROM materials");
        sb.append(" WHERE id = ").append(materialId);

        Statement stmt = localConnection.createStatement();

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

        Statement stmt = localConnection.createStatement();

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
        Statement stmt = localConnection.createStatement();

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

    // PRIVATE SECTION

    private String[] buildInitSql() {

        String[] commands = new String[4];
        StringBuilder sb = new StringBuilder(0);

        sb.append("CREATE TABLE IF NOT EXISTS materials").
        append(" (id INTEGER PRIMARY KEY, description VARCHAR(50),");
        sb.append(" base_uom VARCHAR(3), net_weight DECIMAL(20,3))");
        commands[0] = sb.toString();
        sb.delete(0, sb.length());

        sb.append("CREATE TABLE IF NOT EXISTS cash_journal");
        sb.append(" (id INTEGER PRIMARY KEY AUTOINCREMENT, cajo_number VARCHAR(4),");
        sb.append(" company_code VARCHAR(4), year INTEGER, posting_number INTEGER,");
        sb.append(" amount_receipt DECIMAL(20,2), amount_payments DECIMAL(20,2),");
        sb.append(" net_amount DECIMAL(20,2), partner_name VARCHAR(35),");
        sb.append(" doc_date DATE, post_date DATE, doc_number VARCHAR(30),");
        sb.append(" pos_text VARCHAR(50), delivery_id INTEGER, in_date DATETIME NOT NULL)");
        commands[1] = sb.toString();
        sb.delete(0, sb.length());

        sb.append("CREATE TABLE IF NOT EXISTS cj_items");
        sb.append(" (cj_id INTEGER NOT NULL, delivery_id INTEGER NOT NULL, posnr INTEGER NOT NULL,");
        sb.append(" matnr INTEGER, mat_text VARCHAR(40),");
        sb.append(" uom VARCHAR(3),  quantity DECIMAL(20,3), quantity_kg DECIMAL(20,3),");
        sb.append(" PRIMARY KEY (cj_id, delivery_id, posnr))");
        commands[2] = sb.toString();
        sb.delete(0, sb.length());

        sb.append("CREATE TABLE IF NOT EXISTS cs_status");
        sb.append(" (cj_id INTEGER PRIMARY KEY, status_id VARCHAR(8) NOT NULL,");
        sb.append(" FOREIGN KEY (cj_id) REFERENCES cash_journal(id))");
        commands[3] = sb.toString();
        sb.delete(0, sb.length());

        return commands;
    }

}
