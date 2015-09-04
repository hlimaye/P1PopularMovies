package com.example.harshallimaye.p1popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by harshallimaye on 8/22/15.
 */
public class TrailerAdapter extends ArrayAdapter<MovieTrailer> {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    public TrailerAdapter(Activity context, List<MovieTrailer> trailers) { super(context, 0, trailers); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current trailer object from the grid and render the view
        final MovieTrailer trailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_trailers_list_item, parent, false);
        }

        ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.trailer_button);
        //imageButton.setImageResource(R.mipmap.ic_play_circle_filled_black_48dp);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), trailer.name + " was clicked", Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "http://www.youtube.com/watch?v=" + trailer.key);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.key));
                v.getContext().startActivity(intent);
            }
        });
        TextView titleView = (TextView) convertView.findViewById(R.id.trailer_title);
        titleView.setText(trailer.name);

        return convertView;
    }
}
