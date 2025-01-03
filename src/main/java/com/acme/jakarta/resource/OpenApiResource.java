package com.acme.jakarta.resource;

import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.ServletConfig;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

@Path("/openapi.{type:json|yaml}")
public class OpenApiResource extends BaseOpenApiResource {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiResource.class);

    @Context
    private ServletConfig config;

    @Context
    private Application app;

    @Context
    private UriInfo uriInfo;

    @PostConstruct
    public void init() {
        logger.warn("[SECURITY] OpenAPI endpoint exposed at {}", uriInfo.getAbsolutePath());
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, "application/yaml"})
    @Operation(hidden = true)
    @PermitAll
    public Response getOpenApi(
            @Context final HttpHeaders headers,
            @PathParam("type") final String type
    ) throws Exception {
        return super.getOpenApi(headers, config, app, uriInfo, type);
    }
}
