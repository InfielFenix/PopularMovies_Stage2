package com.projects.alexanderauer.popularmovies.customAdapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projects.alexanderauer.popularmovies.R;
import com.projects.alexanderauer.popularmovies.entities.Trailer;

import java.util.List;

/**
 * Created by Alex on 25.04.2017.
 *
 * Adapter for the trailer collection in the movie details
 *
 */

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.ViewHolder> {

    private List<Trailer> mTrailers;

    public TrailerRecyclerViewAdapter(List<Trailer> mTrailers) {
        this.mTrailers = mTrailers;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Trailer mBoundTrailer;

        public final View mView;
        public final TextView mTrailerTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mTrailerTitle = (TextView) itemView.findViewById(R.id.trailer_title);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mBoundTrailer = mTrailers.get(position);
        holder.mTrailerTitle.setText(holder.mBoundTrailer.getName());

        final String trailerKey = holder.mBoundTrailer.getKey();

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent videoPlayerAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerKey));
                Intent videoPlayerWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerKey));

                try {
                    context.startActivity(videoPlayerAppIntent);
                } catch (ActivityNotFoundException ex) {
                    context.startActivity(videoPlayerWebIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mTrailers == null)
            return 0;
        else
            return mTrailers.size();
    }
}
