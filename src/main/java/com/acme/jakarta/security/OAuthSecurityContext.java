package com.acme.jakarta.security;

import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.util.List;

public class OAuthSecurityContext implements SecurityContext {

    private final SecurityContext originalContext;
    private final List<String> roles;
    private final String subject;

    public OAuthSecurityContext(SecurityContext originalContext, String subject, List<String> roles) {
        this.originalContext = originalContext;
        this.roles = roles;
        this.subject = subject;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> subject;
    }

    @Override
    public boolean isUserInRole(String role) {
        return roles.contains(role);
    }

    @Override
    public boolean isSecure() {
        return originalContext.isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }
}
