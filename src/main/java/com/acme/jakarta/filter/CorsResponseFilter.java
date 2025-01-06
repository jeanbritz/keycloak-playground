package com.acme.jakarta.filter;

import com.acme.Config;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Priority(Priorities.HEADER_DECORATOR)
public class CorsResponseFilter implements ContainerResponseFilter {

    private final Logger logger = LoggerFactory.getLogger(CorsResponseFilter.class);

    private static final String CORS_ALLOW_ORIGIN = Config.getProperty(Config.Key.FRONTEND_BASE_URL);
    private static final String CORS_ALLOW_METHODS = "OPTIONS, GET, POST, PUT, DELETE";
    private static final String CORS_ALLOW_HEADERS = "Content-Type";
    private static final String CORS_EXPOSE_HEADERS = "Content-Type";

    private static final boolean CORS_ALLOW_CREDENTIALS = true;
    private static final int CORS_MAX_AGE = 3600;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Object corsRequest = requestContext.getProperty("CORS_REQUEST");
        if (corsRequest != null && Boolean.parseBoolean(corsRequest.toString())) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", CORS_ALLOW_ORIGIN);
            if (CORS_ALLOW_ORIGIN.equals("*")) {
                logger.warn("CORS configuration uses wildcard domain, which could pose as a security risk");
            }

            responseContext.getHeaders().add("Access-Control-Allow-Methods", CORS_ALLOW_METHODS);
            responseContext.getHeaders().add("Access-Control-Allow-Headers", CORS_ALLOW_HEADERS);
            responseContext.getHeaders().add("Access-Control-Expose-Headers", CORS_EXPOSE_HEADERS);
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", CORS_ALLOW_CREDENTIALS);
            responseContext.getHeaders().add("Access-Control-Max-Age", CORS_MAX_AGE);
        }
    }
}
