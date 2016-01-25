package com.icheung.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.icheung.popularmovies.R;
import com.icheung.popularmovies.fragment.MovieDetailFragment;
import com.icheung.popularmovies.fragment.MoviesFragment;
import com.icheung.popularmovies.model.Movie;

public class MainActivity extends AppCompatActivity implements
        MoviesFragment.OnFragmentInteractionListener {
    private Toolbar mToolbar;
    private Spinner mSortSpinner;

    private MoviesFragment mMoviesFragment;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mSortSpinner = (Spinner) findViewById(R.id.sortSpinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mSortSpinner.getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sort_by));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(dataAdapter);
        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMoviesFragment.setSortBy(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do Nothing
            }
        });

        mTwoPane = findViewById(R.id.movie_detail_container) != null;

        mMoviesFragment = ((MoviesFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_movies));
        mMoviesFragment.setSortBy(mSortSpinner.getSelectedItemPosition());
    }

    @Override
    public void onMovieSelected(Movie movie) {
        if (mTwoPane) {
            MovieDetailFragment fragment = MovieDetailFragment.newInstance(movie);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .putExtra(MovieDetailActivity.KEY_MOVIE, movie);
            startActivity(intent);
        }
    }
}
