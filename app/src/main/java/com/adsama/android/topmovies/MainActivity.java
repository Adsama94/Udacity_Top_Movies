package com.adsama.android.topmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adsama.android.topmovies.Adapter.GridAdapter;
import com.adsama.android.topmovies.Loader.MovieLoader;
import com.adsama.android.topmovies.Model.Movies;
import com.adsama.android.topmovies.data.MovieContract;
import com.example.android.topmovies.BuildConfig;
import com.example.android.topmovies.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<List<Movies>>, SharedPreferences.OnSharedPreferenceChangeListener, GridAdapter.Callbacks{

    private static final int MOVIE_LOADER_ID = 1;
    private static final int CURSOR_LOADER_ID = 4;
    private static final String MENU_SELECTED = "selected";
    private int selected = -1;
    private boolean mTwoPane;

    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.moviesGrid)
    GridView mGridView;
    @Bind(R.id.iv_movie_film)
    ImageView mEmptyFavMovieView;

    ConstraintLayout mErrorLayout, mEmptyFavoriteLayout;
    GridAdapter mAdapter;
    ConnectivityManager connManager;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mErrorLayout = (ConstraintLayout) findViewById(R.id.error_layout);
        mErrorLayout.setVisibility(View.GONE);
        mEmptyFavoriteLayout = (ConstraintLayout) findViewById(R.id.empty_fav_container);
        mEmptyFavoriteLayout.setVisibility(View.GONE);
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
        if (savedInstanceState != null) {
            selected = savedInstanceState.getInt(MENU_SELECTED);
            if (selected == R.id.sort_settings) {
                getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
            } else if (selected == R.id.sort_favorites) {
                getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, favoriteLoaderManager);
            }
        }
        mAdapter = new GridAdapter(MainActivity.this, new ArrayList<Movies>(), this);
        mGridView.setAdapter(mAdapter);
        mTwoPane = findViewById(R.id.movie_detail_container) != null;
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movies selectedMovie = (Movies) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_MOVIE, selectedMovie);
                startActivity(intent);

            }
        });
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mErrorLayout.setVisibility(View.VISIBLE);
            Snackbar snackbar = Snackbar.make(mGridView, getString(R.string.check_connection), Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbar.show();
        }
    }

    @Override
    public void openForTab(Movies movie, int position) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.EXTRA_MOVIE, movie);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, fragment).commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailFragment.EXTRA_MOVIE, movie);
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MENU_SELECTED, selected);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter.isEmpty()) {
            mAdapter.getMovies();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sort, menu);
        if (selected == -1) {
            return true;
        }
        switch (selected) {
            case R.id.sort_settings:
                menu.findItem(R.id.sort_settings);
                break;

            case R.id.sort_favorites:
                menu.findItem(R.id.sort_favorites);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            selected = id;
            return true;
        }
        if (id == R.id.sort_favorites) {
            getLoaderManager().initLoader(CURSOR_LOADER_ID, null, favoriteLoaderManager);
            selected = id;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Movies>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = sharedPrefs.getString(getString(R.string.preference_sort_key), getString(R.string.preference_sort_popular));
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority(getString(R.string.base_url));
        uriBuilder.appendPath("3");
        uriBuilder.appendPath("movie");
        uriBuilder.appendPath(sortBy);
        uriBuilder.appendQueryParameter("api_key", BuildConfig.API_KEY);
        return new MovieLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movies>> loader, List<Movies> movies) {
        mProgressBar.setVisibility(View.GONE);
        mAdapter.clear();
        if (movies != null && !movies.isEmpty()) {
            mAdapter.addAll(movies);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movies>> loader) {
        mAdapter.clear();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mAdapter.clear();
        mProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    private LoaderManager.LoaderCallbacks<Cursor> favoriteLoaderManager = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getApplicationContext(), MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.MOVIE_COLUMNS, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.getCount() > 0) {
                mToolbar.setTitle(R.string.favorites);
                mErrorLayout.setVisibility(View.GONE);
                findViewById(R.id.movie_list_container).setVisibility(View.VISIBLE);
                mEmptyFavoriteLayout.setVisibility(View.GONE);
            } else {
                mToolbar.setTitle(R.string.favorites);
                mErrorLayout.setVisibility(View.GONE);
                findViewById(R.id.movie_list_container).setVisibility(View.GONE);
                mEmptyFavoriteLayout.setVisibility(View.VISIBLE);
                mEmptyFavMovieView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });
            }
            mAdapter.add(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.clear();
        }
    };
}