package com.acme.jakarta.security;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

public class OAuthSecurityContext implements SecurityContext {

    private final SecurityContext originalContext;
    private final List<String> roles;
    private final JWT accessToken;

    public OAuthSecurityContext(SecurityContext originalContext, SignedJWT accessToken, List<String> roles) {
        this.originalContext = originalContext;
        this.roles = roles;
        this.accessToken = accessToken;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> {
            try {
                return accessToken.getJWTClaimsSet().getSubject();
            } catch (ParseException e) {
                return "";
            }
        };
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
