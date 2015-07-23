package com.example.harshallimaye.p1popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by harshallimaye on 7/15/15.
 * Custom Array Adapter to override the default TextView with ImageView
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current movie object from the grid and render the new image view
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_view_item_movie, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_item_poster);

        String url = "http://image.tmdb.org/t/p/w185" + movie.posterPath;

        // Uses the Picasso image library for efficient image caching and rendition.
        Picasso.with(getContext()).load(url).into(imageView);
        return convertView;
    }
}
