package ru.velkomfood.mysap.copa.download.controller;

import java.util.Properties;

/**
 * Created by DPetrov on 30.08.2016.
 */
public class ErpConnector {

    String destinationName;
    private static ErpConnector instance;

    private ErpConnector(String destinationName) {
        this.destinationName = destinationName;
    }

    public static ErpConnector getInstance(String destinationName) {

        if (instance == null) instance = new ErpConnector(destinationName);
        return instance;

    }

    static {
        Properties connectProperties = new Properties();
    }
}
