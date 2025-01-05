package com.acme.jakarta.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.acme.log.Markers.SECURITY_MARKER;

public abstract class SecurityEnforcedFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityEnforcedFilter.class);

    protected abstract SecurityContext doFilter(ContainerRequestContext containerRequestContext);

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        SecurityContext originalContext = containerRequestContext.getSecurityContext();
        SecurityContext newContext = null;
        try {
            newContext = doFilter(containerRequestContext);
            if (newContext == null || newContext.getUserPrincipal() == null) {
                newContext = new AnonymousSecurityContext(originalContext);
            }
            containerRequestContext.setSecurityContext(newContext);
        } catch (Exception e) {
            logger.warn(SECURITY_MARKER,"Error occurred while enforcing SecurityContext", e);
            containerRequestContext.setSecurityContext(new InsecureSecurityContext());
        }
    }
}
