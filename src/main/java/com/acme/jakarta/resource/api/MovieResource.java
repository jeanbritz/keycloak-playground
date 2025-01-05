package com.acme.jakarta.resource.api;

import com.acme.hk2.service.MovieService;
import com.acme.jakarta.annotation.UserPrincipal;
import com.acme.jakarta.security.OAuthSecurityContext;
import com.acme.movie.MovieRequest;
import com.acme.movie.Movie;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;


@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Movies", description = "Movie management APIs")
public class MovieResource {

    private static final Logger logger = LoggerFactory.getLogger(MovieResource.class);

    @Inject
    private MovieService service;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"create-movie"})
    @Operation(summary = "Create a new movie", description = "Adds a new movie to the collection.", security = {@SecurityRequirement(name = "sessionCookie")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Movie.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not allowed to create movie")
    })
    public Response createMovie(@UserPrincipal Principal principal, @Valid MovieRequest request) {
        logger.debug("Incoming request to create movie from {}", principal.getName());
        Movie created = service.createMovie(request.getTitle(), request.getDirector(), request.getYear());

        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @RolesAllowed({"view-movie"})
    @Operation(summary = "Get all movies", description = "Fetches a list of all movies with an optional filter for watched status.", security = {@SecurityRequirement(name = "sessionCookie")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movies retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Movie.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not allowed to view movies"),
            @ApiResponse(responseCode = "404", description = "No movies found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public Response getAllMovies(@UserPrincipal Principal principal, @QueryParam("watched") Boolean watched) {
        logger.debug("Incoming request to view movie(s) from {}", principal.getName());
        List<Movie> result;
        // Optional query param for filtering by watched status
        if (watched != null) {
            result = service.getByStatus(watched);
        } else {
            result = service.getAll();
        }
        if(result.isEmpty()) {
            ApiError apiError = new ApiError("No movie(s) found, given the criteria");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(apiError)
                    .build();
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"view-movie"})
    @Operation(summary = "Get movie by ID", description = "Fetches a single movie by its ID.", security = {@SecurityRequirement(name = "sessionCookie")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Movie.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not allowed to view movie"),
            @ApiResponse(responseCode = "404", description = "Movie not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public Response getMovieById(@UserPrincipal Principal principal, @PathParam("id") String id) {
        logger.debug("Incoming request to view movie from {}", principal);
        Movie movie = service.getById(id);
        if (movie == null) {
            ApiError apiError = new ApiError("Missing requirements");
            apiError.addDetail(new ApiError.ApiErrorMessage("id", "Movie not found"));
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(apiError)
                    .build();
        }
        return Response.ok(movie).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"update-movie"})
    @Operation(summary = "Update a movie", description = "Updates the details of an existing movie.", security = {@SecurityRequirement(name = "sessionCookie")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Movie.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not allowed to update movie"),
            @ApiResponse(responseCode = "404", description = "Movie not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    public Response updateMovie(@UserPrincipal Principal principal, @PathParam("id") String id, @Valid MovieRequest updatedMovie) {
        logger.debug("Incoming request to update movie from {}", principal.getName());
        Movie movie = service.update(id, updatedMovie.getTitle(), updatedMovie.getDirector(), updatedMovie.getYear());
        if (movie == null) {
            ApiError apiError = new ApiError("Missing requirements");
            apiError.addDetail(new ApiError.ApiErrorMessage("id", "Movie not found"));
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(apiError)
                    .build();
        }
        return Response.ok(movie).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"update-movie"})
    @Operation(summary = "Update movie status", description = "Updates the watched status of a movie.", security = {@SecurityRequirement(name = "sessionCookie")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Movie.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not allowed to update movie's statue"),
            @ApiResponse(responseCode = "404", description = "Movie not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public Response updateStatus(@UserPrincipal Principal principal, @PathParam("id") String id, @QueryParam("status") boolean status) {
        logger.debug("Incoming request to update movie status from {}", principal.getName());
        Movie movie = service.updateStatus(id, status);
        if(movie == null) {
            ApiError apiError = new ApiError("Missing requirements");
            apiError.addDetail(new ApiError.ApiErrorMessage("id", "Movie not found"));
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(apiError)
                    .build();
        }
        return Response.ok(movie).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"delete-movie"})
    @Operation(summary = "Delete a movie", description = "Deletes a movie by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not allowed to delete movie"),
            @ApiResponse(responseCode = "404", description = "Movie not found", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public Response deleteMovie(@UserPrincipal Principal principal, @PathParam("id") String id) {
        logger.debug("Incoming request to delete movie from {}", principal.getName());
        boolean removed = service.delete(id);
        if (!removed) {
            ApiError apiError = new ApiError("Missing requirements");
            apiError.addDetail(new ApiError.ApiErrorMessage("id", "Movie not found"));
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(apiError)
                    .build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
