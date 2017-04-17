package ru.velkomfood.mysap.info;

import ru.velkomfood.mysap.info.controller.DBWriter;
import ru.velkomfood.mysap.info.controller.PropertyReader;
import ru.velkomfood.mysap.info.controller.SAPReader;

import java.util.Timer;

/**
 * Created by D. Petrov on 17.04.2017.
 * Start Scheduling service, that can read data from SAP components
 * and can transfer its to MySQL.
 */

public class Launcher {

    public static void main(String[] args) throws Exception {

        IntegrationTask integrationTask = new IntegrationTask();
        PropertyReader propertyReader = PropertyReader.getInstance("settings/config.properties");
        propertyReader.readPropertiesFile();

        // SAP service
        SAPReader reader = SAPReader.getInstance();
        reader.setPropertyReader(propertyReader);
        reader.openSAPConnection();
        integrationTask.setDataReader(reader);

        // Database service
        DBWriter writer = DBWriter.getInstance();
        writer.setPropertyReader(propertyReader);
        integrationTask.setDataWriter(writer);

        long interval = 1000 * 3600; // 60 minutes in milliseconds

        System.out.println("Start SAP Integration Server");
        Timer timer = new Timer();
        // Run integration task every 60 minutes without delay
        timer.schedule(integrationTask, 0, interval);

    }

}
