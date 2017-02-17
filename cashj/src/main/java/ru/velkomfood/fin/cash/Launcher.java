package ru.velkomfood.fin.cash;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.velkomfood.fin.cash.controller.CacheEngine;
import ru.velkomfood.fin.cash.controller.DbManager;
import ru.velkomfood.fin.cash.controller.SapSniffer;
import ru.velkomfood.fin.cash.view.Frontend;

public class Launcher {

	public static void main(String[] args) throws Exception {

		Frontend frontend = new Frontend();

		SapSniffer sap = SapSniffer.getInstance();
		DbManager db = DbManager.getInstance();
		CacheEngine ce = CacheEngine.getInstance();

		sap.initSAPConnection();
		db.createDataSource();
		db.openDbConnection();
		db.initDatabase();
		db.closeDbConnection();

		// set the basic data processors
		frontend.setSapSniffer(sap);
		frontend.setCache(ce);
		frontend.setDbManager(db);

		ServletContextHandler context =
				new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.addServlet(new ServletHolder(frontend), "/cj");

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase("public_html");
		resourceHandler.setWelcomeFiles(new String[] {"index.html"});
		resourceHandler.setDirectoriesListed(false);
		resourceHandler.setRedirectWelcome(true);

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {resourceHandler, context});

		// Create an instance of Jetty server and start it
		System.out.append("Start server on the port 8092\n");
		Server server = new Server(8092);
		server.setHandler(handlers);

		server.start();
		server.join();

	}

}