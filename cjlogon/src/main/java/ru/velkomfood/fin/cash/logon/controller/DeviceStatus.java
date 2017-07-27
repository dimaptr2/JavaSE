package ru.velkomfood.fin.cash.logon.controller;


public class DeviceStatus {

    private static DeviceStatus instance;
    private int currentState;
    private String status;

    private DeviceStatus() { }

    public static DeviceStatus getInstance() {
        if (instance == null) {
            instance = new DeviceStatus();
        }
        return instance;
    }

    public static void setInstance(DeviceStatus instance) {
        DeviceStatus.instance = instance;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void doOkStatus() {
        if (currentState != 0) {
            currentState = 0;
            status = "Всё в порядке";
        }
    }

}
