package ru.velkomfood.fin.cash.controller;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by dpetrov on 10.03.2017.
 */
public class DbEngine {

    private static DbEngine instance;
    private DataSource dataSource;
    private Connection connection;

    private DbEngine() {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("srv-sapapp.eatmeat.ru");
        ds.setPort(3306);
        ds.setDatabaseName("beagle");
        ds.setUser("beagle");
        ds.setPassword("1qaz@WSX");
        this.dataSource = ds;
    }

    public static DbEngine getInstance() {
        if (instance == null) {
            instance = new DbEngine();
        }
        return instance;
    }

    // An opening and a closing connections to the database server
    public void openDbConnection() throws SQLException {
        connection = dataSource.getConnection();
    }

    public void closeDbConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Operations with the data
}
