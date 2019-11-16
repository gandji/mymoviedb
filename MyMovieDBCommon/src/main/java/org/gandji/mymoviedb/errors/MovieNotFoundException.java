package org.gandji.mymoviedb.errors;

/**
 * Created by gandji on 12/10/2019.
 */
public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(Long id) {
        super("Cannot find movie "+id);
    }
}
