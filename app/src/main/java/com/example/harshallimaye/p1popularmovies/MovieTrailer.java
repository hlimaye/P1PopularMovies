package com.example.harshallimaye.p1popularmovies;

/**
 * Created by harshallimaye on 8/21/15.
 */
public class MovieTrailer {
    String movieID;
    String name;
    String site;
    String size;
    String type;
    String key;
    String id;

    public MovieTrailer(String vmovieID, String vname, String vsite, String vsize, String vtype, String vkey, String vid) {
        this.movieID = vmovieID;
        this.name = vname;
        this.site = vsite;
        this.size = vsize;
        this.type = vtype;
        this.key = vkey;
        this.id = vid;
    }
}
