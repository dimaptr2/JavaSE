package ru.velkomfood.visual.mrp;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.velkomfood.visual.mrp.config.CentralConfigurator;
import ru.velkomfood.visual.mrp.controller.MySqlConnector;
import ru.velkomfood.visual.mrp.view.Frontend;
import ru.velkomfood.visual.mrp.view.SearchHelper;

/**
 * Created by dpetrov on 28.07.16.
 */

public class Launcher {

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.out.append("Use a port as the first argument");
            System.exit(1);
        }

        String portString = args[0];
        int port = Integer.valueOf(portString);
        System.out.append("Starting at port: ").append(portString).append('\n');

        CentralConfigurator configurator = CentralConfigurator.getInstance();
        MySqlConnector connector = configurator.mySqlConnector();
        connector.initConnection();

        if (connector.connectionIsValid()) {

            Frontend frontend = configurator.frontend();
            frontend.setConnector(connector);
            frontend.setPageGenerator(configurator.pageGenerator());
            SearchHelper searchHelper = configurator.searchHelper();
            searchHelper.setConnector(connector);

            Server server = new Server(port);

            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.addServlet(new ServletHolder(frontend), "/mrp");
            context.addServlet(new ServletHolder(searchHelper), "/sh");

            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setDirectoriesListed(true);
            resourceHandler.setResourceBase("public_html");
            resourceHandler.setWelcomeFiles(new String[] {"index.html"});

            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[] {resourceHandler, context});

            server.setHandler(handlers);

            server.start();
            server.join();

        }

    }

}
