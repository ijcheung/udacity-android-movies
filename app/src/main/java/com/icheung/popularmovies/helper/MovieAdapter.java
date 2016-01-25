package com.icheung.popularmovies.helper;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.icheung.popularmovies.R;
import com.icheung.popularmovies.model.Movie;
import com.icheung.popularmovies.model.MovieWrapper;
import com.icheung.popularmovies.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private final OnMovieClickedListener mListener;
    private final ArrayList<Movie> mMovies;
    private int currentPage = 0;
    private int totalPages = 1;
    
    public MovieAdapter(OnMovieClickedListener listener, ArrayList<Movie> movies){
        mListener = listener;
        mMovies = movies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Movie movie = mMovies.get(position);

        Log.i("Movie", movie.toString());

        Picasso.with(holder.poster.getContext())
                .load(Constants.POSTER_BASE_URL + Constants.POSTER_SIZE_342 + movie.getPosterPath())
                .placeholder(R.drawable.placeholder)
                .into(holder.poster);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMovieClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void loadPage(MovieWrapper page) {
        currentPage = page.getPage();
        totalPages = page.getTotalPages();
        mMovies.addAll(page.getMovies());
        notifyItemRangeChanged(mMovies.size() - page.getMovies().size(), page.getMovies().size());
    }

    public void clear() {
        int num = mMovies.size();
        mMovies.clear();
        notifyItemRangeRemoved(0, num);
        currentPage = 0;
        totalPages = 1;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public interface OnMovieClickedListener {
        void onMovieClicked(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView poster;

        public ViewHolder(View view) {
            super(view);

            this.view = view;
            this.poster = (ImageView) view.findViewById(R.id.poster);
        }
    }
}
