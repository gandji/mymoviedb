package org.gandji.mymoviedb.data.repositories;

/**
 * Created by gandji on 06/05/2019.
 */
public class MovieCountPerAttribute {
    long nMovies;
    String name;

    public MovieCountPerAttribute(String name, long nMovies) {
        this.name = name;
        this.nMovies = nMovies;
    }

    public long getnMovies() {
        return nMovies;
    }

    public void setnMovies(long nMovies) {
        this.nMovies = nMovies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
