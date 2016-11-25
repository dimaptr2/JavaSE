package ru.velkomfood.mrp.client.config;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import ru.velkomfood.mrp.client.model.MaterialCache;
import javax.sql.DataSource;

/**
 * Created by dpetrov on 12.08.16.
 */
public class ConfigGenerator {

    private static ConfigGenerator instance;

    private ConfigGenerator() { }

    public static ConfigGenerator getInstance() {
        if (instance == null) instance = new ConfigGenerator();
        return instance;
    }

    public DataSource getDataSource(String userName, String password) {

        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("srv-sapapp.eatmeat.ru");
        ds.setPort(3306);
        ds.setDatabaseName("sapdata");
        ds.setUser(userName);
        ds.setPassword(password);

        return (DataSource) ds;
    }

    public MaterialCache materialCache() {
        return MaterialCache.getInstance();
    }

}
