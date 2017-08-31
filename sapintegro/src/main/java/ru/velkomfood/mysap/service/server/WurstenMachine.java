package ru.velkomfood.mysap.service.server;

import ru.velkomfood.mysap.service.agent.Binder;

import javax.xml.ws.Endpoint;

public class WurstenMachine {

    private int port;
    private final String URL = "http://do0058.eatmeat.ru:1212/vproxy";

    public WurstenMachine(int port) {
        this.port = port;
    }

    public void start() {

        Endpoint.publish(URL, new Binder());
        String message = String.format("Service Wursten Machine started on the port %d\n", port);
        System.out.append(message);

    }

}
