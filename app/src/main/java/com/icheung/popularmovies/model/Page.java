package com.icheung.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Page {
    private int page;
    private List<Movie> results;
    @SerializedName("total_pages") private int totalPages;

    public Page(int page, List<Movie> results, int totalPages) {
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
