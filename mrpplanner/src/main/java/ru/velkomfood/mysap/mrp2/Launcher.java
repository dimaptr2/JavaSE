package ru.velkomfood.mysap.mrp2;

import ru.velkomfood.mysap.mrp2.controller.TaskRunner;

public class Launcher {

    public static void main(String[] args) {

        System.out.println("Start uploading service for MRP2 planning");

        TaskRunner taskRunner = TaskRunner.getInstance();
        taskRunner.start();

    }

}
