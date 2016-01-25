package com.icheung.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerWrapper {
    @SerializedName("youtube") private List<Trailer> trailers;

    public TrailerWrapper(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
