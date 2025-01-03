package com.acme.jakarta.filter;

import com.acme.Config;
import com.acme.hk2.service.RoleMapper;
import com.acme.jakarta.security.OAuthSecurityContext;
import com.acme.oidc.SessionAttrs;
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
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class OAuthAuthorizationFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(OAuthAuthorizationFilter.class);

    @Inject
    private RoleMapper roleMapper;

    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext containerRequest) {
        String cookieHeader = containerRequest.getHeaderString("Cookie");
        HttpSession session = null;
        if (cookieHeader != null) {
            String sessionName = Config.getProperty(Config.Key.SESSION_COOKIE_NAME);
            String sessionCookie = cookieHeader.substring(sessionName.length() + 1);
            if(!sessionCookie.isBlank()) {
                logger.debug("Detected Session Cookie: [{}]", sessionCookie);
                session = request.getSession(false);
                if (session == null) {
                    return;
                }
            }
        }
        if(session != null) {
            try {
                List<String> roles;
                OIDCTokens tokens = (OIDCTokens) session.getAttribute(SessionAttrs.OIDC_TOKENS);
                AccessToken accessToken = tokens.getAccessToken();

                SignedJWT signedAccessToken = SignedJWT.parse(accessToken.getValue());
                roles = roleMapper.map(signedAccessToken);
                SecurityContext originalContext = containerRequest.getSecurityContext();
                OAuthSecurityContext oAuthContext = new OAuthSecurityContext(originalContext, signedAccessToken.getJWTClaimsSet().getSubject(), roles);
                containerRequest.setSecurityContext(oAuthContext);

            } catch (Exception e) {
                logger.warn("error occurred while authorizing user [session id: {}]", session.getId(), e);
            }
        }

    }
}
