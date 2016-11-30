package ru.velkomfood.mrp.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.velkomfood.mrp.server.view.Frontend;

// Jetty starter
public class Launcher {

    public static void main(String[] args) throws Exception {

        final int PORT = 9393;
        System.out.append("Start server on the port " + PORT).append("\n");

        // Make a servlet's context
        Frontend frontend = new Frontend();
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(frontend), "/mrp");

        // Include static resources (html, javascript, styles)
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setResourceBase("public_html");
        resourceHandler.setRedirectWelcome(true);

        // Make a list of handlers
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {resourceHandler, context});

        // Build an instance of servlet container
        Server server = new Server(PORT);
        server.setHandler(handlers);

        server.start();
        server.join();

    }

}
