package ru.velkomfood.mysap.data.controller;


import java.util.TimerTask;

public class TaskHandler extends TimerTask {

    private static TaskHandler instance;
    private TaskHandler() { }

    public static TaskHandler getInstance() {
        if (instance == null) {
            instance = new TaskHandler();
        }
        return instance;
    }


    @Override
    public void run() {

    }

}
