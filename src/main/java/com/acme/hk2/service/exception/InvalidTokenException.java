package com.acme.hk2.service.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message, Exception e) {
      super(message, e);
    }
}
