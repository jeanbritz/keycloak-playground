package com.acme.jersey.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.NewCookie;
import jakarta.servlet.http.HttpSession;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface WebSessionHelperService {

    NewCookie createSessionCookie(int maxAge);

    HttpSession createNewSession(HttpServletRequest request);

    HttpSession getExistingSession(HttpServletRequest request);

    HttpSession renewSession(HttpServletRequest request);
}
