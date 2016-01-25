package com.icheung.popularmovies.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.icheung.popularmovies.R;
import com.icheung.popularmovies.data.MovieContract;
import com.icheung.popularmovies.model.Movie;
import com.icheung.popularmovies.util.Constants;
import com.squareup.picasso.Picasso;

public class MovieDetailFragment extends Fragment implements Button.OnClickListener {
    public static final String ARG_ID           = "id";
    public static final String ARG_TITLE        = "title";
    public static final String ARG_RELEASE_DATE = "release_date";
    public static final String ARG_POSTER_PATH  = "poster_path";
    public static final String ARG_OVERVIEW     = "overview";
    public static final String ARG_VOTE_AVERAGE = "vote_average";

    private ContentResolver mContentResolver;

    private CollapsingToolbarLayout mToolbar;
    private ImageView mPoster;
    private TextView mReleaseDate;
    private TextView mRating;
    private Button mFavorite;
    private TextView mSummary;

    private boolean isFavorited = false;

    public MovieDetailFragment() { }

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
        mFavorite = (Button) root.findViewById(R.id.favorite);
        mSummary = (TextView) root.findViewById(R.id.summary);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContentResolver = getActivity().getContentResolver();

        String[] selectionArgs = {Integer.toString(getArguments().getInt(ARG_ID))};
        Cursor cursor = mContentResolver.query(MovieContract.FavoriteMoviesEntry.CONTENT_URI,
                        null,
                        MovieContract.FavoriteMoviesEntry.COLUMN_ID + " = ?",
                        selectionArgs,
                        null);

        if(cursor.moveToFirst()){
            mFavorite.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.btn_star_big_on, 0, 0, 0);
            isFavorited = true;
        }

        mFavorite.setOnClickListener(this);

        Picasso.with(getActivity())
                .load(Constants.POSTER_BASE_URL + Constants.POSTER_SIZE_780 + getArguments().getString(ARG_POSTER_PATH))
                .into(mPoster);

        mToolbar.setTitle(getArguments().getString(ARG_TITLE));

        mReleaseDate.setText(getArguments().getString(ARG_RELEASE_DATE));
        mRating.setText(getArguments().getFloat(ARG_VOTE_AVERAGE) + "/10.0");
        mSummary.setText(getArguments().getString(ARG_OVERVIEW));
    }

    @Override
    public void onClick(View v) {
        if(isFavorited) {
            String[] selectionArgs = {Integer.toString(getArguments().getInt(ARG_ID))};
            int num = mContentResolver.delete(MovieContract.FavoriteMoviesEntry.CONTENT_URI,
                    MovieContract.FavoriteMoviesEntry.COLUMN_ID + " = ?",
                    selectionArgs);
            if(num == 1) {
                mFavorite.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.btn_star_big_off, 0, 0, 0);
                isFavorited = false;
            }
        }
        else {
            Bundle arguments = getArguments();
            ContentValues values = new ContentValues();

            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_ID, arguments.getInt(ARG_ID));
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_TITLE, arguments.getString(ARG_TITLE));
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE, arguments.getString(ARG_RELEASE_DATE));
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_POSTER_PATH, arguments.getString(ARG_POSTER_PATH));
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_OVERVIEW, arguments.getString(ARG_OVERVIEW));
            values.put(MovieContract.FavoriteMoviesEntry.COLUMN_RATING, arguments.getFloat(ARG_VOTE_AVERAGE));

            mContentResolver.insert(MovieContract.FavoriteMoviesEntry.CONTENT_URI,
                    values);

            mFavorite.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.btn_star_big_on, 0, 0, 0);
            isFavorited = true;
        }

        //Send Update to Sibling Fragment
        Intent intent = new Intent(Constants.ACTION_FAVORITES_UPDATED);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}
