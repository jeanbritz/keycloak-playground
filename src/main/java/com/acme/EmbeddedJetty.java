package com.acme;

import com.acme.jakarta.OidcApplication;
import com.acme.jakarta.ApiServerApplication;
import com.acme.jakarta.listener.WebSessionListener;
import com.acme.log.LogbackConfig;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class EmbeddedJetty {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedJetty.class);

    public static void main(String[] args) {
        InetSocketAddress addr = null;
        String hostname = null;
        int port = -1;
        try {
            hostname = Config.getProperty(Config.Key.SERVER_HOSTNAME);
            port = Config.getIntProperty(Config.Key.SERVER_PORT);
            addr = InetSocketAddress.createUnresolved(hostname, port);
        } catch (Exception e) {
            logger.error("Unable to resolve given server address [{}:{}]", hostname, port, e);
            System.exit(0);
        }
        Server server = new Server(addr);

        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setHttpOnly(true); // Enable HttpOnly for cookies
        sessionHandler.setSecureRequestOnly(false); // Set to true to require HTTPS
        sessionHandler.getSessionCookieConfig().setName(Config.getProperty(Config.Key.SESSION_COOKIE_NAME)); // Custom cookie name
        sessionHandler.getSessionCookieConfig().setPath(Config.getProperty(Config.Key.SERVER_BASE_CONTEXT)); // Cookie path
        sessionHandler.getSessionCookieConfig().setDomain(""); // Optional: set domain, maybe the same as the hostname?
        sessionHandler.getSessionCookieConfig().setMaxAge(-1); // Cookie lifespan set live as long as the browser session
        sessionHandler.setSameSite(HttpCookie.SameSite.LAX);
        sessionHandler.setMaxInactiveInterval(24 * 60 * 60);

        // Set up a context handler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(Config.getProperty(Config.Key.SERVER_BASE_CONTEXT)); // Root context
        context.setSessionHandler(sessionHandler);
        server.setHandler(context);


        // Add Jersey Application
        ServletHolder apiServlet = new ServletHolder(ServletContainer.class);
        apiServlet.setInitParameter("jakarta.ws.rs.Application", ApiServerApplication.class.getName());
        apiServlet.setInitParameter("jersey.config.server.wadl.disableWadl", "true");
        context.addServlet(apiServlet, "/api/*");
        apiServlet.setInitOrder(0); // Ensure this servlet is initialized at startup

        // Add OIDC Application
        ServletHolder oidcServlet = new ServletHolder(ServletContainer.class);
        oidcServlet.setInitParameter("jakarta.ws.rs.Application", OidcApplication.class.getName());
        oidcServlet.setInitParameter("jersey.config.server.wadl.disableWadl", "true");
        context.addServlet(oidcServlet, "/oidc/*");
        oidcServlet.setInitOrder(0); // Ensure this servlet is initialized at startup

        context.addEventListener(new WebSessionListener());

        try {
            // Start the server
            server.start();
            logger.info("Server started at {}", server.getURI());
            server.join();
        } catch (Exception e) {
            logger.error("error occurred while starting up server", e);
        }


    }
}