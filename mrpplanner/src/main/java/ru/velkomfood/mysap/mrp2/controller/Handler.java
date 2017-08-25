package ru.velkomfood.mysap.mrp2.controller;

import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Handler extends TimerTask {

    @Override
    public void run() {
        System.out.println("Upload data");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(new DataLoader("MasterData"));
        executor.submit(new DataLoader("TransactionData"));
    }

}
