package com.adsama.android.topmovies.Loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.adsama.android.topmovies.Model.Reviews;
import com.adsama.android.topmovies.Network.ReviewQueryUtils;

import java.util.List;

public class ReviewLoader extends AsyncTaskLoader<List<Reviews>> {

    private String mUrl;

    public ReviewLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Reviews> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<Reviews> reviewsList = ReviewQueryUtils.fetchReviewData(mUrl);
        return reviewsList;
    }
}
