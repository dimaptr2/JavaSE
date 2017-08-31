package ru.velkomfood.mysap.service.agent;

public class SAPManager {

    private static SAPManager instance;

    private SAPManager() { }

    public static SAPManager getInstance() {
        if (instance == null) {
            instance = new SAPManager();
        }
        return instance;
    }

}
