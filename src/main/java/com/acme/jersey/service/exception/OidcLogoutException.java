package com.acme.jersey.service.exception;

public class OidcLogoutException extends RuntimeException {

    public OidcLogoutException(String message, Exception e) {
        super(message, e);
    }
}
