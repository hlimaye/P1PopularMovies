package com.example.harshallimaye.p1popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by harshallimaye on 7/15/15.
 * This is a custom Movie class encapsulating the movie attributes
 * Modified this custom class to implement parcelable, so that it can be saved across activities.
 *
 * 8/21/2015 Added the id attribute to Movie class
 */
public class Movie implements Parcelable{
    String id;
    String title;
    String overview;
    String posterPath;
    double popularity;
    double rating;
    String release_dt;

    public Movie(String vid, String vtitle, String voverview, String vposterPath, double vpopularity, double vrating, String vrel_dt) {
        this.id = vid;
        this.title = vtitle;
        this.overview = voverview;
        this.posterPath = vposterPath;
        this.popularity = vpopularity;
        this.rating = vrating;
        this.release_dt = vrel_dt;
    }

    private Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        popularity = in.readDouble();
        rating = in.readDouble();
        release_dt = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeDouble(popularity);
        dest.writeDouble(rating);
        dest.writeString(release_dt);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
