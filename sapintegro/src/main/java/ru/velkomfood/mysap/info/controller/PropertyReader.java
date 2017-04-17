package ru.velkomfood.mysap.info.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by dpetrov on 17.04.17.
 */
public class PropertyReader {

    private static PropertyReader instance;
    private String propFileName;
    private Properties properties;

    private PropertyReader(String propFileName) {
        this.propFileName = propFileName;
        properties = new Properties();
    }

    public static PropertyReader getInstance(String propFileName) {
        if (instance == null) {
            instance = new PropertyReader(propFileName);
        }
        return instance;
    }

    public void readPropertiesFile() {

        InputStream input = null;

        try {
            input = new FileInputStream(propFileName);
            properties.load(input);
        } catch (IOException inex) {
            inex.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException ine) {
                ine.printStackTrace();
            }
        }

    }

    public Properties getProperties() {
        return properties;
    }

}
