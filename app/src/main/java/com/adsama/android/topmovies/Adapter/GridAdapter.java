package com.adsama.android.topmovies.Adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.adsama.android.topmovies.Model.Movies;
import com.adsama.android.topmovies.data.MovieContract;
import com.example.android.topmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridAdapter extends ArrayAdapter<Movies> {

    private Context mContext;
    private ArrayList<Movies> mMovies;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void openForTab(Movies movie, int position);
    }


    public GridAdapter(Activity context, ArrayList<Movies> movies, Callbacks callbacks) {
        super(context, 0, movies);
        mContext = context;
        mMovies = movies;
        mCallbacks = callbacks;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View gridItemView = convertView;
        final Movies movie = mMovies.get(position);
        if (convertView == null) {
            gridItemView = LayoutInflater.from(getContext()).inflate(R.layout.grid_movie_item, parent, false);
        }
        gridItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.openForTab(movie,position);
            }
        });
        Movies currentMovie = getItem(position);
        ImageView thumbView = (ImageView) gridItemView.findViewById(R.id.thumbnail);
        Picasso.with(mContext).load(getContext().getResources().getString(R.string.base_image_url) + currentMovie.getMoviePoster()).config(Bitmap.Config.RGB_565).into(thumbView);
        return gridItemView;
    }

    public void add(Cursor cursor) {
        mMovies.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);
                String title = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);
                String posterPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER_PATH);
                String backdropPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_BACKDROP_PATH);
                String overview = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW);
                String rating = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_VOTE_AVERAGE);
                String releaseDate = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE_DATE);
                Movies movie = new Movies(id, title, posterPath, backdropPath, overview, rating, releaseDate);
                mMovies.add(movie);
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }

    public ArrayList<Movies> getMovies() {
        return mMovies;
    }
}