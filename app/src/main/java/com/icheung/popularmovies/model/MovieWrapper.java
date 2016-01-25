package com.icheung.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieWrapper {
    private int page;
    @SerializedName("results") private List<Movie> movies;
    @SerializedName("total_pages") private int totalPages;

    public MovieWrapper(int page, List<Movie> movies, int totalPages) {
        this.page = page;
        this.movies = movies;
        this.totalPages = totalPages;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
