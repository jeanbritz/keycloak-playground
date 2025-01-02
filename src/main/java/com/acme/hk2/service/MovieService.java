package com.acme.hk2.service;

import com.acme.movie.Movie;
import org.jvnet.hk2.annotations.Contract;

import java.util.List;

@Contract
public interface MovieService {

    Movie createMovie(String title, String director, int year);

    List<Movie> getAll();

    Movie getById(String id);

    Movie update(String id, String title, String director, int year);

    Movie updateStatus(String id, boolean status);

    boolean delete(String id);

    List<Movie> getByStatus(boolean watched);

}
