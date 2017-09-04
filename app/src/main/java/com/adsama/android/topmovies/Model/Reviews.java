package com.adsama.android.topmovies.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Reviews implements Parcelable {

    @SerializedName("id")
    private String mId;
    @SerializedName("author")
    private String mAuthor;
    @SerializedName("content")
    private String mContent;
    @SerializedName("url")
    private String mUrl;

    public Reviews(String id, String author, String content, String url) {
        mId = id;
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    private Reviews(){}

    public String getReviewId(){
        return mId;
    }

    public String getContent() {
        return mContent;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUrl() {
        return mUrl;
    }

    public static final Parcelable.Creator<Reviews> CREATOR = new Creator<Reviews>() {
        public Reviews createFromParcel(Parcel source) {
            Reviews review = new Reviews();
            review.mId = source.readString();
            review.mAuthor = source.readString();
            review.mContent = source.readString();
            review.mUrl = source.readString();
            return review;
        }

        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mId);
        parcel.writeString(mAuthor);
        parcel.writeString(mContent);
        parcel.writeString(mUrl);
    }
}
