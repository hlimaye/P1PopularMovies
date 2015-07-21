package com.example.harshallimaye.p1popularmovies;

/**
 * Created by harshallimaye on 7/15/15.
 */
public class Movie {
    String title;
    String overview;
    String posterPath;
    double popularity;
    double rating;
    String release_dt;

    public Movie(String vtitle, String voverview, String vposterPath, double vpopularity, double vrating, String vrel_dt) {
        this.title = vtitle;
        this.overview = voverview;
        this.posterPath = vposterPath;
        this.popularity = vpopularity;
        this.rating = vrating;
        this.release_dt = vrel_dt;
    }
}
