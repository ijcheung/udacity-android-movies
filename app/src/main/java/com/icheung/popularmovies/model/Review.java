package com.icheung.popularmovies.model;

public class Review {
    private String content;

    public Review(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
