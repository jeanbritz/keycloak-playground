package com.acme.jersey.service.exception;

public class OidcUserInfoException extends RuntimeException {
  public OidcUserInfoException(String message, Exception e) {
    super(message, e);
  }
}
