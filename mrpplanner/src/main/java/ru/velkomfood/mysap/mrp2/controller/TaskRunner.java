package ru.velkomfood.mysap.mrp2.controller;

import java.util.Timer;

public class TaskRunner {

    private final long INTERVAL = 86400000;
    private static TaskRunner instance;
    private Timer timer;
    private Handler handler;

    private TaskRunner() {
        handler = new Handler();
        timer = new Timer();
    }

    public static TaskRunner getInstance() {
        if (instance == null) {
            instance = new TaskRunner();
        }
        return instance;
    }

    public void start() {
        timer.scheduleAtFixedRate(handler, 1000, INTERVAL);
    }

}
