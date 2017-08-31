package ru.velkomfood.mysap.service;

import ru.velkomfood.mysap.service.server.WurstenMachine;

public class Launcher {

    public static void main(String[] args) throws Exception {

        // Create the SOAP service instance
        WurstenMachine machine = new WurstenMachine(1212);
        machine.start();

    }

}
