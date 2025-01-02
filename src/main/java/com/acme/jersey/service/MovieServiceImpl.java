package com.acme.jersey.service;

import com.acme.movie.Movie;
import com.acme.util.IdGenerator;
import com.acme.util.UUIDGenerator;
import jakarta.annotation.PostConstruct;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieServiceImpl.class);

    private static final IdGenerator<String> ID_GENERATOR = new UUIDGenerator();

    private static final Map<String, Movie> STORE = new HashMap<>();

    @PostConstruct
    public void init() {
        logger.info("Populating resource with some movie data!");
        loadInitialData();
    }

    @Override
    public Movie createMovie(String title, String director, int year) {
        String id = ID_GENERATOR.next();
        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle(title);
        movie.setDirector(director);
        movie.setYear(year);
        STORE.put(id, movie);
        return movie;
    }

    @Override
    public List<Movie> getAll() {
        return new ArrayList<>(STORE.values());
    }

    @Override
    public Movie getById(String id) {
        return STORE.get(id);
    }

    @Override
    public Movie update(String id, String title, String director, int year) {
        if(!STORE.containsKey(id)) {
            return null;
        }
        Movie m = STORE.get(id);
        m.setTitle(title);
        m.setDirector(director);
        m.setYear(year);
        STORE.put(m.getId(), m);
        return m;
    }

    @Override
    public Movie updateStatus(String id, boolean status) {
        if(!STORE.containsKey(id)) {
            return null;
        }
        Movie m = STORE.get(id);
        m.setWatched(status);
        STORE.put(m.getId(), m);
        return m;
    }

    @Override
    public boolean delete(String id) {
        return STORE.remove(id) != null;
    }

    @Override
    public List<Movie> getByStatus(boolean watched) {
        return STORE.values().stream()
                .filter(movie -> movie.isWatched() == watched)
                .collect(Collectors.toList());
    }

    private void loadInitialData() {
        createMovie("Inception", "Christopher Nolan", 2010);
        createMovie("The Matrix", "Lana & Lilly Wachowski", 1999);
        createMovie("The Shawshank Redemption", "Frank Darabont", 1994);
        createMovie("The Godfather", "Francis Ford Coppola", 1972);
    }


}
