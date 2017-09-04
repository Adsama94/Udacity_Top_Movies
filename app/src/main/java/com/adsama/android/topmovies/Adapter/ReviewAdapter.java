package com.adsama.android.topmovies.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adsama.android.topmovies.Model.Reviews;
import com.example.android.topmovies.R;

import java.util.List;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Reviews> mReviewsList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout reviewLayout;
        TextView authorName;
        TextView authorContent;

        public ViewHolder(View itemView) {
            super(itemView);
            reviewLayout = (LinearLayout) itemView.findViewById(R.id.review_layout);
            authorName = (TextView) itemView.findViewById(R.id.review_author);
            authorContent = (TextView) itemView.findViewById(R.id.review_content);

        }
    }

    public ReviewAdapter(List<Reviews> reviews) {
        this.mReviewsList = reviews;

    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Reviews reviews = mReviewsList.get(position);
        holder.authorName.setText(reviews.getAuthor());
        holder.authorContent.setText(reviews.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewsList.size();
    }

    public void setReviewData(List<Reviews> reviewData){
        mReviewsList = reviewData;
        notifyDataSetChanged();
    }
}

