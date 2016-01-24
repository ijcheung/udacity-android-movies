package com.icheung.popularmovies.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icheung.popularmovies.BuildConfig;
import com.icheung.popularmovies.R;
import com.icheung.popularmovies.activity.MainActivity;
import com.icheung.popularmovies.api.MoviesApi;
import com.icheung.popularmovies.fragment.data.MovieDataFragment;
import com.icheung.popularmovies.helper.MovieAdapter;
import com.icheung.popularmovies.model.Movie;
import com.icheung.popularmovies.model.Page;
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

    private MoviesApi mMoviesApi;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movies, container, false);

        mMoviesRecyclerView = (RecyclerView) root.findViewById(R.id.moviesList);

        final GridLayoutManager layoutManager = new GridLayoutManager(mMoviesRecyclerView.getContext(), 3);

        mMoviesRecyclerView.setAdapter(mAdapter);
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
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        mMoviesApi = new Retrofit.Builder()
                .baseUrl(MoviesApi.BASE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MoviesApi.class);

        MainActivity activity = (MainActivity) context;

        mSortByValues = getResources().getStringArray(R.array.sort_by_value);

        //Find Retained Data Fragment
        FragmentManager fm = activity.getSupportFragmentManager();
        mMovieDataFragment = (MovieDataFragment) fm.findFragmentByTag(TAG_MOVIE_DATA_FRAGMENT);

        if (mMovieDataFragment == null) {
            mMovieDataFragment = new MovieDataFragment();
            fm.beginTransaction().add(mMovieDataFragment, TAG_MOVIE_DATA_FRAGMENT).commit();
        }

        mMovies = mMovieDataFragment.getData();
        mAdapter = new MovieAdapter(this, mMovies);

        //Initial Load
        if(mMovies.size() == 0) {
            loadMovies();
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
        if(sortBy != mSortBy){
            mSortBy = sortBy;
            mAdapter.clear();
            loadMovies();
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

    public interface OnFragmentInteractionListener {
        void onMovieSelected(Movie movie);
    }
}
