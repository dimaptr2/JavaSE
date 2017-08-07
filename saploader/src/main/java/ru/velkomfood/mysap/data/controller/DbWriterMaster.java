package ru.velkomfood.mysap.data.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.velkomfood.mysap.data.model.master.*;

import java.io.IOException;
import java.sql.SQLException;

public class DbWriterMaster {

    private static DbWriterMaster instance;
    private boolean firstStart;
    private final String DB_URL = "jdbc:mysql://localhost:3306/sapcache?user=saper&password=12345678";
    private JdbcConnectionSource connectionSource;

    // Data structures
    private Dao<Channel, String> channelDAO;
    private Dao<Sector, String> sectorDAO;
    private Dao<Company, String> companyDAO;
    private Dao<Material, Long> materialDAO;
    private Dao<DeliveryType, Integer> deliveryTypeDAO;
    private Dao<InvoiceType, Integer> invoiceTypeDAO;
    private Dao<Customer, String> customerDAO;
    private Dao<Vendor, String> vendorDAO;

    private DbWriterMaster() {
        firstStart = true;
    }

    public static DbWriterMaster getInstance() {
        if (instance == null) {
            instance = new DbWriterMaster();
        }
        return instance;
    }


    public boolean isFirstStart() {
        return firstStart;
    }

    public void openConnection() throws SQLException {
        connectionSource = new JdbcPooledConnectionSource(DB_URL);
    }

    public void closeConnection() throws SQLException, IOException {
        if (connectionSource != null) {
            connectionSource.close();
        }
    }


    public void initDatabase() throws SQLException {

        TableUtils.createTableIfNotExists(connectionSource, Channel.class);
        TableUtils.createTableIfNotExists(connectionSource, Company.class);
        TableUtils.createTableIfNotExists(connectionSource, Customer.class);
        TableUtils.createTableIfNotExists(connectionSource, DeliveryType.class);
        TableUtils.createTableIfNotExists(connectionSource, InvoiceType.class);
        TableUtils.createTableIfNotExists(connectionSource, Material.class);
        TableUtils.createTableIfNotExists(connectionSource, Sector.class);
        TableUtils.createTableIfNotExists(connectionSource, Vendor.class);

        channelDAO = DaoManager.createDao(connectionSource, Channel.class);
        sectorDAO = DaoManager.createDao(connectionSource, Sector.class);
        companyDAO = DaoManager.createDao(connectionSource, Company.class);
        materialDAO = DaoManager.createDao(connectionSource, Material.class);

        firstStart = false;

    }


}
