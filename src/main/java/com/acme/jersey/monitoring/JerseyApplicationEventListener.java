package com.acme.jersey.monitoring;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.acme.log.Markers.MONITORING_MARKER;

public class JerseyApplicationEventListener implements ApplicationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(JerseyApplicationEventListener.class);

    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
            case INITIALIZATION_START:
                logEvent(event, "Initialization started");
                break;
            case INITIALIZATION_FINISHED:
                logEvent(event, "Initialization finished");
                break;
            case INITIALIZATION_APP_FINISHED:
                logEvent(event, "Initialization of application finished");
                break;
            case DESTROY_FINISHED:
                logEvent(event, "Application is shutting down");
                break;
            case RELOAD_FINISHED:
                logEvent(event, "Application reload finished");
                break;
            default:
                logEvent(event, "Unknown application event");
                break;
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return new JerseyRequestEventListener();
    }

    private void logEvent(ApplicationEvent event, String message) {
        String format = "[app:{}] [{}] {}";
        logger.debug(MONITORING_MARKER, format, event.getResourceConfig().getApplicationPath(), event.getType(), message);
    }
}
