package ru.velkomfood.mysap.data.controller;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import ru.velkomfood.mysap.data.model.master.Company;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Queue;

public class SapReader {

    private static SapReader instance;
    private DbWriterMaster dbWriterMaster;
    private LocalDate currentDate;
    final static String DEST = "";
    final static String SUFFIX = ".jcoDestination";

    private Properties connProperties;
    private JCoDestination destination;

    private SapReader(DbWriterMaster dbWriterMaster) {
        this.dbWriterMaster = dbWriterMaster;
        connProperties = new Properties();
        connProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "");
        connProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "");
        connProperties.setProperty(DestinationDataProvider.JCO_R3NAME, "");
        connProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "");
        connProperties.setProperty(DestinationDataProvider.JCO_USER, "");
        connProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "");
        connProperties.setProperty(DestinationDataProvider.JCO_LANG, "");
        createDestinationDataFile(DEST, connProperties);
    }

    private void createDestinationDataFile(String destName, Properties props) {

        File destCfg = new File(destName + SUFFIX);

        if (destCfg.isDirectory() || !destCfg.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(destCfg, false);
                props.store(fos, "Productive environment");
                fos.close();
            } catch (IOException fe) {
                fe.printStackTrace();
            }
        }

    }

    public static SapReader getInstance(DbWriterMaster dbWriterMaster) {
        if (instance == null) {
            instance = new SapReader(dbWriterMaster);
        }
        return instance;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public void initSAPDestination() throws JCoException {

        destination = JCoDestinationManager.getDestination(DEST);

    }

    public void run() {

        System.out.println("Start the job!");

        long m1 = new Date().getTime();

        try {

            try {

                JCoContext.begin(destination);

                dbWriterMaster.openConnection();

                if (dbWriterMaster.isFirstStart()) {
                    dbWriterMaster.initDatabase();
                }

                readCompany();

                dbWriterMaster.closeConnection();

            } finally {

                JCoContext.end(destination);

            }

        } catch (JCoException | SQLException | IOException ex) {

            ex.printStackTrace();

        }

        long m2 = new Date().getTime();
        showMessageAboutExecutionTime(m1, m2);
        System.out.println("Finish the job!");

    }

    private void readCompany() throws JCoException, SQLException {


    }

    private void showMessageAboutExecutionTime(long start, long finish) {

        long delta = (finish - start) / 1000; // delta in seconds

        String timeUnit = "sec";

        if (delta > 60) {
            timeUnit = "min";
            delta /= 60;
            if (delta > 60) {
                timeUnit = "hours";
                delta /= 60;
            }
        }

        System.out.printf("Time of execution is %d %s\n", delta, timeUnit);

    }

}
