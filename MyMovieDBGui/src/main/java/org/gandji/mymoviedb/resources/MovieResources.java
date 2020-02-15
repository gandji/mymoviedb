package org.gandji.mymoviedb.resources;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gandji on 20/10/2019.
 */
public class MovieResources extends Resources<MovieResource> {
    public MovieResources(List<MovieResource> movies) {
        super(movies);
    }
    public MovieResources(List<MovieResource> movies, Link... links) {
        super(movies, Arrays.asList(links));
    }
}
