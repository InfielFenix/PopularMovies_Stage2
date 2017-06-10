package com.projects.alexanderauer.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.projects.alexanderauer.popularmovies.customAdapters.ReviewRecyclerViewAdapter;
import com.projects.alexanderauer.popularmovies.customAdapters.TrailerRecyclerViewAdapter;
import com.projects.alexanderauer.popularmovies.data.FavoriteMovieContract;
import com.projects.alexanderauer.popularmovies.entities.Movie;
import com.projects.alexanderauer.popularmovies.entities.Review;
import com.projects.alexanderauer.popularmovies.entities.Trailer;
import com.projects.alexanderauer.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.projects.alexanderauer.popularmovies.MainActivityFragment.THE_MOVIE_DB_BASIC_URL;
import static com.projects.alexanderauer.popularmovies.MainActivityFragment.THE_MOVIE_DB_PARAM_API_KEY;
import static com.projects.alexanderauer.popularmovies.MainActivityFragment.THE_MOVIE_DB_URL_EXT_REVIEW;
import static com.projects.alexanderauer.popularmovies.MainActivityFragment.THE_MOVIE_DB_URL_EXT_TRAILER;

/**
 * Created by Alex on 12.03.2017.
 *
 * Activity for displaying the details of a movie
 *
 */

public class MovieDetailActivity extends AppCompatActivity {
    private final static String IMAGE_DB_BASE_URL = "http://image.tmdb.org/t/p/",
            IMAGE_SIZE = "w342/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // activate the back arrow in the upper left corner of the app
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(getString(R.string.extra_movie))) {
            // get movie object from extras
            final Movie movie = getIntent().getParcelableExtra(getString(R.string.extra_movie));

            // load trailers and reviews via AsyncTasks
            if (NetworkUtils.isConnected2Internet(this)) {
                new GetMovieTrailers().execute(movie.getId());
                new GetMovieReviews().execute(movie.getId());
            }

            setTitle(movie.getTitle());

            // set title
            TextView tvMovieTitle = (TextView) findViewById(R.id.movie_title);
            tvMovieTitle.setText(movie.getTitle());

            // set the movie poster with the Picasso library
            ImageView ivMoviePoster = (ImageView) findViewById(R.id.movie_poster);
            Picasso.with(this)
                    .load(IMAGE_DB_BASE_URL + IMAGE_SIZE + movie.getPosterPath())
                    .into(ivMoviePoster);

            // set the release year
            TextView tvReleaseDate = (TextView) findViewById(R.id.release_date);
            if (movie.getReleaseDate().contains("-"))
                tvReleaseDate.setText(movie.getReleaseDate().split("-")[0]);
            else
                tvReleaseDate.setText(movie.getReleaseDate());

            // set user rating value with the extension of the maximum
            TextView tvUserRating = (TextView) findViewById(R.id.user_rating);
            tvUserRating.setText(movie.getUserRating() + "/10");

            // image view which serves as a button to mark a movie as favourite
            ImageView ivIsFavourite = (ImageView) findViewById(R.id.is_favourite);
            ivIsFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imageView = (ImageView) view;

                    // insert/delete data into the database via the content resolver
                    Cursor favouriteCursor = getContentResolver().query(FavoriteMovieContract.FavouriteMovieEntry.buildFavouriteMovieUri(movie.getId()), null, null, null, null);
                    if (favouriteCursor.getCount() == 0) {
                        ContentValues values = new ContentValues();

                        values.put(FavoriteMovieContract.FavouriteMovieEntry._ID, movie.getId());
                        values.put(FavoriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
                        values.put(FavoriteMovieContract.FavouriteMovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
                        values.put(FavoriteMovieContract.FavouriteMovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                        values.put(FavoriteMovieContract.FavouriteMovieEntry.COLUMN_RELEASE_DATE,movie.getReleaseDate());
                        values.put(FavoriteMovieContract.FavouriteMovieEntry.COLUMN_USER_RATING,movie.getUserRating());

                        // add movie to favourites
                        getContentResolver().insert(FavoriteMovieContract.FavouriteMovieEntry.CONTENT_URI, values);

                        imageView.setImageResource(R.drawable.ic_favorite_black_24dp);
                    } else {
                        // remove movie from favourites
                        Uri uri = FavoriteMovieContract.FavouriteMovieEntry.buildFavouriteMovieUri(movie.getId());
                        getContentResolver().delete(uri, null, null);

                        imageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                }
            });

            // set correct image resource
            Cursor favouriteCursor = getContentResolver().query(FavoriteMovieContract.FavouriteMovieEntry.buildFavouriteMovieUri(movie.getId()), null, null, null, null);
            if (favouriteCursor.getCount() > 0) {
                ivIsFavourite.setImageResource(R.drawable.ic_favorite_black_24dp);
            }

            // set the plot synopsis
            TextView tvOverview = (TextView) findViewById(R.id.overview);
            tvOverview.setText(movie.getOverview());
        }
    }

    // set trailers
    private void setTrailers(ArrayList<Trailer> trailers) {
        RecyclerView rvTrailers = (RecyclerView) findViewById(R.id.trailers);
        TextView tvNoTrailers = (TextView) findViewById(R.id.no_trailers_text);

        rvTrailers.setLayoutManager(new LinearLayoutManager(rvTrailers.getContext()));

        if (trailers.size() > 1) {
            tvNoTrailers.setVisibility(View.GONE);
            rvTrailers.setAdapter(new TrailerRecyclerViewAdapter(trailers));
        } else
            tvNoTrailers.setVisibility(View.VISIBLE);
    }

    // set reviews
    private void setReviews(ArrayList<Review> reviews) {
        RecyclerView rvReviews = (RecyclerView) findViewById(R.id.reviews);
        TextView tvNoReviews = (TextView) findViewById(R.id.no_reviews_text);

        rvReviews.setLayoutManager(new LinearLayoutManager(rvReviews.getContext()));
        if (reviews.size() > 1) {
            tvNoReviews.setVisibility(View.GONE);
            rvReviews.setAdapter(new ReviewRecyclerViewAdapter(reviews));
        } else
            tvNoReviews.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // trigger the onBackPressed event when back arrow gets pressed
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // asynchronous task which sends a web request and retrieves the movie data from themoviedb
    public class GetMovieTrailers extends AsyncTask<Integer, Integer, ArrayList<Trailer>> {

        @Override
        protected ArrayList<Trailer> doInBackground(Integer... movieIds) {
            if (movieIds.length == 0)
                return null;

            int movieId = movieIds[0];

            // build the correct URI
            Uri builtUri = Uri.parse(THE_MOVIE_DB_BASIC_URL + '/' + movieId + THE_MOVIE_DB_URL_EXT_TRAILER).buildUpon()
                    .appendQueryParameter(THE_MOVIE_DB_PARAM_API_KEY, BuildConfig.THE_MOVIE_DB_API_TOKEN).build();

            HttpURLConnection urlConnection = null;
            try {
                // get URL
                URL url = new URL(builtUri.toString());
                // open connection
                urlConnection = (HttpURLConnection) url.openConnection();

                // create an input stream reader
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                // read the retrieved data with the Gson library which converts and puts it directly
                // into a MovieCollection object
                TrailerCollection trailerCollection = new Gson().fromJson(reader, TrailerCollection.class);

                return trailerCollection.trailers;
            } catch (MalformedURLException e) {
                Log.e("MalformedURLException", e.getMessage());
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }

        // just for the Gson library to easy convert the retrieved data into a set of movie objects
        private class TrailerCollection {
            @SerializedName("results")
            ArrayList<Trailer> trailers;
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailers) {
            super.onPostExecute(trailers);

            // set the trailers as soon as loading is finished
            setTrailers(trailers);
        }
    }

    // asynchronous task which sends a web request and retrieves the movie data from themoviedb
    public class GetMovieReviews extends AsyncTask<Integer, Integer, ArrayList<Review>> {

        @Override
        protected ArrayList<Review> doInBackground(Integer... movieIds) {
            if (movieIds.length == 0)
                return null;

            int movieId = movieIds[0];

            // build the correct URI
            Uri builtUri = Uri.parse(THE_MOVIE_DB_BASIC_URL + '/' + movieId + THE_MOVIE_DB_URL_EXT_REVIEW).buildUpon()
                    .appendQueryParameter(THE_MOVIE_DB_PARAM_API_KEY, BuildConfig.THE_MOVIE_DB_API_TOKEN).build();

            HttpURLConnection urlConnection = null;
            try {
                // get URL
                URL url = new URL(builtUri.toString());
                // open connection
                urlConnection = (HttpURLConnection) url.openConnection();

                // create an input stream reader
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                // read the retrieved data with the Gson library which converts and puts it directly
                // into a MovieCollection object
                ReviewCollection reviewCollection = new Gson().fromJson(reader, ReviewCollection.class);

                return reviewCollection.reviews;
            } catch (MalformedURLException e) {
                Log.e("MalformedURLException", e.getMessage());
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }

        // just for the Gson library to easy convert the retrieved data into a set of movie objects
        private class ReviewCollection {
            @SerializedName("results")
            ArrayList<Review> reviews;
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            super.onPostExecute(reviews);

            // set the trailers as soon as loading is finished
            setReviews(reviews);
        }
    }
}
