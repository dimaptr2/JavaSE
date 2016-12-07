package ru.velkomfood.mysap.synchronizer.controller;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import ru.velkomfood.mysap.synchronizer.model.Material;
import ru.velkomfood.mysap.synchronizer.model.MaterialValuation;
import ru.velkomfood.mysap.synchronizer.model.Unit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Queue;

/**
 * Created by dpetrov on 05.12.16.
 */
public class MySqlWriter {

    private Queue<Unit> units;
    private Queue<Material> materials;
    private Queue<MaterialValuation> valuations;

    private MysqlDataSource dataSource;
    private Connection dbConnection;

    public MySqlWriter() {
        dataSource = new MysqlDataSource();
        dataSource.setServerName("localhost");
        dataSource.setPort(3306);
        dataSource.setDatabaseName("XXX");
        dataSource.setUser("XXX");
        dataSource.setPassword("XXX");
    }

    // setters and getters


    public void setUnits(Queue<Unit> units) {
        this.units = units;
    }

    public void setMaterials(Queue<Material> materials) {
        this.materials = materials;
    }

    public void setValuations(Queue<MaterialValuation> valuations) {
        this.valuations = valuations;
    }

    public void openDbConnection() throws SQLException {
        dbConnection = dataSource.getConnection();
    }

    public void refreshDatabase() throws SQLException {

        Statement stmt = dbConnection.createStatement();

        try {
            stmt.executeUpdate("DELETE FROM units");
            stmt.executeUpdate("DELETE FROM mara");
            stmt.executeUpdate("DELETE FROM mbew");
        } finally {
            stmt.close();
        }

    }

    // Create units of measurements
    public void createUoms() throws SQLException {

        PreparedStatement pstmt = dbConnection.prepareStatement("INSERT INTO units VALUES (?, ?, ?)");

        try {
           Iterator<Unit> it = units.iterator();
           while (it.hasNext()) {
               Unit u = it.next();
               pstmt.setString(1, u.getUomSAP());
               pstmt.setString(2, u.getUomISO());
               pstmt.setString(3, u.getDescription());
               pstmt.addBatch();
           }
           pstmt.executeBatch();
        } finally {
            pstmt.close();
        }

    }

    // Create material master data
    public void createMaterials() throws SQLException {

        PreparedStatement pstmt = dbConnection.prepareStatement("INSERT INTO mara VALUES (?, ?)");

        try {
            Iterator<Material> it = materials.iterator();
            while (it.hasNext()) {
                Material material = it.next();
                pstmt.setInt(1, material.getMatnr());
                pstmt.setString(2, material.getDescription());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } finally {
            pstmt.close();
        }

    }

    public void createValuations() throws SQLException {

        PreparedStatement pstmt = dbConnection.prepareStatement("INSERT INTO mbew VALUES (?, ?, ?, ?, ?, ?, ?)");

        try {
            Iterator<MaterialValuation> it = valuations.iterator();
            while (it.hasNext()) {
                MaterialValuation mv = it.next();
                pstmt.setInt(1, mv.getMatnr());
                pstmt.setInt(2, mv.getPlant());
                pstmt.setString(3, mv.getBaseUom());
                pstmt.setString(4, mv.getPriceControl());
                pstmt.setBigDecimal(5, mv.getFixedPrice());
                pstmt.setBigDecimal(6, mv.getWeightedPrice());
                pstmt.setBigDecimal(7, mv.getPriceUnit());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } finally {
            pstmt.close();
        }
    }

    public void closeDbConnection() throws SQLException {
        if (dbConnection != null && !dbConnection.isClosed())
            dbConnection.close();
    }

}
