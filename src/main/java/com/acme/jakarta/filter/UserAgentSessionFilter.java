package com.acme.jakarta.filter;

import com.acme.Config;
import com.acme.hk2.mapper.RoleMapper;
import com.acme.hk2.service.UserAgentAnalyzer;
import com.acme.jakarta.security.SecurityEnforcedFilter;
import com.acme.jakarta.security.OAuthSecurityContext;
import com.acme.oidc.SessionAttrs;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import nl.basjes.parse.useragent.UserAgent;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.acme.log.Markers.SECURITY_MARKER;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class UserAgentSessionFilter extends SecurityEnforcedFilter {

    private static final Logger logger = LoggerFactory.getLogger(UserAgentSessionFilter.class);

    @Inject
    private RoleMapper roleMapper;

    @Inject
    private UserAgentAnalyzer analyzer;

    @Context
    private HttpServletRequest request;

    @Override
    public SecurityContext doFilter(ContainerRequestContext containerRequest) {
        UserAgent ua = analyzer.parse((ContainerRequest) containerRequest);
        SecurityContext originalContext = containerRequest.getSecurityContext();
        logger.info("Detected User Agent [Class: {}, Name: {}, Version: {}, OS: {}]",
                ua.get(UserAgent.AGENT_CLASS).getValue(),
                ua.get(UserAgent.AGENT_NAME).getValue(),
                ua.get(UserAgent.AGENT_VERSION),
                ua.get(UserAgent.OPERATING_SYSTEM_NAME).getValue());

        String cookieHeader = containerRequest.getHeaderString("Cookie");
        HttpSession session = null;
        if (cookieHeader != null) {
            String sessionCookieName = Config.getProperty(Config.Key.SESSION_COOKIE_NAME);
            Cookie[] cookies = request.getCookies();
            Cookie sessionCookie = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (sessionCookieName.equals(cookie.getName())) {
                        sessionCookie = cookie;
                        logger.debug("Detected Session Cookie: [{}]", sessionCookie.getValue());
                    }
                }
            }
            if (sessionCookie != null) {
                session = request.getSession(false);
            }
        }
        if (session != null) {
            try {
                List<String> roles;
                OIDCTokens tokens = (OIDCTokens) session.getAttribute(SessionAttrs.OIDC_TOKENS);
                if (tokens != null) {
                    AccessToken accessToken = tokens.getAccessToken();
                    SignedJWT signedAccessToken = SignedJWT.parse(accessToken.getValue());
                    roles = roleMapper.map(signedAccessToken);
                    return new OAuthSecurityContext(originalContext, signedAccessToken, roles);
                }
                return null;
            } catch (Exception e) {
                logger.warn(SECURITY_MARKER, "Error occurred while authorizing user [session id: {}]", session.getId(), e);
            }
        }
        return null;
    }

}
