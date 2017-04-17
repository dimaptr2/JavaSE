package ru.velkomfood.fin.cash;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.velkomfood.fin.cash.view.StartUI;
import ru.velkomfood.fin.cash.view.ViewItems;

/**
 * Created by dpetrov on 10.03.2017.
 */
public class Launcher {

    public static void main(String[] args) throws Exception {

        System.out.println("Start Cash Journal Service on the port 8083");

        StartUI frontend = new StartUI();
        ViewItems viewItems = new ViewItems();
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(frontend), "/cj");
        context.addServlet(new ServletHolder(viewItems), "/items");
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("public_html");
        resourceHandler.setRedirectWelcome(true);
        resourceHandler.setDirAllowed(false);
        resourceHandler.setWelcomeFiles(new String[] {"index.html"});

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, context});

        Server server = new Server(8083);
        server.setHandler(handlers);

        server.start();
        server.join();

    }

}
