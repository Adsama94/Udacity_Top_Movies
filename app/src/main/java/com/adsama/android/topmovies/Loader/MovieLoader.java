package com.adsama.android.topmovies.Loader;

import android.content.Context;

import com.adsama.android.topmovies.Model.Movies;
import com.adsama.android.topmovies.Network.MovieQueryUtils;

import java.util.List;

public class MovieLoader extends android.content.AsyncTaskLoader<List<Movies>> {

    private String mUrl;

    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movies> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<Movies> moviesList = MovieQueryUtils.fetchMovieData(mUrl);
        return moviesList;
    }
}