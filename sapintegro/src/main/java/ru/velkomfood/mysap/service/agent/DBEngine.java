package ru.velkomfood.mysap.service.agent;

public class DBEngine {

    private static DBEngine instance;

    private DBEngine() { }

    public static DBEngine getInstance() {
        if (instance == null) {
            instance = new DBEngine();
        }
        return instance;
    }

}
