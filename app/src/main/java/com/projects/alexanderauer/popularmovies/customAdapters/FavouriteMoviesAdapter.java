package com.projects.alexanderauer.popularmovies.customAdapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.alexanderauer.popularmovies.MovieDetailActivity;
import com.projects.alexanderauer.popularmovies.R;
import com.projects.alexanderauer.popularmovies.data.FavoriteMovieContract;
import com.projects.alexanderauer.popularmovies.entities.Movie;
import com.squareup.picasso.Picasso;

import static com.projects.alexanderauer.popularmovies.customAdapters.MovieGridAdapter.IMAGE_DB_BASE_URL;
import static com.projects.alexanderauer.popularmovies.customAdapters.MovieGridAdapter.IMAGE_SIZE;

/**
 * Created by Alex on 28.04.2017.
 *
 * Adapter for the favourite movies collection
 *
 */

public class FavouriteMoviesAdapter extends CursorAdapter {
    Context mContext;

    public FavouriteMoviesAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);

        mContext = context;
    }

    public static class ViewHolder {
        public final TextView tvMovieTitle;
        public final TextView tvReleaseDate;
        public final ImageView ivMoviePoster;

        public ViewHolder(View view){
            tvMovieTitle = (TextView) view.findViewById(R.id.favourite_movie_title);
            tvReleaseDate = (TextView) view.findViewById(R.id.release_date);
            ivMoviePoster = (ImageView) view.findViewById(R.id.poster);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        int layoutId = R.layout.favourite_movie_item;

        View view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // convert Cursor to a Movie Object
        final Movie movie = FavoriteMovieContract.FavouriteMovieEntry.getMovieFromCursor(cursor);

        // set data to the UI
        viewHolder.tvMovieTitle.setText(movie.getTitle());
        viewHolder.tvReleaseDate.setText(movie.getReleaseDate().split("-")[0]);
        Picasso.with(context)
                .load(IMAGE_DB_BASE_URL + IMAGE_SIZE + movie.getPosterPath())
                .into(viewHolder.ivMoviePoster);

        // set onClickListener to jump to the Movie details as soon user clicks on a movie item
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create intent, put the movie data object into its extras and start the activity
                Intent startMovieDetailActivity = new Intent(context, MovieDetailActivity.class);

                startMovieDetailActivity.putExtra(context.getString(R.string.extra_movie), movie);

                context.startActivity(startMovieDetailActivity);
            }
        });
    }
}
