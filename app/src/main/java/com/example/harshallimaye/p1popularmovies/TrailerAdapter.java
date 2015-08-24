package com.example.harshallimaye.p1popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by harshallimaye on 8/22/15.
 */
public class TrailerAdapter extends ArrayAdapter<MovieTrailer> {

    public TrailerAdapter(Activity context, List<MovieTrailer> trailers) { super(context, 0, trailers); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current movie object from the grid and render the new image view
        final MovieTrailer trailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_trailers_list_item, parent, false);
        }

        ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.trailer_button);
        //imageButton.setImageResource(R.mipmap.ic_play_circle_filled_black_48dp);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), trailer.name + " was clicked", Toast.LENGTH_SHORT).show();
            }
        });
        TextView titleView = (TextView) convertView.findViewById(R.id.trailer_title);
        titleView.setText(trailer.name);

        return convertView;
    }
}
