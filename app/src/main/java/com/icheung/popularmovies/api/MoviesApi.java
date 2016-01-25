package com.icheung.popularmovies.api;

import com.icheung.popularmovies.model.MovieWrapper;
import com.icheung.popularmovies.model.ReviewWrapper;
import com.icheung.popularmovies.model.TrailerWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesApi {
    public static final String BASE_ENDPOINT = "http://api.themoviedb.org";

    @GET("3/discover/movie")
    public Call<MovieWrapper> getMovies(
            @Query("sort_by") String sortBy,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("3/movie/{id}/trailers")
    public Call<TrailerWrapper> getTrailers(
            @Path("id") int id,
            @Query("api_key") String apiKey
    );

    @GET("3/movie/{id}/reviews")
    public Call<ReviewWrapper> getReviews(
            @Path("id") int id,
            @Query("api_key") String apiKey
    );
}