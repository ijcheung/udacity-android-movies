package com.icheung.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.icheung.popularmovies.R;
import com.icheung.popularmovies.fragment.MovieDetailFragment;
import com.icheung.popularmovies.model.Movie;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String KEY_MOVIE = "key_movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        Movie movie = (Movie) intent.getSerializableExtra(KEY_MOVIE);

        MovieDetailFragment fragment = MovieDetailFragment.newInstance(movie);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, fragment)
                .commit();
    }
}
