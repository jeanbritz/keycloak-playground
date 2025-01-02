package com.acme.jakarta.filter;

import com.acme.hk2.service.RoleMapper;
import com.acme.jakarta.security.OAuthSecurityContext;
import com.acme.oidc.SessionAttrs;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class OAuthAuthorizationFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(OAuthAuthorizationFilter.class);

    @Inject
    private RoleMapper roleMapper;

    @Context
    private HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        List<String> roles = Collections.emptyList();
        HttpSession session = servletRequest.getSession(false);
        if (session == null) {
            abortWithUnauthorized(requestContext);
            return;
        }

        OIDCTokens tokens = (OIDCTokens) session.getAttribute(SessionAttrs.OIDC_TOKENS);
        AccessToken accessToken = tokens.getAccessToken();
        logger.debug("incoming access token: {}", accessToken);
        try {
            SignedJWT signedAccessToken = SignedJWT.parse(accessToken.getValue());
            roles = roleMapper.map(signedAccessToken);
            SecurityContext originalContext = requestContext.getSecurityContext();
            OAuthSecurityContext oAuthContext = new OAuthSecurityContext(originalContext, signedAccessToken.getJWTClaimsSet().getSubject(), roles);
            requestContext.setSecurityContext(oAuthContext);

        } catch (Exception e) {
            logger.warn("error occurred while authorizing user [session id: {}]", session.getId(), e);
            abortWithUnauthorized(requestContext);
        }

    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build()
        );
    }

}
