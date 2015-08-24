package com.example.harshallimaye.p1popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
public class MovieDetail_ActivityFragment extends Fragment {

    private final String LOG_TAG = MovieDetail_ActivityFragment.class.getSimpleName();

    private ArrayList<MovieTrailer> mTrailerList;
    private TrailerAdapter mTrailerAdapter;
    //private String mMovieID;

    public MovieDetail_ActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // The detail Activity called via intent.  Inspect the intent for movie attributes passed as string array.
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {

            // Movie attributes are in this string array in this sequence:
            // Original title, Poster Path, Synopsis, Rating, Release Date
            String[] movieDetail = intent.getStringArrayExtra(Intent.EXTRA_TEXT);

            TextView title = (TextView) rootView.findViewById(R.id.movie_detail_title);
            title.setText(movieDetail[0]);

            ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_detail_poster);

            String url = "http://image.tmdb.org/t/p/w185" + movieDetail[1];
            Picasso.with(getActivity()).load(url).into(imageView);

            TextView overview = (TextView) rootView.findViewById(R.id.movie_detail_overview);
            overview.setText(movieDetail[2]);

            TextView rating = (TextView) rootView.findViewById(R.id.movie_detail_rating);
            rating.setText(movieDetail[3]);

            TextView date = (TextView) rootView.findViewById(R.id.movie_detail_release_dt);
            date.setText(movieDetail[4]);

            String movieID = movieDetail[5];


            mTrailerList = new ArrayList<>();
            mTrailerAdapter = new TrailerAdapter(getActivity(), mTrailerList);
            ListView listView = (ListView) rootView.findViewById(R.id.movie_trailers_list_view);
            listView.setAdapter(mTrailerAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //    Toast.makeText(getActivity(), "Trailer was clicked", Toast.LENGTH_SHORT).show();
                }
            });

            updateTrailers(movieID);
        }
        return rootView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void updateTrailers(String movieID) {
        if(!isNetworkAvailable()) {
            Log.i(LOG_TAG, "No internet connection!");
            Toast.makeText(getActivity(), "Oops no internet connection! Please try later", Toast.LENGTH_LONG).show();
            return;
        }

        FetchMovieTrailers trailerTask = new FetchMovieTrailers();

        // Get user's sort by preference from Settings.
        trailerTask.execute(movieID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        updateTrailers(mMovieID);
        super.onCreate(savedInstanceState);
    }

    public class FetchMovieTrailers extends AsyncTask<String, Void, List<MovieTrailer>> {
        private final String LOG_TAG = FetchMovieTrailers.class.getSimpleName();


        @Override
        protected List<MovieTrailer> doInBackground(String... params) {
            Log.i(LOG_TAG, "Connecting to the movie db to fetch movie trailers");

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String trailersJsonStr = null;

            String movieID = params[0];

            try {
                // Construct the URL for the Movie DB query
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + movieID + "/videos";
                final String APIKEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(APIKEY_PARAM, getString(R.string.api_key))//api_key)
                        .build();

                Log.i(LOG_TAG, "URL to fetch trailers:" + builtUri.toString());

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
                trailersJsonStr = buffer.toString();
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
                Log.v(LOG_TAG, trailersJsonStr);
                return getMovieTrailersFromJson(trailersJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            // This will only happen if there was an error getting or parsing the movies JSON.
            Log.i(LOG_TAG, "Something went wrong in parsing the JSON response");
            return null;
        }

        private List<MovieTrailer> getMovieTrailersFromJson(String trailersJsonStr)
                throws JSONException {

            Log.i(LOG_TAG, "Creating array list of MovieTrailer objects from the JSON response");
            // These are the names of the JSON objects that need to be extracted.

            final String MDB_LIST = "results";
            final String MDB_ID = "id";
            final String MDB_KEY = "key";
            final String MDB_NAME = "name";
            final String MDB_SITE = "site";
            final String MDB_SIZE = "size";
            final String MDB_TYPE = "type";

            JSONObject trailersJson = new JSONObject(trailersJsonStr);
            JSONArray trailersArray = trailersJson.getJSONArray(MDB_LIST);

            MovieTrailer[] trailers = new MovieTrailer[trailersArray.length()];

            for (int i = 0; i < trailersArray.length(); i++) {
                // Get the JSON object representing the trailer
                JSONObject trailerJSONObj = trailersArray.getJSONObject(i);
                trailers[i] = new MovieTrailer(
                        "movie id",
                        trailerJSONObj.getString(MDB_NAME),
                        trailerJSONObj.getString(MDB_SITE),
                        trailerJSONObj.getString(MDB_SIZE),
                        trailerJSONObj.getString(MDB_TYPE),
                        trailerJSONObj.getString(MDB_KEY),
                        trailerJSONObj.getString(MDB_ID)
                );
            }

            Log.i(LOG_TAG, "Number of trailers found in the list:" + trailers.length);
            mTrailerList = new ArrayList<MovieTrailer>(Arrays.asList(trailers));

            return mTrailerList;
        }


        @Override
        protected void onPostExecute(List<MovieTrailer> result) {

            if (result != null) {
                // first clear the old movies list and then add the new list.
                mTrailerAdapter.clear();
                mTrailerAdapter.addAll(result);
                // New data is back from the server.  Hooray!
            }

           }
    }
}
