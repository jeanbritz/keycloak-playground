package com.acme.hk2.service.exception;

public class OidcCallbackException extends RuntimeException {
    public OidcCallbackException(String message, Exception e) {
        super(message, e);
    }
}
