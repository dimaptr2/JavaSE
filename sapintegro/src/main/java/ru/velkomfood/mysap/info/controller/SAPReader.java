package ru.velkomfood.mysap.info.controller;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.ext.DestinationDataProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by dpetrov on 17.04.17.
 */
public class SAPReader {

    private static SAPReader instance;
    String DEST_NAME = "PRD500R11";
    final String SUFFIX = ".jcoDestination";
    String destFileName;
    private PropertyReader propertyReader;
    private Properties sapProps;
    private JCoDestination destination;

    private SAPReader() {
        destFileName = DEST_NAME + SUFFIX;
        sapProps = new Properties();
    }

    public static SAPReader getInstance() {
        if (instance == null) {
            instance = new SAPReader();
        }
        return instance;
    }

    public void setPropertyReader(PropertyReader propertyReader) {
        this.propertyReader = propertyReader;
    }

    public void openSAPConnection() throws JCoException {

        sapProps.setProperty(DestinationDataProvider.JCO_ASHOST,
                propertyReader.getProperties().getProperty("ashost"));
        sapProps.setProperty(DestinationDataProvider.JCO_SYSNR,
                propertyReader.getProperties().getProperty("sysnr"));
        sapProps.setProperty(DestinationDataProvider.JCO_R3NAME,
                propertyReader.getProperties().getProperty("sysid"));
        sapProps.setProperty(DestinationDataProvider.JCO_CLIENT,
                propertyReader.getProperties().getProperty("client"));
        sapProps.setProperty(DestinationDataProvider.JCO_USER,
                propertyReader.getProperties().getProperty("sapuser"));
        sapProps.setProperty(DestinationDataProvider.JCO_PASSWD,
                propertyReader.getProperties().getProperty("sappassword"));
        sapProps.setProperty(DestinationDataProvider.JCO_LANG,
                propertyReader.getProperties().getProperty("saplang"));

        createDestinationDataFile(destFileName, sapProps);
        destination = JCoDestinationManager.getDestination(DEST_NAME);

    }

    // Create a destination data file
    private void createDestinationDataFile(String fileName, Properties props) {

        File cfgDest = new File(fileName);

        try {
            FileOutputStream fos = new FileOutputStream(cfgDest, false);
            props.store(fos, "Production server");
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create a destination file", e);
        }

    } // create a file

    // Main job


    public JCoDestination getDestination() {
        return destination;
    }

    // get UOMs
    public void getUnitsOfMeasurements(DBWriter dbw) throws JCoException {

        JCoFunction rfcUom = destination.getRepository().getFunction("Z_RFC_GET_UNITS_MEASURE");
        if (rfcUom == null) {
            throw new RuntimeException("Function Z_RFC_GET_UNITS_MEASURE not found");
        }

        rfcUom.getImportParameterList().setValue("LANGUAGE",
                propertyReader.getProperties().getProperty("saplang"));
        rfcUom.execute(destination);

    }

}
