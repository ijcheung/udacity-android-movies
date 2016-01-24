package com.icheung.popularmovies.fragment;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icheung.popularmovies.R;
import com.icheung.popularmovies.model.Movie;
import com.icheung.popularmovies.util.Constants;
import com.squareup.picasso.Picasso;

public class MovieDetailFragment extends Fragment {
    public static final String ARG_ID           = "id";
    public static final String ARG_TITLE        = "title";
    public static final String ARG_RELEASE_DATE = "release_date";
    public static final String ARG_POSTER_PATH  = "poster_path";
    public static final String ARG_OVERVIEW     = "overview";
    public static final String ARG_VOTE_AVERAGE = "vote_average";

    private CollapsingToolbarLayout mToolbar;
    private ImageView mPoster;
    private TextView mReleaseDate;
    private TextView mRating;
    private TextView mSummary;

    public MovieDetailFragment() {
    }

    public static MovieDetailFragment newInstance(Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, movie.getId());
        args.putString(ARG_TITLE, movie.getTitle());
        args.putString(ARG_RELEASE_DATE, movie.getReleaseDate());
        args.putString(ARG_POSTER_PATH, movie.getPosterPath());
        args.putString(ARG_OVERVIEW, movie.getOverview());
        args.putFloat(ARG_VOTE_AVERAGE, movie.getVoteAverage());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mToolbar = (CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar);
        mPoster = (ImageView) root.findViewById(R.id.poster);
        mReleaseDate = (TextView) root.findViewById(R.id.release_date);
        mRating = (TextView) root.findViewById(R.id.rating);
        mSummary = (TextView) root.findViewById(R.id.summary);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Picasso.with(getActivity())
                .load(Constants.POSTER_BASE_URL + Constants.POSTER_SIZE_780 + getArguments().getString(ARG_POSTER_PATH))
                .into(mPoster);

        mToolbar.setTitle(getArguments().getString(ARG_TITLE));

        mReleaseDate.setText(getArguments().getString(ARG_RELEASE_DATE));
        mRating.setText(getArguments().getFloat(ARG_VOTE_AVERAGE) + "/10.0");
        mSummary.setText(getArguments().getString(ARG_OVERVIEW));
    }
}
