package com.acme.jakarta.mapper;

import com.acme.jakarta.resource.exception.OidcCallbackException;
import com.acme.jakarta.resource.oidc.OidcError;
import com.nimbusds.oauth2.sdk.ErrorObject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class OidcCallbackExceptionMapper implements ExceptionMapper<OidcCallbackException> {
    @Override
    public Response toResponse(OidcCallbackException e) {
        ErrorObject errorObject = e.getErrorObject();
        OidcError error;
        if(errorObject != null) {
            error = new OidcError(errorObject.getCode(), errorObject.getDescription(), e.getResponse().getStatus());
        } else if (e.getCause() != null){
            error = new OidcError("invalid_callback", e.getMessage(), e.getResponse().getStatus());
        } else {
            error = new OidcError("invalid_callback", e.getMessage(), e.getResponse().getStatus());
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(error).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
