package com.acme;

import com.acme.jersey.OidcApplication;
import com.acme.jersey.APIServerApplication;
import com.acme.listener.WebSessionListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedJetty {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedJetty.class);

    public static void main(String[] args) {

        Server server = new Server(8080);

        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setHttpOnly(true); // Enable HttpOnly for cookies
        sessionHandler.setSecureRequestOnly(false); // Set to true to require HTTPS
        sessionHandler.getSessionCookieConfig().setName(Config.getProperty(Config.Key.SESSION_COOKIE_NAME)); // Custom cookie name
        sessionHandler.getSessionCookieConfig().setPath("/"); // Cookie path
        sessionHandler.getSessionCookieConfig().setDomain(""); // Optional: set domain
        sessionHandler.getSessionCookieConfig().setMaxAge(3600); // Cookie lifespan in seconds

        // Set up a context handler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/"); // Root context
        context.setSessionHandler(sessionHandler);
        server.setHandler(context);


        // Add Jersey Application
        ServletHolder jerseyServlet = new ServletHolder(ServletContainer.class);
        jerseyServlet.setInitParameter("jakarta.ws.rs.Application", APIServerApplication.class.getName());
        context.addServlet(jerseyServlet, "/api/*");
        jerseyServlet.setInitOrder(0); // Ensure this servlet is initialized at startup

        // Add a servlet
        ServletHolder oidcServlet = new ServletHolder(ServletContainer.class);
        oidcServlet.setInitParameter("jakarta.ws.rs.Application", OidcApplication.class.getName());
        context.addServlet(oidcServlet, "/*");
        oidcServlet.setInitOrder(0); // Ensure this servlet is initialized at startup
//        context.addFilter(CorsResponseFilter.class.getName(), "/*", EnumSet.of(DispatcherType.REQUEST));
        context.addEventListener(new WebSessionListener());
        // Start the server
        try {
            server.start();
            logger.info("Server started at http://localhost:8080/");
            server.join();
        } catch (Exception e) {
            logger.error("error occurred while starting up server", e);
        }


        // Wait for the server to stop
    }
}