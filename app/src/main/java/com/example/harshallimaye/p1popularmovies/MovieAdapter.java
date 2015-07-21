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
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);
        //View rootview = LayoutInflater.from(getContext()).inflate(R.layout.grid_view_item_movie, parent, false);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_view_item_movie, parent, false);
        }

        //TextView textView = (TextView) convertView.findViewById(R.id.movie_title);
        //textView.setText(movie.title);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_item_poster);

        String url = "http://image.tmdb.org/t/p/w185" + movie.posterPath;
        Picasso.with(getContext()).load(url).into(imageView);
        //iconView.setImageResource(movie.image);

        return convertView;
    }
}
