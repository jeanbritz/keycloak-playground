package com.acme.hk2.service.exception;

public class OidcMetadataException extends RuntimeException {
    public OidcMetadataException(String message, Exception e) {
      super(message, e);
    }
}
