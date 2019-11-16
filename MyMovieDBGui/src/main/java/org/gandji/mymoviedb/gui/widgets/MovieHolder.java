package org.gandji.mymoviedb.gui.widgets;

import org.gandji.mymoviedb.data.Movie;

/**
 * Created by gandji on 08/10/2017.
 */
public interface MovieHolder {
    void setData(Movie movie);
    Movie getMovie();
    // REMOVE void close();
}
