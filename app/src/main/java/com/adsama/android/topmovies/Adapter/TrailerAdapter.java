package com.adsama.android.topmovies.Adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adsama.android.topmovies.Model.Trailers;
import com.example.android.topmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private List<Trailers> mTrailersList;
    private Context mContext;
    private Callbacks mCallbacks;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout trailerLayout;
        ImageView trailerThumbnail;
        public Trailers mTrailer;

        public ViewHolder(View itemView) {
            super(itemView);
            trailerLayout = (ConstraintLayout) itemView.findViewById(R.id.trailer_layout);
            trailerThumbnail = (ImageView) itemView.findViewById(R.id.trailer_thumbnail);
        }
    }

    public interface Callbacks {
        void watch(Trailers trailer, int position);
    }

    public TrailerAdapter(List<Trailers> trailers, Context context,Callbacks callbacks) {
        this.mTrailersList = trailers;
        this.mContext = context;
        this.mCallbacks = callbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Trailers currentTrailer = mTrailersList.get(position);
        String thumbnailUrl = mContext.getString(R.string.trailer_thumbnail_url) + currentTrailer.getKey() + "/0.jpg";
        Picasso.with(mContext).load(thumbnailUrl).into(holder.trailerThumbnail);
        holder.mTrailer = currentTrailer;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.watch(currentTrailer, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailersList.size();
    }

    public void setTrailerData(List<Trailers> trailerData) {
        mTrailersList = trailerData;
        notifyDataSetChanged();
    }

    public List<Trailers> getTrailers() {
        return mTrailersList;
    }
}