package com.acme.jakarta.resource.exception;

public class OidcMetadataException extends RuntimeException {
    public OidcMetadataException(String message, Exception e) {
      super(message, e);
    }
}
