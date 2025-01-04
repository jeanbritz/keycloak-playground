package com.acme.jakarta.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;


import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Priority(Priorities.AUTHORIZATION)
public class RolesAllowedRequestFilter implements ContainerRequestFilter {

    private final boolean denyAll;
    private final List<String> rolesAllowed;

    public RolesAllowedRequestFilter() {
        this.denyAll = true;
        this.rolesAllowed = null;
    }

    public RolesAllowedRequestFilter(List<String> rolesAllowed) {
        this.denyAll = false;
        this.rolesAllowed = rolesAllowed != null ? rolesAllowed : Collections.emptyList();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if(!isAuthenticated(requestContext)) {
            throw new NotAuthorizedException("Not Authorized");
        }
        if (!this.denyAll) {
            for (String role : this.rolesAllowed) {
                if (requestContext.getSecurityContext().isUserInRole(role)) {
                    return;
                }
            }
            if (!this.rolesAllowed.isEmpty()) {
                throw new ForbiddenException("Forbidden");
            }
        }
        throw new ForbiddenException("Forbidden");
    }

    private static boolean isAuthenticated(ContainerRequestContext requestContext) {
        return requestContext.getSecurityContext().getUserPrincipal() != null;
    }

}
