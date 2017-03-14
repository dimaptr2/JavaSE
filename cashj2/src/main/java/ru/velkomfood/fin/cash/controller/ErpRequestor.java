package ru.velkomfood.fin.cash.controller;

/**
 * Created by dpetrov on 10.03.2017.
 */
public class ErpRequestor {

    private static ErpRequestor instance;
    private ErpRequestor() { }

    public static ErpRequestor getInstance() {
        if(instance == null) {
            instance = new ErpRequestor();
        }
        return instance;
    }

}
