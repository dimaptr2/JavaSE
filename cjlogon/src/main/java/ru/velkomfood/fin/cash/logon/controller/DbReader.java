package ru.velkomfood.fin.cash.logon.controller;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import ru.velkomfood.fin.cash.logon.model.CashDocument;
import ru.velkomfood.fin.cash.logon.model.FiscalHead;
import ru.velkomfood.fin.cash.logon.model.FiscalItem;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DbReader {

    private static DbReader instance;
    private DataSource dataSource;
    private Connection connection;

    private DbReader() {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("");
        ds.setPort(3306);
        ds.setDatabaseName("");
        ds.setUser("");
        ds.setPassword("");
        dataSource = ds;
    }

    public static DbReader getInstance() {
        if (instance == null) {
            instance = new DbReader();
        }
        return instance;
    }

    // Open and close the connection to the database
    public void openConnection() throws SQLException {
        connection = dataSource.getConnection();
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public boolean isOpened() throws SQLException {
        if (connection == null || connection.isClosed()) {
            return false;
        } else {
            return true;
        }
    }

    public List<CashDocument> readCashDocumentsByDate(String atDate) throws SQLException {

        List<CashDocument> docs = new ArrayList<>();
        Statement stmt = connection.createStatement();

        try {

            StringBuilder sb = new StringBuilder(0);
            sb.append("SELECT * FROM cash_journal AS cj WHERE cj.posting_date = ")
                    .append("\'")
                    .append(atDate)
                    .append("\'")
                    .append(" AND NOT EXISTS ( SELECT * FROM completed AS cm WHERE cm.id = cj.id )")
                    .append(" ORDER BY cj.id");
            ResultSet rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                CashDocument doc = new CashDocument(
                        rs.getLong("id"), rs.getString("cajo_number"),
                        rs.getString("company_id"), rs.getInt("year"),
                        LocalDate.parse(rs.getString("posting_date")),
                        rs.getString("position_text"),
                        rs.getLong("delivery_id"), rs.getBigDecimal("amount")
                );
                docs.add(doc);
            }
            rs.close();

        } finally {

            stmt.close();

        }

        return docs;
    }

    public CashDocument readCashDocumentById(long id) throws SQLException {

        CashDocument cashDocument = new CashDocument();
        Statement stmt = connection.createStatement();

        try {

            StringBuilder sb = new StringBuilder(0);
            sb.append("SELECT * FROM cash_journal")
                    .append(" WHERE id = ")
                    .append("\'")
                    .append(id)
                    .append("\'");
            ResultSet rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                cashDocument.setId(rs.getLong("id"));
                cashDocument.setAmount(rs.getBigDecimal("amount"));
                cashDocument.setCajoNumber(rs.getString("cajo_number"));
                cashDocument.setCompanyId(rs.getString("company_id"));
                cashDocument.setDeliveryId(rs.getLong("delivery_id"));
                cashDocument.setPositionText(rs.getString("position_text"));
                cashDocument.setPostingDate(LocalDate.parse(rs.getString("posting_date")));
                cashDocument.setYear(rs.getInt("year"));
            }
            rs.close();

        } finally {

            stmt.close();

        }

        return cashDocument;
    }

    public List<FiscalItem> buildFiscalDocumentByDeliveryId(long delivery) throws SQLException {

        List<FiscalItem> documents = new ArrayList<>();
        Statement stmt = connection.createStatement();

        try {

            StringBuilder sb = new StringBuilder(0);
            sb.append("SELECT id, position, material_id,")
                    .append(" description, net_price, price, quantity, vat, vat_rate")
                    .append(" FROM delivery_item")
                    .append(" WHERE id = ")
                    .append(delivery)
                    .append(" ORDER BY id, position");
            int index = 0;
            ResultSet rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                index++;
                FiscalItem item = new FiscalItem(
                  index, rs.getLong("material_id"),
                  rs.getString("description"), rs.getBigDecimal("quantity"),
                  rs.getBigDecimal("price"), rs.getInt("vat_rate")
                );
                documents.add(item);
            }
            rs.close();

        } finally {

            stmt.close();

        }

        return documents;
    }

    public FiscalHead readFiscalGeneralDataByDeliveryId(long delivery) throws SQLException {

        FiscalHead head = new FiscalHead();
        Statement stmt = connection.createStatement();

        try {

            StringBuilder sb = new StringBuilder(0);
            sb.append("SELECT id, company_id, posting_date, amount")
                    .append(" FROM delivery_head")
                    .append(" WHERE id = ")
                    .append(delivery);
            ResultSet rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                head.setId(rs.getLong("id"));
                head.setCompanyId(rs.getString("company_id"));
                head.setPostingDate(LocalDate.parse(rs.getString("posting_date")));
                head.setAmount(rs.getBigDecimal("amount"));
            }
            rs.close();

        } finally {

            stmt.close();

        }

        return head;
    }

    public boolean isCompleted(CashDocument doc) throws SQLException {

        boolean existence = false;

        StringBuilder sb = new StringBuilder(0);
        sb.append("SELECT id FROM completed WHERE id = ")
                .append(doc.getId());

        Statement stmt = connection.createStatement();

        try {

            ResultSet rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                long key = rs.getLong("id");
                if (key != 0) {
                    existence = true;
                }
            }
            rs.close();

        } finally {

            stmt.close();

        }

        return existence;
    }

    public int updateCashDocumentStatus(CashDocument cashDocument) throws SQLException {

        final String SUCCESS = "Posted";
        int dbCounter = 0;

        StringBuilder sb = new StringBuilder(0);
        sb.append("INSERT INTO completed VALUES")
                .append(" (?, ?)");

        PreparedStatement pstmt = connection.prepareStatement(sb.toString());

        try {

            pstmt.setLong(1, cashDocument.getId());
            pstmt.setString(2, SUCCESS);
            dbCounter = pstmt.executeUpdate();

        } finally {

            pstmt.close();

        }

        return dbCounter;

    } // update a status

    public void recoverStatusByDocId(long id) throws SQLException {

        StringBuilder sb = new StringBuilder(0);
        sb.append("DELETE FROM completed WHERE id = ")
                .append(id);

        PreparedStatement pstmt = connection.prepareStatement(sb.toString());

        try {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }

    }

}
