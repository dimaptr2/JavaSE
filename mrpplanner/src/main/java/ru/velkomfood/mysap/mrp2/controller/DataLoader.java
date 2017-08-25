package ru.velkomfood.mysap.mrp2.controller;

public class DataLoader implements Runnable {

    private String threadName;

    public DataLoader(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void run() {
        System.out.printf("Thread called %s\n", threadName);
        switch (threadName) {
            case "MasterData":
                break;
            case "TransactionData":
                break;
        }
    }

}
