package com.acme.hk2.service;

import jakarta.ws.rs.core.NewCookie;
import jakarta.servlet.http.HttpSession;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface WebSessionHelper {

    NewCookie createSessionCookie(int maxAge);

    void end(HttpSession session);

    HttpSession get();

}
