package ru.velkomfood.mysap.info;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoException;
import ru.velkomfood.mysap.info.controller.DBWriter;
import ru.velkomfood.mysap.info.controller.SAPReader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.TimerTask;

/**
 * Created by dpetrov on 17.04.17.
 */
public class IntegrationTask extends TimerTask {

    private SAPReader dataReader;
    private DBWriter dataWriter;

    // setters and getters

    public void setDataReader(SAPReader dataReader) {
        this.dataReader = dataReader;
    }

    public void setDataWriter(DBWriter dataWriter) {
        this.dataWriter = dataWriter;
    }

    // This is the main runnable method
    @Override
    public void run() {
        uploadData();
    } //run

    // Here is a main task for the data uploading
    private void uploadData() {

        long startTime = (new Date()).getTime() / 1000;
        System.out.println("Start the uploading ...");

        try {

            dataWriter.openDatabaseConnection();

            try {
                JCoContext.begin(dataReader.getDestination());
                dataReader.getUnitsOfMeasurements(dataWriter);
            } finally {
                JCoContext.end(dataReader.getDestination());
            }

            dataWriter.closeDatabaseConnection();

        } catch (IOException de) {
            de.printStackTrace();
        } catch (SQLException | JCoException e) {
            e.printStackTrace();
        }

        long finishTime = (new Date()).getTime() / 1000;
        long range = finishTime - startTime;

        if (range < 60) {
            System.out.printf("Time of execution is: %d seconds\n", range);
        } else {
            range = range / 60;
            if (range < 60) {
                System.out.printf("Time of execution is: %d minutes\n", range);
            } else {
                range = range / 60;
                System.out.printf("Time of execution is: %d hours\n", range);
            }
        }

    } // upload data

}
