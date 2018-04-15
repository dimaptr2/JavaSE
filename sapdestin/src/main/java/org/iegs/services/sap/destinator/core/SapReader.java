package org.iegs.services.sap.destinator.core;

/**
 *  This a main SAP Reader class.
 *  It contains basic algorithms, that makes the RFC destinations to the SAP servers.
 *  Parameters of the RFC destinations included into properties files, that called like as names of destinations.
 */

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class SapReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SapReader.class);

    private static final SapReader instance = new SapReader();

    private Properties[] propsArray;

    /**
     * Singleton creator
     * @return
     */
    public static SapReader createInstance() {
        return instance;
    }

    /**
     * Initialize the parameters
     * @throws IOException
     */
    public void initialize() throws IOException {

        Properties r11Properties = new Properties();
        Properties r14Properties = new Properties();
        Properties r15Properties = new Properties();
        propsArray = new Properties[3];

        InputStream is1 = getClass().getResourceAsStream("/r11.properties");
        InputStream is2 = getClass().getResourceAsStream("/r14.properties");
        InputStream is3 = getClass().getResourceAsStream("/r15.properties");

        try {
            r11Properties.load(is1);
            propsArray[0] = r11Properties;
            r14Properties.load(is2);
            propsArray[1] = r14Properties;
            r15Properties.load(is3);
            propsArray[2] = r15Properties;
        } finally {
            is1.close();
            is2.close();
            is3.close();
        }

    }

    /**
     * Basic business logic. Create the destinations to the SAP servers.
     * @throws JCoException
     * @throws IOException
     */
    public void makeDestinations() throws JCoException, IOException {

        for (Properties properties: propsArray) {
            createDestinationByPropertiesObject(properties);
        }

    }

    /**
     * Read the properties and use them for the destination creation.
     * @param props
     * @throws JCoException
     * @throws IOException
     */
    private void createDestinationByPropertiesObject(Properties props) throws JCoException, IOException {

        Properties sapConnection = new Properties();
        String destName = "", destFileName = "";

        for (Map.Entry<Object, Object> entry: props.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            switch (key) {
                case "destination":
                    destName = value;
                    break;
                case "dest_full_name":
                    destFileName = value;
                    break;
                case "sap_router":
                    sapConnection.setProperty(DestinationDataProvider.JCO_SAPROUTER, value);
                    break;
                case "ashost":
                    sapConnection.setProperty(DestinationDataProvider.JCO_ASHOST, value);
                    break;
                case "sysnr":
                    sapConnection.setProperty(DestinationDataProvider.JCO_SYSNR, value);
                    break;
                case "sysid":
                    sapConnection.setProperty(DestinationDataProvider.JCO_R3NAME, value);
                    break;
                case "client":
                    sapConnection.setProperty(DestinationDataProvider.JCO_CLIENT, value);
                    break;
                case "user":
                    sapConnection.setProperty(DestinationDataProvider.JCO_USER, value);
                    break;
                case "password":
                    sapConnection.setProperty(DestinationDataProvider.JCO_PASSWD, value);
                    break;
                case "language":
                    sapConnection.setProperty(DestinationDataProvider.JCO_LANG, value);
                    break;
            }
        }

        createDestinationDataFile(destFileName, sapConnection);

        JCoDestination destination = JCoDestinationManager.getDestination(destName);
        if (destination != null && destination.isValid()) {
            destination.ping();
            LOGGER.info(destination.getApplicationServerHost());
        }

    }

    /**
     * Create the temporal destination data file.
     * @param fileName
     * @param properties
     * @throws IOException
     */
    private void createDestinationDataFile(String fileName, Properties properties) throws IOException {

        File cfg = new File(fileName);
        if (!cfg.exists() || cfg.isDirectory()) {
            OutputStream fos = new FileOutputStream(cfg, false);
            try {
                properties.store(fos, "Productive environment");
            } finally {
                fos.close();
            }
        }

    }

}
