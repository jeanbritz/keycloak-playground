package com.acme.movie;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MovieRequest {


    private final String title;
    private final String director;
    private final int year;

    public MovieRequest(@JsonProperty(value = "title", required = true) String title,
                        @JsonProperty(value = "director", required = true) String director,
                        @JsonProperty(value = "year", required = true) int year) {
        this.title = title;
        this.director = director;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public int getYear() {
        return year;
    }
}
