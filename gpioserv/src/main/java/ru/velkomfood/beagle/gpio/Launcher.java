package ru.velkomfood.beagle.gpio;

public class Launcher {

    public static void main(String[] args) throws Exception {

        System.out.println("Start GPIO watcher");
        GPIOEngine gpioEngine = GPIOEngine.getInstance();
        gpioEngine.initGPIO();
        Thread th1 = new Thread(gpioEngine, "listen");
        th1.start();

    }

}
