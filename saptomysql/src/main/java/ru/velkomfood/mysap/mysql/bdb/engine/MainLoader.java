package ru.velkomfood.mysap.mysql.bdb.engine;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import ru.velkomfood.mysap.mysql.bdb.entities.MrpReportEntity;

import javax.sql.DataSource;
import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by dpetrov on 10.08.16.
 */

public class MainLoader {

    private static MainLoader instance;
    private String nameEnvironment;
    private EnvironmentConfig environmentConfig;
    private Environment environment;
    private StoreConfig storeConfig;
    private EntityStore store;
    private DataSource dataSource;
    private long numberObjects;

    private MainLoader(String nameEnvironment) {
        this.nameEnvironment = nameEnvironment;
        numberObjects = 0;
    }

    public static MainLoader getInstance(String nameEnvironment) {

        if (instance == null) instance = new MainLoader(nameEnvironment);

        return instance;
    }

    public void initEnvironment() throws DatabaseException {

        environmentConfig = new EnvironmentConfig();
        storeConfig = new StoreConfig();

        environmentConfig.setAllowCreate(true);
        storeConfig.setAllowCreate(true);

        environment = new Environment(new File(nameEnvironment), environmentConfig);

        store = new EntityStore(environment, "bdbmrp", storeConfig);

    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long getNumberObjects() {
        return numberObjects;
    }

    // Data processing ...
    public void run() throws SQLException {

        String sql = "SELECT DISTINCT matnr, werks, year FROM sectotals ORDER BY matnr, werks, year";

        Statement statement = dataSource.getConnection().createStatement();

        ResultSet resultSet = executeMyQuery(sql, statement);

        while (resultSet.next()) {

            MrpReportEntity mrpe = new MrpReportEntity();
            ReportDA da = new ReportDA(store);

            String key = resultSet.getString("matnr") + "-" +
                    resultSet.getInt("werks") + "-" + resultSet.getInt("year");
            mrpe.setId(key);
            mrpe.setMaterial(resultSet.getString("matnr"));
            mrpe.setPlant(resultSet.getInt("werks"));
            mrpe.setYear(resultSet.getInt("year"));
            da.pIdx.put(mrpe);

        }
        resultSet.close();

        sql = "SELECT * FROM requirements ORDER BY matnr, werks, year, month";

        resultSet = executeMyQuery(sql, statement);

        while (resultSet.next()) {

            MrpReportEntity mrpe = new MrpReportEntity();
            ReportDA da = new ReportDA(store);
            String key = resultSet.getString("matnr") + "-" +
                    resultSet.getInt("werks") + "-" + resultSet.getInt("year");
            mrpe.setId(key);
            mrpe.setMaterial(resultSet.getString("matnr"));
            mrpe.setMaterialDescription(resultSet.getString("maktx"));
            mrpe.setPlant(resultSet.getInt("werks"));
            mrpe.setYear(resultSet.getInt("year"));
            mrpe.setPurchaseGroup(resultSet.getString("pur_group"));
            mrpe.setNameOfPurchaseGroup(resultSet.getString("description"));
            mrpe.setBaseUnit(resultSet.getString("base_unit"));

            mrpe.setStock(resultSet.getBigDecimal("plantstk"));

            BigDecimal number = new BigDecimal("0.000");

            if (resultSet.getBigDecimal("quantity") != null) {
                number = resultSet.getBigDecimal("quantity");
            }

            switch (resultSet.getInt("month")) {
                case 1:
                    mrpe.setReq01(number);
                    break;
                case 2:
                    mrpe.setReq02(number);
                    break;
                case 3:
                    mrpe.setReq03(number);
                    break;
                case 4:
                    mrpe.setReq04(number);
                    break;
                case 5:
                    mrpe.setReq05(number);
                    break;
                case 6:
                    mrpe.setReq06(number);
                    break;
                case 7:
                    mrpe.setReq07(number);
                    break;
                case 8:
                    mrpe.setReq08(number);
                    break;
                case 9:
                    mrpe.setReq09(number);
                    break;
                case 10:
                    mrpe.setReq10(number);
                    break;
                case 11:
                    mrpe.setReq11(number);
                    break;
                case 12:
                    mrpe.setReq12(number);
                    break;
            }

            da.pIdx.put(mrpe);
            numberObjects++;
        }
        resultSet.close();

        statement.close();

    } // end of run() method

    public void closeEnvironment() {

        if (store != null) store.close();
        if (environment != null) environment.close();

    }

    private ResultSet executeMyQuery(String command, Statement stmt) throws SQLException {
        return stmt.executeQuery(command);
    }

}
