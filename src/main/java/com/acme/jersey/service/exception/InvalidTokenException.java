package com.acme.jersey.service.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message, Exception e) {
      super(message, e);
    }
}
