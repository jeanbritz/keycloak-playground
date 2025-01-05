package com.acme.jakarta.security;

import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

public class AnonymousSecurityContext implements SecurityContext {

    private final SecurityContext originalContext;

    public AnonymousSecurityContext(SecurityContext originalContext) {
        this.originalContext = originalContext;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public boolean isSecure() {
        return this.originalContext.isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return this.originalContext.getAuthenticationScheme();
    }
}
