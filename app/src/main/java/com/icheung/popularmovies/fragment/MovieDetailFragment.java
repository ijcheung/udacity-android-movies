package com.icheung.popularmovies.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icheung.popularmovies.BuildConfig;
import com.icheung.popularmovies.R;
import com.icheung.popularmovies.api.MoviesApi;
import com.icheung.popularmovies.data.MovieContract;
import com.icheung.popularmovies.model.Movie;
import com.icheung.popularmovies.model.Review;
import com.icheung.popularmovies.model.ReviewWrapper;
import com.icheung.popularmovies.model.Trailer;
import com.icheung.popularmovies.model.TrailerWrapper;
import com.icheung.popularmovies.util.Constants;
import com.icheung.popularmovies.util.Utilities;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MovieDetailFragment extends Fragment implements Button.OnClickListener {
    private static final String ARG_ID           = "id";
    private static final String ARG_TITLE        = "title";
    private static final String ARG_RELEASE_DATE = "release_date";
    private static final String ARG_POSTER_PATH  = "poster_path";
    private static final String ARG_OVERVIEW     = "overview";
    private static final String ARG_VOTE_AVERAGE = "vote_average";

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    private static final String YOUTUBE_BASE_THUMB_URL = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_THUMB_SIZE_DEFAULT = "/hqdefault.jpg";

    private final MoviesApi mMoviesApi;
    private ContentResolver mContentResolver;

    private CollapsingToolbarLayout mCollapsingToolbar;
    private Toolbar mToolbar;
    private ImageView mPoster;
    private TextView mReleaseDate;
    private TextView mRating;
    private Button mFavorite;
    private TextView mSummary;
    private LinearLayout mTrailers;
    private LinearLayout mReviews;

    private boolean isFavorited = false;
    private String firstTrailer;

    public MovieDetailFragment() {
        mMoviesApi = new Retrofit.Builder()
                .baseUrl(MoviesApi.BASE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MoviesApi.class);
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

        mCollapsingToolbar = (CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar);
        mToolbar = (Toolbar) root.findViewById(R.id.toolbar);
        mPoster = (ImageView) root.findViewById(R.id.poster);
        mReleaseDate = (TextView) root.findViewById(R.id.release_date);
        mRating = (TextView) root.findViewById(R.id.rating);
        mFavorite = (Button) root.findViewById(R.id.favorite);
        mSummary = (TextView) root.findViewById(R.id.summary);

        mTrailers = (LinearLayout) root.findViewById(R.id.trailers);
        mReviews = (LinearLayout) root.findViewById(R.id.reviews);

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

        if(cursor != null && cursor.moveToFirst()){
            mFavorite.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.btn_star_big_on, 0, 0, 0);
            isFavorited = true;
            cursor.close();
        }

        mFavorite.setOnClickListener(this);

        Picasso.with(getActivity())
                .load(Constants.POSTER_BASE_URL + Constants.POSTER_SIZE_780 + getArguments().getString(ARG_POSTER_PATH))
                .into(mPoster);

        mCollapsingToolbar.setTitle(getArguments().getString(ARG_TITLE));
        mReleaseDate.setText(getArguments().getString(ARG_RELEASE_DATE));
        mRating.setText(getArguments().getFloat(ARG_VOTE_AVERAGE) + "/10.0");
        mSummary.setText(getArguments().getString(ARG_OVERVIEW));

        mToolbar.inflateMenu(R.menu.menu_movie_detail);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.action_share):
                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_SUBJECT, getArguments().getString(ARG_TITLE));
                        share.putExtra(Intent.EXTRA_TEXT, YOUTUBE_BASE_URL + firstTrailer);
                        startActivity(Intent.createChooser(share, getString(R.string.action_share_trailer)));
                        return true;
                }
                return false;
            }
        });

        loadTrailers();
        loadReviews();
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

    private void loadTrailers() {
        Call<TrailerWrapper> call = mMoviesApi.getTrailers(getArguments().getInt(ARG_ID), BuildConfig.MOVIE_DB_API_KEY);
        call.enqueue(new Callback<TrailerWrapper>() {
            @Override
            public void onResponse(Response<TrailerWrapper> response) {
                List<Trailer> trailers = (response.body()).getTrailers();

                if(trailers.size() > 0) {
                    firstTrailer = trailers.get(0).getSource();
                    mToolbar.getMenu().getItem(0).setVisible(true);

                    for(final Trailer trailer : trailers) {
                        View trailerRoot = getActivity().getLayoutInflater().inflate(R.layout.trailer, mTrailers, false);

                        ImageView thumb = (ImageView) trailerRoot.findViewById(R.id.thumb);
                        thumb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + trailer.getSource())));
                            }
                        });
                        mTrailers.addView(trailerRoot);
                        Picasso.with(getContext())
                                .load(YOUTUBE_BASE_THUMB_URL + trailer.getSource() + YOUTUBE_THUMB_SIZE_DEFAULT)
                                .into(thumb);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utilities.showError(getActivity(), R.string.error_load_failed);
            }
        });
    }

    private void loadReviews() {
        Call<ReviewWrapper> call = mMoviesApi.getReviews(getArguments().getInt(ARG_ID), BuildConfig.MOVIE_DB_API_KEY);
        call.enqueue(new Callback<ReviewWrapper>() {
            @Override
            public void onResponse(Response<ReviewWrapper> response) {
                List<Review> reviews = (response.body()).getReviews();

                for(int i = 0; i < 3 && i < reviews.size(); i++) {
                    View reviewRoot = getActivity().getLayoutInflater().inflate(R.layout.review, mReviews, false);

                    TextView text = (TextView) reviewRoot.findViewById(R.id.text);
                    text.setText(reviews.get(i).getContent());

                    mReviews.addView(reviewRoot);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utilities.showError(getActivity(), R.string.error_load_failed);
            }
        });
    }
}
