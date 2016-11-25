package ru.velkomfood.mysap.mysql;

import ru.velkomfood.mysap.mysql.bdb.engine.MainLoader;
import ru.velkomfood.mysap.mysql.controller.MySqlConnector;
import ru.velkomfood.mysap.mysql.controller.SapConnector;
import ru.velkomfood.mysap.mysql.model.*;

import java.util.Date;
import java.util.Queue;

/**
 * Created by dpetrov on 12.07.16.
 */

public class Launcher {

    public static void main(String[] args) throws Exception {

        Date start = new Date();

        SapConnector sapconn = SapConnector.getInstance("sap-01");
        sapconn.run();

        MySqlConnector mysql = MySqlConnector.getInstance("mysql-02");
        mysql.setSapConnector(sapconn);
        mysql.setCustomers(sapconn.getCustomers());
        mysql.setVendors(sapconn.getVendors());
        mysql.setMaterials(sapconn.getMaterials());
        mysql.setMaterialPrices(sapconn.getMaterialPrices());
        mysql.setStocks(sapconn.getStocks());
        mysql.setMrpitems(sapconn.getMrpitems());
        mysql.setLogisticInfoRecords(sapconn.getLogisticInfoRecords());
        mysql.run();

        Queue<VendorEntity> vendors = mysql.getVendors();
        Queue<CustomerEntity> customers = mysql.getCustomers();
        Queue<MaterialEntity> materials = mysql.getMaterials();
        Queue<MaterialPriceEntity> materialPrices = mysql.getMaterialPrices();
        Queue<StocksEntity> stocks = mysql.getStocks();
        Queue<MrpItemsEntity> mrpItems = mysql.getMrpitems();
        Queue<LogisticInfoRecord> infoRecords = mysql.getLogisticInfoRecords();

        MainLoader bdbLoader = MainLoader.getInstance("./bdbmrp");
        bdbLoader.initEnvironment();
        bdbLoader.setDataSource(mysql.getDataSource());
        bdbLoader.run();

        System.out.println("Vendors are: " + vendors.size());
        System.out.println("Customers are: " + customers.size());
        System.out.println("Materials are: " + materials.size());
        System.out.println("Material prices are: " + materialPrices.size());
        System.out.println("Stocks are: " + stocks.size());
        System.out.println("MRP items are: " + mrpItems.size());
        System.out.println("Logistic info-records are: " + infoRecords.size());
        System.out.println("Report mini-database is filled");
        System.out.printf("Number of processed objects is: %d", bdbLoader.getNumberObjects());
        System.out.println();

        bdbLoader.closeEnvironment();

        Date finish = new Date();

        long interval = ( finish.getTime() - start.getTime() ) / 60000; // in minutes

        System.out.printf("Time of execution is %d minutes", interval);
        System.out.println();

    }

}
