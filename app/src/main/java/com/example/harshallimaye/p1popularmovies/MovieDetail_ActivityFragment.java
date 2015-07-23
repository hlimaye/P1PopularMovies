package com.example.harshallimaye.p1popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetail_ActivityFragment extends Fragment {

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
            String[] movieDetail =  intent.getStringArrayExtra(Intent.EXTRA_TEXT);

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

        }

        return rootView;
    }
}
