package com.icheung.popularmovies.fragment.data;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.icheung.popularmovies.model.Movie;

import java.util.ArrayList;

public class MovieDataFragment extends Fragment {
    private ArrayList<Movie> data;

    public MovieDataFragment(){
        data = new ArrayList<Movie>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setData(ArrayList<Movie> data) {
        this.data = data;
    }

    public ArrayList<Movie> getData() {
        return data;
    }
}
