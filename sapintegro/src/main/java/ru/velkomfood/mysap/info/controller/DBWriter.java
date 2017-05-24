package ru.velkomfood.mysap.info.controller;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by dpetrov on 17.04.17.
 */
public class DBWriter {

    private static DBWriter instance;
    private MysqlDataSource mysql;
    private DataSource dataSource;
    private PropertyReader propertyReader;
    private Connection connection;

    private DBWriter() {
        mysql = new MysqlDataSource();
    }

    public static DBWriter getInstance() {
        if (instance == null) {
            instance = new DBWriter();
        }
        return instance;
    }

    public void setPropertyReader(PropertyReader propertyReader) {
        this.propertyReader = propertyReader;
    }

    // Open and close a database connection

    public void openDatabaseConnection() throws IOException, SQLException {

        mysql.setServerName(propertyReader.getProperties().getProperty("dbserver.name"));
        mysql.setDatabaseName(propertyReader.getProperties().getProperty("database.name"));
        mysql.setPort(Integer.parseInt(propertyReader.getProperties().getProperty("port")));
        mysql.setUser(propertyReader.getProperties().getProperty("user"));
        mysql.setPassword(propertyReader.getProperties().getProperty("password"));
        dataSource = mysql;
        connection = dataSource.getConnection();

    }

    public void closeDatabaseConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

}
