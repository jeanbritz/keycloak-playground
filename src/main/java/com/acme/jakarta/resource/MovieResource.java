package com.acme.jakarta.resource;

import com.acme.hk2.service.MovieService;
import com.acme.movie.MovieRequest;
import com.acme.movie.Movie;
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
public class MovieResource {

    private static final Logger logger = LoggerFactory.getLogger(MovieResource.class);

    @Inject
    private MovieService service;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"create-movie"})
    public Response createMovie(@Valid MovieRequest request) {
        Movie created = service.createMovie(request.getTitle(), request.getDirector(), request.getYear());

        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @RolesAllowed({"view-movie"})
    public Response getAllMovies(@QueryParam("watched") Boolean watched) {
        // Optional query param for filtering by watched status
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
    public Response getMovieById(@PathParam("id") String id) {
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
    public Response updateMovie(@PathParam("id") String id, @Valid MovieRequest updatedMovie) {
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
    public Response updateStatus(@PathParam("id") String id, @QueryParam("status") boolean status) {
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
    public Response deleteMovie(@PathParam("id") String id) {
        boolean removed = service.delete(id);
        if (!removed) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Movie not found.")
                    .build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
