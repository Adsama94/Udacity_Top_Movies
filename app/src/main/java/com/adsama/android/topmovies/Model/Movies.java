package com.adsama.android.topmovies.Model;


import android.os.Parcel;
import android.os.Parcelable;

public class Movies implements Parcelable {

    private String mMovieTitle;
    private String mMoviePoster;
    private String mMovieBackdrop;
    private String mMovieOverview;
    private String mUserRating;
    private String mReleaseDate;
    private long mMovieId;


    public Movies() {
    }

    public Movies(long movieId,String title, String poster, String backdrop, String overview, String userRating, String releaseDate) {
        mMovieId = movieId;
        mMovieTitle = title;
        mMoviePoster = poster;
        mMovieBackdrop = backdrop;
        mMovieOverview = overview;
        mUserRating = userRating;
        mReleaseDate = releaseDate;
    }

    public long getMovieId() {
        return mMovieId;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public String getMoviePoster() {
        return mMoviePoster;
    }

    public String getMovieBackdrop() {
        return mMovieBackdrop;
    }

    public String getMovieOverview() {
        return mMovieOverview;
    }

    public String getUserRating() {
        return mUserRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
        public Movies createFromParcel(Parcel source) {
            Movies movie = new Movies();
            movie.mMovieId = source.readLong();
            movie.mMovieTitle = source.readString();
            movie.mMoviePoster = source.readString();
            movie.mMovieBackdrop = source.readString();
            movie.mMovieOverview = source.readString();
            movie.mUserRating = source.readString();
            movie.mReleaseDate = source.readString();

            return movie;
        }

        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mMovieId);
        parcel.writeString(mMovieTitle);
        parcel.writeString(mMoviePoster);
        parcel.writeString(mMovieBackdrop);
        parcel.writeString(mMovieOverview);
        parcel.writeString(mUserRating);
        parcel.writeString(mReleaseDate);

    }
}