package com.projects.alexanderauer.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.projects.alexanderauer.popularmovies.entities.Movie;

/**
 * Created by Alex on 26.04.2017.
 *
 * Contract for the SQLLite database
 *
 */

public class FavoriteMovieContract {

    public static final String CONTENT_AUTHORITY = "com.projects.alexanderauer.popularmovies.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class FavouriteMovieEntry implements BaseColumns {
        // table name
        public static final String TABLE_FAVOURITE_MOVIES = "favourite_movies";
        // columns
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_USER_RATING = "user_rating";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_FAVOURITE_MOVIES).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVOURITE_MOVIES;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVOURITE_MOVIES;

        // for building URIs on insertion
        public static Uri buildFavouriteMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // transform Cursor into a Movie object
        public static Movie getMovieFromCursor(Cursor cursor){
            Movie movie = new Movie();

            int versionIndex = cursor.getColumnIndex(FavouriteMovieEntry._ID);
            movie.setId(Integer.parseInt(cursor.getString(versionIndex)));

            versionIndex = cursor.getColumnIndex(FavouriteMovieEntry.COLUMN_MOVIE_TITLE);
            movie.setTitle(cursor.getString(versionIndex));

            versionIndex = cursor.getColumnIndex(FavouriteMovieEntry.COLUMN_POSTER_PATH);
            movie.setPosterPath(cursor.getString(versionIndex));

            versionIndex = cursor.getColumnIndex(FavouriteMovieEntry.COLUMN_RELEASE_DATE);
            movie.setReleaseDate(cursor.getString(versionIndex));

            versionIndex = cursor.getColumnIndex(FavouriteMovieEntry.COLUMN_OVERVIEW);
            movie.setOverview(cursor.getString(versionIndex));

            versionIndex = cursor.getColumnIndex(FavouriteMovieEntry.COLUMN_USER_RATING);
            movie.setUserRating(Double.parseDouble(cursor.getString(versionIndex)));

            return movie;
        }
    }
}