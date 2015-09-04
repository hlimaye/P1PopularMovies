package com.example.harshallimaye.p1popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by harshallimaye on 8/24/15.
 */
public class ReviewAdapter extends ArrayAdapter<MovieReview> {

    public ReviewAdapter(Activity context, List<MovieReview> reviews) { super(context, 0, reviews);}
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the current trailer object from the grid and render the view
        final MovieReview review = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_reviews_list_item, parent, false);
        }

        TextView authorTxt = (TextView) convertView.findViewById(R.id.author_name);
        authorTxt.setText(review.author);

        TextView content = (TextView) convertView.findViewById(R.id.review_content);
        content.setText(review.content);

        return convertView;
    }
}
