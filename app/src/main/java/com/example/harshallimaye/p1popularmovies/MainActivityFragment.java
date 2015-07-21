package com.example.harshallimaye.p1popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
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
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieAdapter movieAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            //Toast.makeText(getActivity(), "Update Movies", Toast.LENGTH_SHORT);
            updateMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>()); //Arrays.asList(movies)); ////
        GridView gridView = (GridView) rootview.findViewById(R.id.gridview_movies);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getAdapter().getItem(position);

                Intent intent = new Intent(getActivity(), MovieDetail_Activity.class);
                String[] movieDetail = {movie.title, movie.posterPath, movie.overview, Double.toString(movie.rating), movie.release_dt};
                intent.putExtra(Intent.EXTRA_TEXT, movieDetail);
                startActivity(intent);
                //Toast.makeText(getActivity(), "This will launch moview detail", Toast.LENGTH_SHORT).show();
            }
        });

        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        FetchMoviesTask movieTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_order = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_default_sort_key));
        movieTask.execute(sort_order);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
        /*private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }*/

        /**
         * Prepare the weather high/lows for presentation.
         */
        /*private String formatHighLows(double high, double low, String unitType) {

            if (unitType.equals(getString(R.string.pref_units_imperial))) {
                high = (high * 1.8) + 32;
                low = (low * 1.8) + 32;
            } else if (!unitType.equals(getString(R.string.pref_units_metric))) {
                Log.d(LOG_TAG, "Unit type not found: " + unitType);
            }

            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }*/

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private List<Movie> getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {


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

            /*SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPrefs.getString(
                    getString(R.string.pref_units_key),
                    getString(R.string.pref_units_metric));
            */
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

            return Arrays.asList(movies); //resultStrs;

        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            Log.i(LOG_TAG, "Inside background task");
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
            String api_key = "1c73f86b0ed8d262c5f7c7e5ebf43575";

            try {
                // Construct the URL for the Movie DB query
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String APIKEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort_by)
                        .appendQueryParameter(APIKEY_PARAM, api_key)
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
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
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
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            if (result != null) {
                movieAdapter.clear();
                movieAdapter.addAll(result);
                //for (Movie movie : result) {
                //    movieAdapter.add(movie);
                //}
                // New data is back from the server.  Hooray!
            }
        }
    }
}
