package com.projects.alexanderauer.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Alex on 26.04.2017.
 *
 * ContentProvider class
 *
 */

public class FavoriteMoviesProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoriteMoviesDBHelper mOpenHelper;
    private static final int FAVOURITE = 100;
    private static final int FAVOURITE_WITH_ID = 200;

    private static UriMatcher buildUriMatcher(){
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteMovieContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, FavoriteMovieContract.FavouriteMovieEntry.TABLE_FAVOURITE_MOVIES, FAVOURITE);
        matcher.addURI(authority, FavoriteMovieContract.FavouriteMovieEntry.TABLE_FAVOURITE_MOVIES + "/#", FAVOURITE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate(){
        mOpenHelper = new FavoriteMoviesDBHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            // All Flavors selected
            case FAVOURITE:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMovieContract.FavouriteMovieEntry.TABLE_FAVOURITE_MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual flavor based on Id selected
            case FAVOURITE_WITH_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMovieContract.FavouriteMovieEntry.TABLE_FAVOURITE_MOVIES,
                        projection,
                        FavoriteMovieContract.FavouriteMovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default:{
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case FAVOURITE:{
                return FavoriteMovieContract.FavouriteMovieEntry.CONTENT_DIR_TYPE;
            }
            case FAVOURITE_WITH_ID:{
                return FavoriteMovieContract.FavouriteMovieEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case FAVOURITE: {
                long _id = db.insert(FavoriteMovieContract.FavouriteMovieEntry.TABLE_FAVOURITE_MOVIES, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = FavoriteMovieContract.FavouriteMovieEntry.buildFavouriteMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch(match){
            case FAVOURITE:
                numDeleted = db.delete(
                        FavoriteMovieContract.FavouriteMovieEntry.TABLE_FAVOURITE_MOVIES, selection, selectionArgs);
                break;
            case FAVOURITE_WITH_ID:
                numDeleted = db.delete(FavoriteMovieContract.FavouriteMovieEntry.TABLE_FAVOURITE_MOVIES,
                        FavoriteMovieContract.FavouriteMovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        // not needed
        return 0;
    }
}
