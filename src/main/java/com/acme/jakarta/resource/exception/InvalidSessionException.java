package com.acme.jakarta.resource.exception;

import jakarta.ws.rs.NotAuthorizedException;

import java.util.Collections;


public class InvalidSessionException extends NotAuthorizedException {

    public InvalidSessionException(String message) {
        super(message, Collections.emptyList());
    }
}
