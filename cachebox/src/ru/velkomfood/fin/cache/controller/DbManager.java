package ru.velkomfood.fin.cache.controller;

import ru.velkomfood.fin.cache.model.CashJournal;
import ru.velkomfood.fin.cache.model.DeliveryHead;
import ru.velkomfood.fin.cache.model.DeliveryItem;

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

    private Map<Integer, String> materials;
    private List<CashJournal> journalList;
    private List<DeliveryHead> heads;
    private List<DeliveryItem> items;

    private DbManager() { }

    public static DbManager getInstance() {
        if (instance == null) {
            instance = new DbManager();
        }
        return instance;
    }

  public void createDataSource() {  }

  // Opening and closing connections to databases (global and local)
  public void openDbConnection() throws SQLException {

        java.util.Date now = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        String[] temp = fmt.format(now).split("-");
        localConnection = DriverManager.getConnection("jdbc:sqlite:db/cj-" + temp[0]
                + ".db");
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
          for (String line: sql) {
              stmt.addBatch(line);
          }
          stmt.executeBatch();
      } catch (SQLException sqex) {
          sqex.printStackTrace();
      } finally {
          stmt.close();
      }

  }

    // setters
    public void setMaterials(Map<Integer, String> materials) {
        this.materials = materials;
    }

    public void setJournalList(List<CashJournal> journalList) {
        this.journalList = journalList;
    }

    public void setHeads(List<DeliveryHead> heads) {
        this.heads = heads;
    }

    public void setItems(List<DeliveryItem> items) {
        this.items = items;
    }

    // Upload master data
    public void uploadMasterData() {

        PreparedStatement pstmt = null;
        StringBuilder sb = new StringBuilder(0);
        sb.append("INSERT INTO materials VALUES (?, ?)");

        try {
            pstmt = localConnection.prepareStatement(sb.toString());
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        }

        Iterator<Map.Entry<Integer, String>> it = materials.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Integer, String> entry = it.next();
            try {
                pstmt.setInt(1, entry.getKey());
                pstmt.setString(2, entry.getValue());
                pstmt.execute();
            } catch (SQLException sqlex) {
                continue;
            }
        }

        try {
            pstmt.close();
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        }

    }

    // PRIVATE SECTION FOR METHODS

    private String[] buildInitSql() {

        String[] commands = new String[4];
        StringBuilder sb = new StringBuilder(0);

        sb.append("CREATE TABLE IF NOT EXISTS materials").
        append(" (id INTEGER PRIMARY KEY, description VARCHAR(50))");
        commands[0] = sb.toString();
        sb.delete(0, sb.length());

        sb.append("CREATE TABLE IF NOT EXISTS cash_journal");
        sb.append(" (id INTEGER PRIMARY KEY AUTOINCREMENT, cajo_number VARCHAR(4),");
        sb.append(" company_code VARCHAR(4), year INTEGER, posting_number INTEGER,");
        sb.append(" amount_receipt DECIMAL(20,2), amount_payments DECIMAL(20,2),");
        sb.append(" net_amount DECIMAL(20,2), partner_name VARCHAR(35),");
        sb.append(" doc_date DATE, post_date DATE, doc_number VARCHAR(30),");
        sb.append(" pos_text VARCHAR(50), delivery_id INTEGER)");
        commands[1] = sb.toString();
        sb.delete(0, sb.length());

        sb.append("CREATE TABLE IF NOT EXISTS delivery_head");
        sb.append(" (vbeln INTEGER PRIMARY KEY, user VARCHAR(12),");
        sb.append(" delivery_date DATE, customer VARCHAR(10))");
        commands[2] = sb.toString();
        sb.delete(0, sb.length());

        sb.append("CREATE TABLE IF NOT EXISTS delivery_item");
        sb.append(" (vbeln INTEGER NOT NULL, posnr INTEGER NOT NULL,");
        sb.append(" matnr INTEGER, quantity DECIMAL(20,3), uom VARCHAR(3), mat_text VARCHAR(40),");
        sb.append(" PRIMARY KEY (vbeln, posnr))");
        commands[3] = sb.toString();
        sb.delete(0, sb.length());

        return commands;
    }

}
