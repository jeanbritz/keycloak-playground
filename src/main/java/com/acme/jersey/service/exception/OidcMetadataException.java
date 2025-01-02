package com.acme.jersey.service.exception;

public class OidcMetadataException extends RuntimeException {
    public OidcMetadataException(String message, Exception e) {
      super(message, e);
    }
}
