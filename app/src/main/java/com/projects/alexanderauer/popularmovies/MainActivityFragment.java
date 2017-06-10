package com.projects.alexanderauer.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.projects.alexanderauer.popularmovies.customAdapters.FavouriteMoviesAdapter;
import com.projects.alexanderauer.popularmovies.customAdapters.MovieGridAdapter;
import com.projects.alexanderauer.popularmovies.data.FavoriteMovieContract;
import com.projects.alexanderauer.popularmovies.entities.Movie;
import com.projects.alexanderauer.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Alex on 09.03.2017.
 * <p>
 * The main fragment that contains the grid view with the movie collection
 */

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {
    final static String THE_MOVIE_DB_BASIC_URL = "https://api.themoviedb.org/3/movie",
            THE_MOVIE_DB_PARAM_API_KEY = "api_key",
            THE_MOVIE_DB_URL_EXT_MOST_POPULAR = "/popular",
            THE_MOVIE_DB_URL_EXT_TOP_RATED = "/top_rated",
            THE_MOVIE_DB_URL_EXT_TRAILER = "/videos",
            THE_MOVIE_DB_URL_EXT_REVIEW = "/reviews",
            SORT_TYPE_FAVOURITE_MOVIES = "sortTypeFavouriteMovies";

    private static final int MOVIE_LOADER_ID = 0;

    public String currentSortType = THE_MOVIE_DB_URL_EXT_MOST_POPULAR;

    private GridView gridView;
    private TextView tvNoDataText;

    private ArrayList<Movie> movieBuffer;

    public MainActivityFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // init loader which loads the movies from TheMovieDb
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load data of the previous session, if available
        if (savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.extra_movies)))
            loadMovies(currentSortType);
        else {
            movieBuffer = savedInstanceState.getParcelableArrayList(getString(R.string.extra_movies));
            currentSortType = savedInstanceState.getString(getString(R.string.extra_sort_type));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the fragment_main layout
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // get and lay down the grid view
        gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        tvNoDataText = (TextView) rootView.findViewById(R.id.no_data_text);

        // set movies or the list of favourites
        switch (currentSortType) {
            case THE_MOVIE_DB_URL_EXT_MOST_POPULAR:
            case THE_MOVIE_DB_URL_EXT_TOP_RATED:
                setMovies(movieBuffer);
                break;
            case SORT_TYPE_FAVOURITE_MOVIES:
                setFavouriteMovies();
                break;
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // store data
        outState.putParcelableArrayList(getString(R.string.extra_movies), movieBuffer);
        outState.putString(getString(R.string.extra_sort_type), currentSortType);

        super.onSaveInstanceState(outState);
    }

    public void loadMovies(String sortType) {
        // check if the new sort type is not the same as the current one
        if (sortType != this.currentSortType || movieBuffer == null) {
            // set the new sort type as the current one
            this.currentSortType = sortType;

            // check the internet connection, otherwise the app would crash by doing the
            // web request
            if (NetworkUtils.isConnected2Internet(getActivity()))
                // load the movies via the loader manager
                getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            else
                Toast.makeText(getActivity().getApplicationContext(), R.string.no_internet_conn, Toast.LENGTH_LONG).show();
        }
    }

    public void setMovies(ArrayList<Movie> movies) {
        // buffer the movies in a local array list
        movieBuffer = movies;

        // load the data into the grid view
        if (movieBuffer != null && movieBuffer.size() > 0) {
            gridView.setNumColumns(2);
            tvNoDataText.setVisibility(View.GONE);
            gridView.setAdapter(new MovieGridAdapter(getActivity(), movies));
        }
    }

    public void setFavouriteMovies() {
        // get favourites from SQLLite Database
        Cursor favouriteMovies = getActivity().getContentResolver().query(FavoriteMovieContract.FavouriteMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        this.currentSortType = SORT_TYPE_FAVOURITE_MOVIES;
        gridView.setNumColumns(1);

        // set favourites or no data text
        if(favouriteMovies.getCount() > 0){
            tvNoDataText.setVisibility(View.GONE);
        }else
            tvNoDataText.setVisibility(View.VISIBLE);

        gridView.setAdapter(new FavouriteMoviesAdapter(this.getActivity(), favouriteMovies, false));
    }

    private class MovieCollection {
        @SerializedName("results")
        ArrayList<Movie> movies;
    }

    // loader which sends a web request and retrieves the movie data from themoviedb
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(getActivity()) {

            // Initialize a ArrayList of Movie objects, this will hold all the data
            String loadedSortType;

            @Override
            protected void onStartLoading() {
                if (movieBuffer != null && currentSortType.equals(loadedSortType)) {
                    // Delivers any previously loaded movies data immediately
                    deliverResult(movieBuffer);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                // build the correct URI
                Uri builtUri = Uri.parse(THE_MOVIE_DB_BASIC_URL + currentSortType).buildUpon()
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
                    MovieCollection movieCollection = new Gson().fromJson(reader, MovieCollection.class);

                    // buffer data
                    movieBuffer = movieCollection.movies;
                    loadedSortType = currentSortType;

                    return movieCollection.movies;
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

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(ArrayList<Movie> data) {
                movieBuffer = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        // set the movies as soon as loading is finished
        if(currentSortType != SORT_TYPE_FAVOURITE_MOVIES)
            setMovies(movies);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        // not needed
    }
}
