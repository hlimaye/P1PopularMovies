package com.example.harshallimaye.p1popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;


public class MovieDetail_Activity extends ActionBarActivity {

    private final String LOG_TAG = MovieDetail_Activity.class.getSimpleName();
    private Movie mSelectedMovie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null)
        {

            Bundle arguments = new Bundle();
            Movie movie =  (Movie) getIntent().getExtras().getParcelable("com.example.harshallimaye.p1popularmovies.movie");
            mSelectedMovie = movie;
            arguments.putParcelable(MovieDetail_ActivityFragment.DETAIL_URI, movie);

            MovieDetail_ActivityFragment fragment = new MovieDetail_ActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
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
        } else {
            Log.i(LOG_TAG, "Removing movie from favorite:" + mSelectedMovie.title);
            editor.remove(mSelectedMovie.id);
            editor.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
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
