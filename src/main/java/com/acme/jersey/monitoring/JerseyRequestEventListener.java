package com.acme.jersey.monitoring;

import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.acme.log.Markers.MONITORING_MARKER;

public class JerseyRequestEventListener implements RequestEventListener {

    private static final Logger logger = LoggerFactory.getLogger(JerseyRequestEventListener.class);

    @Override
    public void onEvent(RequestEvent event) {
        switch (event.getType()) {
            case MATCHING_START:
                logEvent(event, "Request matching started");
                break;
            case REQUEST_MATCHED:
                logEvent(event, "Request matched");
                case RESOURCE_METHOD_START:
                logEvent(event,"Request resource method start: {}", event.getUriInfo().getMatchedResources());
                break;
            case ON_EXCEPTION:
                logEvent(event, "On exception: {}", event.getException().getCause());
                case FINISHED:
                String status = (event.getContainerResponse() != null? String.valueOf(event.getContainerResponse().getStatus()) : "unknown");
                logEvent(event, "Request finished with status {}", status);
                break;
            default:
                logEvent(event, "Unknown request event");
                break;
        }
    }

    private void logEvent(RequestEvent event, String message, Object... parameter) {
        String format = "[uri:{}] [{}] " + message;
        logger.debug(MONITORING_MARKER, format, event.getUriInfo().getRequestUri().getPath(), event.getType(), parameter);
    }
}