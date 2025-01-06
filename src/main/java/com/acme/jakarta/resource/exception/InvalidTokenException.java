package com.acme.jakarta.resource.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message, Exception e) {
      super(message, e);
    }
}
