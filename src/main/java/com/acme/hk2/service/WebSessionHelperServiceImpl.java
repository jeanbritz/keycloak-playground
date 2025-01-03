package com.acme.hk2.service;

import com.acme.Config;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.NewCookie;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;

@Service
public class WebSessionHelperServiceImpl implements WebSessionHelperService {

    private static final Logger logger = LoggerFactory.getLogger(WebSessionHelperServiceImpl.class);

    @Override
    public NewCookie createSessionCookie(int maxAge) {
        return new NewCookie.Builder(Config.getProperty(Config.Key.SESSION_COOKIE_NAME))
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .build();
    }

    @Override
    public HttpSession createNewSession(HttpServletRequest request) {
        HttpSession newSession = request.getSession(true);
        logger.debug("creating new web session [session id: {}]", newSession.getId());
        return request.getSession(true);
    }

    @Override
    public HttpSession getExistingSession(HttpServletRequest request) {
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) {
            logger.debug("getting existing web session [session id: {}]", existingSession.getId());
        } else {
            // No valid session associated with the HTTP request
            logger.warn("unable to get existing session");
        }
        return existingSession;
    }

    @Override
    public HttpSession renewSession(HttpServletRequest request) {
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
                logger.warn("existing session already invalid, session id: {}", oldSession.getId());
            }
        } else {
            newSession = request.getSession(true);
        }

        return newSession;
    }
}
