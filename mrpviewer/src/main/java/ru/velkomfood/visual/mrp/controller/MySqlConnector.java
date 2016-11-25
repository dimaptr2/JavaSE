package ru.velkomfood.visual.mrp.controller;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import ru.velkomfood.visual.mrp.model.entities.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dpetrov on 26.07.16.
 */

public class MySqlConnector {

    private static MySqlConnector instance;
    private DataSource dataSource;
    private Connection connection;
    private Statement statement;
    private int currentYear;

    private MySqlConnector() {

        MysqlDataSource ds = new MysqlDataSource();

        ds.setServerName("srv-sapapp.eatmeat.ru");
        ds.setPort(3306);
        ds.setDatabaseName("sapdata");
        ds.setUser("sapdata");
        ds.setPassword("123qweASD");

        dataSource = (DataSource) ds;



    }

    public static MySqlConnector getInstance() {

        if (instance == null) instance = new MySqlConnector();

        return instance;
    }

    public void initConnection() throws SQLException {

        connection = dataSource.getConnection();

        if (connection != null
                && !connection.isClosed()) statement = connection.createStatement();

    }

    public boolean connectionIsValid() throws SQLException {

        if (!connection.isClosed())
            return true;
        else
            return false;

    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public void closeResources() throws SQLException {

        if (statement != null) statement.close();
        if (connection != null) connection.close();

    }

    // Fetch purchase group
    public Map<String, PurchaseGroup> fetchPurchaseGroups() throws SQLException {

        Map<String, PurchaseGroup> groups = new ConcurrentHashMap<>();

        String SQL = "SELECT id, description FROM pur_groups ORDER BY id";

        ResultSet rs = statement.executeQuery(SQL);

        while (rs.next()) {
            PurchaseGroup pg = new PurchaseGroup();
            pg.setId(rs.getString("id"));
            pg.setDescription(rs.getString("decsription"));
            groups.put(pg.getId(), pg);
        }

        rs.close();

        return groups;

    }

    // Fetch materials
    public List<MaterialEntity> fetchMaterialsList(Map<String, String> params) throws SQLException {

        final String LOW = "000000000000000000";
        final String HIGH = "999999999999999999";

        List<MaterialEntity> materialList = new ArrayList<>();
        String sql = "SELECT matnr, maktx FROM mara";

        if (!params.isEmpty()) {
            if (!params.get("fromMatnr").equals("") && !params.get("toMatnr").equals("")) {
                sql = sql + " WHERE matnr BETWEEN (" + "\'" + params.get("fromMatnr") + "\'";
                sql = sql + " AND " + "\'" + params.get("toMatnr") + "\')";
            } else if (!params.get("fromMatnr").equals("") && params.get("toMatnr").equals("")) {
                sql = sql + " WHERE matnr = " + "\'" + params.get("fromMatnr") + "\'";
            } else {
                sql = sql + " WHERE matnr BETWEEN (" + "\'" + LOW + "\'";
                sql = sql + " AND " + "\'" + HIGH + "\')";
            }
        }

        sql = sql + " ORDER BY matnr";

        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            MaterialEntity me = new MaterialEntity();
            me.setMatnr(rs.getString("matnr"));
            me.setMaktx(rs.getString("maktx"));
            materialList.add(me);
        }

        rs.close();

        return materialList;

    }


}
