package com.projects.alexanderauer.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alex on 26.04.2017.
 *
 * Database helper class
 */

public class FavoriteMoviesDBHelper extends SQLiteOpenHelper {
    //name & version
    private static final String DATABASE_NAME = "favouriteMovies.db";
    private static final int DATABASE_VERSION = 100;

    public FavoriteMoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create the database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                FavoriteMovieContract.FavouriteMovieEntry.TABLE_FAVOURITE_MOVIES + "(" +
                FavoriteMovieContract.FavouriteMovieEntry._ID +  " INTEGER PRIMARY KEY," +
                FavoriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_TITLE + " STRING NOT NULL," +
                FavoriteMovieContract.FavouriteMovieEntry.COLUMN_POSTER_PATH + " STRING," +
                FavoriteMovieContract.FavouriteMovieEntry.COLUMN_OVERVIEW + " STRING," +
                FavoriteMovieContract.FavouriteMovieEntry.COLUMN_RELEASE_DATE + " STRING," +
                FavoriteMovieContract.FavouriteMovieEntry.COLUMN_USER_RATING + " STRING);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    // Upgrade database when version is changed.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieContract.FavouriteMovieEntry.TABLE_FAVOURITE_MOVIES);

        // re-create database
        onCreate(sqLiteDatabase);
    }
}
