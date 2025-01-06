package com.acme.jakarta.resource.exception;

public class OidcUserInfoException extends RuntimeException {
  public OidcUserInfoException(String message, Exception e) {
    super(message, e);
  }
}
