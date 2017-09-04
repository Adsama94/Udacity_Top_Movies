package com.adsama.android.topmovies;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adsama.android.topmovies.Adapter.ReviewAdapter;
import com.adsama.android.topmovies.Adapter.TrailerAdapter;
import com.adsama.android.topmovies.Loader.ReviewLoader;
import com.adsama.android.topmovies.Loader.TrailerLoader;
import com.adsama.android.topmovies.Model.Movies;
import com.adsama.android.topmovies.Model.Reviews;
import com.adsama.android.topmovies.Model.Trailers;
import com.adsama.android.topmovies.data.MovieContract;
import com.example.android.topmovies.BuildConfig;
import com.example.android.topmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DetailFragment extends Fragment implements TrailerAdapter.Callbacks {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String EXTRA_MOVIE = "com.example.android.topmovies";

    @Bind(R.id.tv_title)
    TextView mTitleText;
    @Bind(R.id.tv_plot)
    TextView mPlotText;
    @Bind(R.id.tv_date)
    TextView mDateText;
    @Bind(R.id.tv_votes)
    TextView mVoteText;
    @Bind(R.id.iv_poster)
    ImageView mPosterView;
    @Bind(R.id.tv_dateText)
    TextView mReleaseDate;
    @Bind(R.id.tv_voteText)
    TextView mAverageRating;
    @Bind(R.id.reviews_list)
    RecyclerView mReviewsListView;
    @Bind(R.id.trailers_list)
    RecyclerView mTrailersListView;
    @Bind(R.id.iv_share)
    ImageView mShareIcon;
    @Bind(R.id.mark_as_favorite)
    ImageView mFavourite;
    @Bind(R.id.remove_from_favorites)
    ImageView mUnFavourite;

    private static final int REVIEW_LOADER_ID = 2;
    private static final int TRAILER_LOADER_ID = 3;


    Movies mMovies;
    Trailers mTrailers;
    ReviewAdapter mReviewAdapter;
    TrailerAdapter mTrailerAdapter;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(EXTRA_MOVIE)) {
            mMovies = getArguments().getParcelable(EXTRA_MOVIE);
        }
        setHasOptionsMenu(true);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && activity instanceof DetailActivity) {
            appBarLayout.setTitle(mMovies.getMovieTitle());
        }
        getLoaderManager().initLoader(REVIEW_LOADER_ID, null, reviewsLoaderManager);
        getLoaderManager().initLoader(TRAILER_LOADER_ID, null, trailersLoaderManager);
        ImageView movieBackdrop = ((ImageView) activity.findViewById(R.id.movie_backdrop));
        if (movieBackdrop != null) {
            Picasso.with(getContext()).load(getContext().getResources().getString(R.string.backdrop_url) + mMovies.getMovieBackdrop()).config(Bitmap.Config.RGB_565).into(movieBackdrop);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_frag, container, false);
        ButterKnife.bind(this, rootView);
        mTitleText.setText(mMovies.getMovieTitle());
        mPlotText.setText(mMovies.getMovieOverview());
        mDateText.setText(mMovies.getReleaseDate());
        mVoteText.setText(mMovies.getUserRating());
        mReleaseDate.setText(getString(R.string.release_date));
        mAverageRating.setText(getString(R.string.average_rating));
        Picasso.with(getContext()).load(getContext().getResources().getString(R.string.base_image_url) + mMovies.getMoviePoster()).into(mPosterView);
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mTrailersListView.setLayoutManager(trailerLayoutManager);
        mTrailerAdapter = new TrailerAdapter(new ArrayList<Trailers>(), getContext(), this);
        mTrailersListView.setAdapter(mTrailerAdapter);
        final LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mReviewsListView.setLayoutManager(reviewLayoutManager);
        mReviewAdapter = new ReviewAdapter(new ArrayList<Reviews>());
        mReviewsListView.setAdapter(mReviewAdapter);
        mShareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrailers = mTrailerAdapter.getTrailers().get(0);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, mMovies.getMovieTitle());
                sendIntent.putExtra(Intent.EXTRA_TEXT, mTrailers.getName() + ": " + mTrailers.getTrailerUrl());
                startActivity(sendIntent);
            }
        });
        mUnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAsFavorite();
            }
        });
        mFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromFavorites();
            }
        });
        updateFavoriteButtons();
        return rootView;
    }

    private boolean isFavorite() {
        Cursor movieCursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovies.getMovieId(), null, null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }
    }

    public void markAsFavorite() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                            mMovies.getMovieId());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                            mMovies.getMovieTitle());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                            mMovies.getMoviePoster());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
                            mMovies.getMovieBackdrop());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                            mMovies.getMovieOverview());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                            mMovies.getUserRating());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                            mMovies.getReleaseDate());

                    getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateFavoriteButtons();
                Toast.makeText(getContext(), "MOVIE STORED", Toast.LENGTH_SHORT).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void removeFromFavorites() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (isFavorite()) {
                    getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovies.getMovieId(), null);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateFavoriteButtons();
                Toast.makeText(getContext(), "MOVIE REMOVED", Toast.LENGTH_SHORT).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onResume() {
        updateFavoriteButtons();
        super.onResume();
    }

    private void updateFavoriteButtons() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return isFavorite();
            }

            @Override
            protected void onPostExecute(Boolean isFavorite) {
                if (isFavorite) {
                    mUnFavourite.setVisibility(View.GONE);
                    mFavourite.setVisibility(View.VISIBLE);
                } else {
                    mFavourite.setVisibility(View.GONE);
                    mUnFavourite.setVisibility(View.VISIBLE);

                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private android.support.v4.app.LoaderManager.LoaderCallbacks<List<Reviews>> reviewsLoaderManager = new android.support.v4.app.LoaderManager.LoaderCallbacks<List<Reviews>>() {
        @Override
        public android.support.v4.content.Loader<List<Reviews>> onCreateLoader(int id, Bundle args) {
            Long actualId = mMovies.getMovieId();
            String parsingId = actualId.toString();
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("https");
            uriBuilder.authority(getString(R.string.base_url));
            uriBuilder.appendPath("3");
            uriBuilder.appendPath("movie");
            uriBuilder.appendPath(parsingId);
            uriBuilder.appendPath("reviews");
            uriBuilder.appendQueryParameter("api_key", BuildConfig.API_KEY);
            Log.e(LOG_TAG, "FINAL URL IS: " + uriBuilder);
            return new ReviewLoader(getActivity().getBaseContext(), uriBuilder.toString());
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<List<Reviews>> loader, List<Reviews> reviews) {
            if (reviews != null && !reviews.isEmpty()) {
                mReviewAdapter.setReviewData(reviews);
            }
        }

        @Override
        public void onLoaderReset(android.support.v4.content.Loader<List<Reviews>> loader) {
            mReviewAdapter.notifyDataSetChanged();
        }
    };

    private android.support.v4.app.LoaderManager.LoaderCallbacks<List<Trailers>> trailersLoaderManager = new LoaderManager.LoaderCallbacks<List<Trailers>>() {
        @Override
        public Loader<List<Trailers>> onCreateLoader(int id, Bundle args) {
            Long actualId = mMovies.getMovieId();
            String parsingId = actualId.toString();
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("https");
            uriBuilder.authority(getString(R.string.base_url));
            uriBuilder.appendPath("3");
            uriBuilder.appendPath("movie");
            uriBuilder.appendPath(parsingId);
            uriBuilder.appendPath("videos");
            uriBuilder.appendQueryParameter("api_key", BuildConfig.API_KEY);
            Log.e(LOG_TAG, "FINAL URL IS: " + uriBuilder);
            return new TrailerLoader(getActivity().getBaseContext(), uriBuilder.toString());
        }

        @Override
        public void onLoadFinished(Loader<List<Trailers>> loader, List<Trailers> trailers) {
            if (trailers != null && !trailers.isEmpty()) {
                mTrailerAdapter.setTrailerData(trailers);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Trailers>> loader) {
            mTrailerAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void watch(Trailers trailer, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getTrailerUrl())));
    }
}