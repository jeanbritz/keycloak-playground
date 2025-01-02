package com.acme.listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class WebSessionListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        logger.debug("Web Session Created: {}", se.getSession().getId());

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        logger.debug("Web Session Destroyed: {}", se.getSession().getId());
    }
}
