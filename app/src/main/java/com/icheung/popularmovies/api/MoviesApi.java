package com.icheung.popularmovies.api;

import com.icheung.popularmovies.model.Page;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MoviesApi {
    public static final String BASE_ENDPOINT = "http://api.themoviedb.org";

    //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&page=1&api_key=xxx
    @GET("3/discover/movie")
    public Call<Page> getMovies(
            @Query("sort_by") String sortBy,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );
}