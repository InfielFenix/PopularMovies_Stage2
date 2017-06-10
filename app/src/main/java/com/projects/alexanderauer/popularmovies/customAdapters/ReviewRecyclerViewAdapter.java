package com.projects.alexanderauer.popularmovies.customAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projects.alexanderauer.popularmovies.R;
import com.projects.alexanderauer.popularmovies.entities.Review;

import java.util.List;

/**
 * Created by Alex on 25.04.2017.
 *
 * Adapter for the review collection in the movie details
 *
 */

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    private List<Review> mReviews;

    public ReviewRecyclerViewAdapter(List<Review> mReviews) {
        this.mReviews = mReviews;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Review mBoundReview;

        public final View mView;
        public final TextView mReviewAuthor;
        public final TextView mReviewContent;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mReviewAuthor = (TextView) itemView.findViewById(R.id.review_author);
            mReviewContent = (TextView) itemView.findViewById(R.id.review_content);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mBoundReview = mReviews.get(position);
        holder.mReviewAuthor.setText(holder.mBoundReview.getAuthor());
        holder.mReviewContent.setText(holder.mBoundReview.getContent());
    }

    @Override
    public int getItemCount() {
        if(mReviews == null)
            return 0;
        else
            return mReviews.size();
    }
}
