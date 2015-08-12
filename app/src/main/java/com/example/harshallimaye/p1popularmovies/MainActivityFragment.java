package com.example.harshallimaye.p1popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This is the main activity fragment that fetches a list of 20 movies from the moviedb.
 * Movies are displayed in a grid view and sorted based upon the users preference on settings panel.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    // The GridView by default handles TextView. This is a custom array adapter that display movie poster images.
    private MovieAdapter mMovieAdapter;
    private ArrayList<Movie> mMovieList;

    // member variable stores position of the movie
    private String mSortOrder = null;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "");
        // Get user's sort by preference from Settings.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_order = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_default_sort_key));

        if(savedInstanceState == null) {
            Log.i(LOG_TAG, "New instance created");
            // initialize to default values
            mMovieList = new ArrayList<Movie>();
            mSortOrder = sort_order; //getString(R.string.pref_default_sort_key);
            Log.i(LOG_TAG,"Let's fetch the movie list");
            // fetch movie list
            updateMovies();
        }
        else {
            Log.i(LOG_TAG, "Recreating previous instance, let's restore saved state");
            // movies list retrieved from saved state, no need to fetch it again from movie database.
            mMovieList = savedInstanceState.getParcelableArrayList("movies");
            mSortOrder = savedInstanceState.getString("sortOrder");
            if(!mSortOrder.equals(sort_order)) {
                Log.i(LOG_TAG,"User has changed sort order, let's fetch the movie list");
                mSortOrder = sort_order;
                updateMovies();
            }

        }
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "Saving movies to saved state");
        outState.putParcelableArrayList("movies", mMovieList);
        outState.putString("sortOrder", mSortOrder);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        // The custom array movie adapter is instantiated with a Movies array list.
        mMovieAdapter = new MovieAdapter(getActivity(), mMovieList);

        // Bind custom adapter to the grid view
        GridView gridView = (GridView) rootview.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Obtain the movie that was clicked from the list of movies
                Movie movie = (Movie) parent.getAdapter().getItem(position);

                // Invoke the movie detail activity using explicit intents
                Intent intent = new Intent(getActivity(), MovieDetail_Activity.class);
                // Movie attributes are sent to the detail activity using string array.
                String[] movieDetail = {movie.title, movie.posterPath, movie.overview, Double.toString(movie.rating), movie.release_dt};
                intent.putExtra(Intent.EXTRA_TEXT, movieDetail);
                startActivity(intent);
            }
        });

        return rootview;
    }

    @Override
    public void onResume() {
        Log.i(LOG_TAG, "onResume...");
        // Get user's sort by preference from Settings.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_order = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_default_sort_key));

        if(!mSortOrder.equals(sort_order)) {
            Log.i(LOG_TAG,"User has changed sort order, let's fetch the movie list");
            mSortOrder = sort_order;
            updateMovies();
        }

        super.onResume();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /* This function uses an async task to fetch and update the list of movies

     */
    private void updateMovies() {

        if(!isNetworkAvailable()) {
            Log.i(LOG_TAG, "No internet connection!");
            Toast.makeText(getActivity(), "Oops no internet connection! Please try later", Toast.LENGTH_LONG).show();
            return;
        }

        FetchMoviesTask movieTask = new FetchMoviesTask();

        // Get user's sort by preference from Settings.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_order = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_default_sort_key));
        movieTask.execute(sort_order);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Take the String representing the complete movie list in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         */
        private List<Movie> getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            Log.i(LOG_TAG, "Creating array list of Movie objects from the JSON response");
            // These are the names of the JSON objects that need to be extracted.

            final String MDB_LIST = "results";
            final String MDB_TITLE = "original_title";
            final String MDB_OVERVIEW = "overview";
            final String MDB_RELEASE_DATE = "release_date";
            final String MDB_POSTER = "poster_path";
            final String MDB_POPULARITY = "popularity";
            final String MDB_RATING = "vote_average";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(MDB_LIST);

            Movie[] movies = new Movie[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                // Get the JSON object representing the movie
                JSONObject moviesJSONObj = moviesArray.getJSONObject(i);
                movies[i] = new Movie(
                        moviesJSONObj.getString(MDB_TITLE),
                        moviesJSONObj.getString(MDB_OVERVIEW),
                        moviesJSONObj.getString(MDB_POSTER),
                        moviesJSONObj.getDouble(MDB_POPULARITY),
                        moviesJSONObj.getDouble(MDB_RATING),
                        moviesJSONObj.getString(MDB_RELEASE_DATE)
                );
            }

            Log.i(LOG_TAG, "Number of movies found in the list:" + movies.length);
            mMovieList = new ArrayList<Movie>(Arrays.asList(movies));

            return mMovieList;
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            Log.i(LOG_TAG, "Connecting to the movie db to fetch movies");
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            //if (params.length == 0) {
            //    return null;
            //}

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String sort_by = params[0];

            try {
                // Construct the URL for the Movie DB query
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String APIKEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort_by)
                        .appendQueryParameter(APIKEY_PARAM, getString(R.string.api_key))//api_key)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to the Movie DB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error while calling movie db api ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                Log.v(LOG_TAG, moviesJsonStr);
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movies JSON.
            Log.i(LOG_TAG, "Something went wrong in parsing the JSON response");
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            if (result != null) {
                // first clear the old movies list and then add the new list.
                mMovieAdapter.clear();
                mMovieAdapter.addAll(result);
                // New data is back from the server.  Hooray!
            }
        }
    }
}
