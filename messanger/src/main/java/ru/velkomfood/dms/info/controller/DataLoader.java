package ru.velkomfood.dms.info.controller;

import java.util.TimerTask;

public class DataLoader extends TimerTask {

    private String name;
    private boolean isFirstStep;

    public DataLoader(String name) {
        this.name = name;
        isFirstStep = true;
    }

    @Override
    public void run() {

        Thread t = new DataProcessor(name, isFirstStep);
        t.start();

        if (isFirstStep) {
            isFirstStep = false;
        }

    }

}
