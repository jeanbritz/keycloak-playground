package com.acme.jakarta.listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionActivationListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionIdListener;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class WebSessionListener implements HttpSessionListener, HttpSessionBindingListener, HttpSessionIdListener, HttpSessionActivationListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        logger.debug("HTTP Session created [id:{}]", event.getSession().getId());

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        logger.debug("HTTP Session destroyed [id:{}]", event.getSession().getId());
    }

    @Override
    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
        logger.debug("HTTP Session ID Changed [old:{}, new:{}]", oldSessionId, event.getSession().getId());
    }

    @Override
    public void sessionWillPassivate(HttpSessionEvent event) {
        logger.debug("HTTP Session will passivate: [id:{}]", event.getSession().getId());
    }

    @Override
    public void sessionDidActivate(HttpSessionEvent event) {
        logger.debug("HTTP Session did activate: [id:{}]", event.getSession().getId());
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        logger.debug("HTTP Session value bound: [id:{}, value:{}]", event.getSession().getId(), event.getValue());
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        logger.debug("HTTP Session value unbound: [id:{}, value:{}]", event.getSession().getId(), event.getValue());
    }
}
