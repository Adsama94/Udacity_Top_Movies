package com.adsama.android.topmovies.Loader;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import com.adsama.android.topmovies.Model.Trailers;
import com.adsama.android.topmovies.Network.TrailerQueryUtils;

import java.util.List;

public class TrailerLoader extends AsyncTaskLoader<List<Trailers>> {

    private String mUrl;

    public TrailerLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Trailers> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<Trailers> trailersList = TrailerQueryUtils.fetchTrailerData(mUrl);
        return trailersList;
    }
}
