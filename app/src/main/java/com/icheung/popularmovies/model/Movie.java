package com.icheung.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Movie implements Serializable {
    private int id;
    @SerializedName("poster_path")private String posterPath;
    private String title;
    @SerializedName("release_date")private String releaseDate;
    private String overview;
    @SerializedName("vote_average")private float voteAverage;

    public Movie(int id, String posterPath, String title, String releaseDate, String overview, float voteAverage) {
        this.id = id;
        this.posterPath = posterPath;
        this.title = title;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.voteAverage = voteAverage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    @Override
    public String toString() {
        return "Movie {" +
                "\n\tid=" + id +
                "\n\tposterPath=" + posterPath +
                "\n\ttitle=" + title +
                "\n\treleaseDate=" + releaseDate +
                "\n\toverview=" + overview +
                "\n\tvoteAverage=" + voteAverage +
                "\n}";
    }
}
