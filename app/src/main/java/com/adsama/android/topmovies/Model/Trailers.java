package com.adsama.android.topmovies.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Trailers implements Parcelable {

    @SuppressWarnings("unused")
    public static final String LOG_TAG = Trailers.class.getSimpleName();

    @SerializedName("id")
    private String mId;
    @SerializedName("key")
    private String mKey;
    @SerializedName("name")
    private String mName;
    @SerializedName("site")
    private String mSite;
    @SerializedName("size")
    private String mSize;

    public Trailers(String id, String key, String name, String site, String size) {
        mId = id;
        mKey = key;
        mName = name;
        mSite = site;
        mSize = size;
    }

    public String getTrailerId(){
        return mId;
    }

    private Trailers(){}

    public String getName() {
        return mName;
    }

    public String getKey() {
        return mKey;
    }

    public String getTrailerUrl() {
        return "http://www.youtube.com/watch?v=" + mKey;
    }

    public static final Parcelable.Creator<Trailers> CREATOR = new Creator<Trailers>() {
        public Trailers createFromParcel(Parcel source) {
            Trailers trailer = new Trailers();
            trailer.mId = source.readString();
            trailer.mKey = source.readString();
            trailer.mName = source.readString();
            trailer.mSite = source.readString();
            trailer.mSize = source.readString();
            return trailer;
        }

        public Trailers[] newArray(int size) {
            return new Trailers[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mId);
        parcel.writeString(mKey);
        parcel.writeString(mName);
        parcel.writeString(mSite);
        parcel.writeString(mSize);
    }
}
