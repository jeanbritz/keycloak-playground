package com.acme.jakarta.mapper;

import com.acme.jakarta.resource.exception.InvalidSessionException;
import com.acme.jakarta.resource.oidc.OidcError;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidSessionExceptionMapper implements ExceptionMapper<InvalidSessionException> {

    @Override
    public Response toResponse(InvalidSessionException e) {
        OidcError error = new OidcError("unauthenticated", e.getMessage(), e.getResponse().getStatus());
        return Response.status(Response.Status.UNAUTHORIZED).entity(error).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
