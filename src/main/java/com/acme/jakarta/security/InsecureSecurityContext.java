package com.acme.jakarta.security;

import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

public class InsecureSecurityContext implements SecurityContext {

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
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return "";
    }
}
