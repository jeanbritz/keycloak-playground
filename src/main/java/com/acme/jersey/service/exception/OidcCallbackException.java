package com.acme.jersey.service.exception;

public class OidcCallbackException extends RuntimeException {
    public OidcCallbackException(String message, Exception e) {
        super(message, e);
    }
}
