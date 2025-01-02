package com.acme.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class CorsPreflightRequestFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
            if (requestContext.getHeaderString("Origin") != null) {
                requestContext.setProperty("CORS_REQUEST", true);
            }
    }

}
