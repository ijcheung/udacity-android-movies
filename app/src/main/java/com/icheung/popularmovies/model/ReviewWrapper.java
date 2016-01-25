package com.icheung.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewWrapper {
    @SerializedName("results") private List<Review> reviews;

    public ReviewWrapper(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
