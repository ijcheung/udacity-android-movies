package com.icheung.popularmovies.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icheung.popularmovies.BuildConfig;
import com.icheung.popularmovies.R;
import com.icheung.popularmovies.api.MoviesApi;
import com.icheung.popularmovies.data.MovieContract;
import com.icheung.popularmovies.fragment.data.MovieDataFragment;
import com.icheung.popularmovies.helper.MovieAdapter;
import com.icheung.popularmovies.model.Movie;
import com.icheung.popularmovies.model.Page;
import com.icheung.popularmovies.util.Constants;
import com.icheung.popularmovies.util.InfiniteScrollListener;
import com.icheung.popularmovies.util.Utilities;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MoviesFragment extends Fragment implements
        MovieAdapter.OnMovieClickedListener {
    private static final String TAG_MOVIE_DATA_FRAGMENT = "movie_data_fragment";

    private LocalBroadcastManager mLocalBroadcastManager;
    private MoviesApi mMoviesApi;
    private BroadcastReceiver mFavoritesUpdatedReceiver;

    private RecyclerView mMoviesRecyclerView;

    private MovieAdapter mAdapter;
    private ArrayList<Movie> mMovies;
    private MovieDataFragment mMovieDataFragment;

    private int mSortBy;

    private String[] mSortByValues;

    private OnFragmentInteractionListener mListener;

    public MoviesFragment() { }

    public static MoviesFragment newInstance() {
        MoviesFragment fragment = new MoviesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFavoritesUpdatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(mSortBy == 2) {
                    mAdapter.clear();
                    loadFavorites();
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movies, container, false);

        mMoviesRecyclerView = (RecyclerView) root.findViewById(R.id.moviesList);

        final GridLayoutManager layoutManager = new GridLayoutManager(mMoviesRecyclerView.getContext(), 3);

        mMoviesRecyclerView.setLayoutManager(layoutManager);
        mMoviesRecyclerView.addOnScrollListener(new InfiniteScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                loadMovies();
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mLocalBroadcastManager.registerReceiver(mFavoritesUpdatedReceiver,
                new IntentFilter(Constants.ACTION_FAVORITES_UPDATED));

        mMoviesApi = new Retrofit.Builder()
                .baseUrl(MoviesApi.BASE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MoviesApi.class);

        mSortByValues = getResources().getStringArray(R.array.sort_by_value);

        //Find Retained Data Fragment
        FragmentManager fm = getActivity().getSupportFragmentManager();
        mMovieDataFragment = (MovieDataFragment) fm.findFragmentByTag(TAG_MOVIE_DATA_FRAGMENT);

        if (mMovieDataFragment == null) {
            mMovieDataFragment = new MovieDataFragment();
            fm.beginTransaction().add(mMovieDataFragment, TAG_MOVIE_DATA_FRAGMENT).commit();
        }

        mMovies = mMovieDataFragment.getData();
        mAdapter = new MovieAdapter(this, mMovies);
        mMoviesRecyclerView.setAdapter(mAdapter);

        //Initial Load
        if(mMovies.size() == 0) {
            loadMovies();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLocalBroadcastManager.unregisterReceiver(mFavoritesUpdatedReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMovieClicked(int position) {
        mListener.onMovieSelected(mMovies.get(position));
    }

    public void setSortBy(int sortBy){
        if(sortBy != mSortBy) {
            mSortBy = sortBy;
            mAdapter.clear();
            switch (sortBy) {
                //Popularity
                case 0:
                //Rating
                case 1:
                    loadMovies();
                    break;
                //Favorite
                case 2:
                    loadFavorites();
                    break;
            }
        }
    }

    private void loadMovies(){
        if(mAdapter.getCurrentPage() < mAdapter.getTotalPages()) {
            Call<Page> call = mMoviesApi.getMovies(mSortByValues[mSortBy] + ".desc", mAdapter.getCurrentPage() + 1, BuildConfig.MOVIE_DB_API_KEY);
            call.enqueue(new Callback<Page>() {
                @Override
                public void onResponse(Response<Page> response) {
                    Page page = (response.body());
                    mAdapter.loadPage(page);
                }

                @Override
                public void onFailure(Throwable t) {
                    Utilities.showError(getActivity(), R.string.error_load_failed);
                }
            });
        }
    }

    private void loadFavorites(){
        Cursor cursor = getActivity().getContentResolver()
                .query(MovieContract.FavoriteMoviesEntry.CONTENT_URI, null, null, null, null);

        if(cursor.moveToFirst()){
            do{
                Movie movie = new Movie(
                        cursor.getInt(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_POSTER_PATH)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_OVERVIEW)),
                        cursor.getFloat(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_RATING))
                );
                mMovies.add(movie);

                //Disable Auto Loading
                mAdapter.setTotalPages(0);

                mAdapter.notifyItemRangeInserted(0, mMovies.size());
            } while(cursor.moveToNext());
        }
    }

    public interface OnFragmentInteractionListener {
        void onMovieSelected(Movie movie);
    }
}
