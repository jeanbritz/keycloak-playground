package com.acme.hk2.service;

import com.acme.Config;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.NewCookie;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;

@Service
public class WebSessionHelperImpl implements WebSessionHelper {

    private static final Logger logger = LoggerFactory.getLogger(WebSessionHelperImpl.class);

    @Context
    private HttpServletRequest request;

    @Override
    public NewCookie createSessionCookie(int maxAge) {
        return new NewCookie.Builder(Config.getProperty(Config.Key.SESSION_COOKIE_NAME))
                .path(Config.getProperty(Config.Key.SERVER_BASE_CONTEXT))
                .maxAge(maxAge)
                .httpOnly(true)
                .build();
    }

    @Override
    public void end(HttpSession session) {
        if(session != null) {
            logger.info("Ending web session [id:{}]", session.getId());
            try {
                session.invalidate();
            } catch (IllegalStateException e) {
                logger.debug("Web session already invalid, therefore could not be ended [id:{}]", session.getId(),e);
            }
        }
    }

    @Override
    public HttpSession get() {
        HttpSession session = request.getSession(false);
        if (session != null) {
            logger.debug("Retrieving existing valid session [id:{}]", session.getId());
        } else {
            logger.debug("Creating new web session");
            session = request.getSession(true);
        }
        return session;
    }

    public HttpSession renew(HttpServletRequest request) {
        HttpSession oldSession = request.getSession(false);
        HttpSession newSession = null;

        if (oldSession != null) {
            // Save attributes from the old session
            Enumeration<String> attributeNames = oldSession.getAttributeNames();
            newSession = request.getSession(true); // Create a new session

            while (attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                Object attributeValue = oldSession.getAttribute(attributeName);

                // Transfer attribute to the new session
                newSession.setAttribute(attributeName, attributeValue);
            }

            // Invalidate the old session
            try {
                oldSession.invalidate();
            } catch (IllegalStateException e) {
                logger.warn("Existing session already invalid, session id: {}", oldSession.getId());
            }
        } else {
            newSession = request.getSession(true);
        }

        return newSession;
    }
}
