package com.example.harshallimaye.p1popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane = false;
    private Movie mSelectedMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetail_ActivityFragment())
                        .commit();
            }
        }
        else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void toggleFavorite(View view) {
        findViewById(R.id.movie_detail_title);



        Gson gson = new Gson();
        String movieStr = gson.toJson(mSelectedMovie);
        //Toast.makeText(getApplication().getApplicationContext(), "Favorite clicked:" + movieStr, Toast.LENGTH_SHORT).show();

        // store in SharedPreferences

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Favorite Movies", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (prefs.getString(mSelectedMovie.id, null) == null) {
            Log.i(LOG_TAG, "Adding movie to favorite:" + mSelectedMovie.title);
            editor.putString(mSelectedMovie.id, movieStr);
            editor.commit();
        }
        else {
            Log.i(LOG_TAG, "Removing movie from favorite:" + mSelectedMovie.title);
            editor.remove(mSelectedMovie.id);
            editor.commit();
        }



        /*MovieProvider provider = new MovieProvider();
        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mSelectedMovie.id);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mSelectedMovie.title);
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,mSelectedMovie.overview );
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mSelectedMovie.posterPath);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, mSelectedMovie.popularity);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING,mSelectedMovie.rating);
        movieValues.put(MovieContract.MovieEntry.COLUMN_REL_DATE, mSelectedMovie.release_dt);

        provider.insert(MovieContract.MovieEntry.buildMoviesUri(Long.parseLong(mSelectedMovie.id)), movieValues);
        */
    }

    @Override
    public void onItemSelected(Movie movie) {
        mSelectedMovie = movie;
        if (mTwoPane)
        // tablet layout
        {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetail_ActivityFragment.DETAIL_URI, movie);

            MovieDetail_ActivityFragment fragment = new MovieDetail_ActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
        // phone layout
        else {
            Intent intent = new Intent(getApplicationContext(), MovieDetail_Activity.class);
            // Movie attributes are sent to the detail activity using string array.
            //String[] movieDetail = {movie.title, movie.posterPath, movie.overview, Double.toString(movie.rating), movie.release_dt, movie.id};

            intent.putExtra("com.example.harshallimaye.p1popularmovies.movie", (Parcelable) movie);
            startActivity(intent);
        }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
