package com.acme.jakarta.resource;

import com.acme.hk2.service.MovieService;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public Response createMovie(@Valid MovieRequest request) {
        logger.debug("incoming request to create movie");
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
            @ApiResponse(responseCode = "404", description = "No movies found")
    })
    public Response getAllMovies(@QueryParam("watched") Boolean watched) {
        // Optional query param for filtering by watched status
        logger.debug("incoming request to view movie(s)");
        List<Movie> result;
        if (watched != null) {
            result = service.getByStatus(watched);
        } else {
            result = service.getAll();
        }
        if(result.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
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
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public Response getMovieById(@PathParam("id") String id) {
        logger.debug("incoming request to view movie");
        Movie movie = service.getById(id);
        if (movie == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
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
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public Response updateMovie(@PathParam("id") String id, @Valid MovieRequest updatedMovie) {
        logger.debug("incoming request to update movie");
        Movie movie = service.update(id, updatedMovie.getTitle(), updatedMovie.getDirector(), updatedMovie.getYear());
        if (movie == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Movie not found.")
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
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public Response updateStatus(@PathParam("id") String id, @QueryParam("status") boolean status) {
        logger.debug("incoming request to update movie status");
        Movie movie = service.updateStatus(id, status);
        if(movie == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Movie not found.")
                    .build();
        }
        return Response.ok(movie).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"delete-movie"})
    @Operation(summary = "Delete a movie", description = "Deletes a movie by its ID.", security = {@SecurityRequirement(name = "sessionCookie")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public Response deleteMovie(@PathParam("id") String id) {
        logger.debug("incoming request to delete movie");
        boolean removed = service.delete(id);
        if (!removed) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Movie not found.")
                    .build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
