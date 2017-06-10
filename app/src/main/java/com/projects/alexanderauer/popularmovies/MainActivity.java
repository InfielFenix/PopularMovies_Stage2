package com.projects.alexanderauer.popularmovies;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.most_popular_movies));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // get the reference of the main activity fragment
        MainActivityFragment fragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);

        // distinguish between the different sort orders and load the movies
        switch (id) {
            case R.id.sort_most_popular:
                setTitle(getString(R.string.most_popular_movies));
                fragment.loadMovies(MainActivityFragment.THE_MOVIE_DB_URL_EXT_MOST_POPULAR);
                return true;
            case R.id.sort_top_rated:
                setTitle(getString(R.string.top_rated_movies));
                fragment.loadMovies(MainActivityFragment.THE_MOVIE_DB_URL_EXT_TOP_RATED);
                return true;
            case R.id.show_favorites:
                setTitle(getString(R.string.your_favourites));
                fragment.setFavouriteMovies();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
}
