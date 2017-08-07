package ru.velkomfood.mysap.data.controller;


import java.util.TimerTask;

public class TaskHandler extends TimerTask {

    private static TaskHandler instance;
    private SapReader sapReader;

    private TaskHandler() { }

    public static TaskHandler getInstance() {
        if (instance == null) {
            instance = new TaskHandler();
        }
        return instance;
    }

    public void setSapReader(SapReader sapReader) {
        this.sapReader = sapReader;
    }

    @Override
    public void run() {
        sapReader.run();
    }

}
