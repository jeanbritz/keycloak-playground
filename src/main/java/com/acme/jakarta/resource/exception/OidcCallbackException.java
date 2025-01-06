package com.acme.jakarta.resource.exception;

import com.nimbusds.oauth2.sdk.ErrorObject;
import jakarta.ws.rs.BadRequestException;

public class OidcCallbackException extends BadRequestException {

    private ErrorObject errorObject = null;

    public OidcCallbackException(String message) {
        super(message);
    }

    public OidcCallbackException(String message, Exception e) {
        super(message, e);
    }

    public OidcCallbackException(String message, ErrorObject errorObject) {
        super(message);
        this.errorObject = errorObject;
    }

    public ErrorObject getErrorObject() {
        return this.errorObject;
    }
}
