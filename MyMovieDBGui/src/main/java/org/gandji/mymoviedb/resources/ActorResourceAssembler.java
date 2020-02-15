package org.gandji.mymoviedb.resources;

import org.gandji.mymoviedb.data.Actor;
import org.gandji.mymoviedb.data.Movie;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gandji on 18/10/2019.
 *
 * TODO also switch actor to actor resource and video file to video file resource
 */
@Component
public class ActorResourceAssembler implements ResourceAssembler<Actor, ActorResource> {

    @Autowired
    MovieResourceAssembler movieResourceAssembler;

    @Override
    public ActorResource toResource(Actor actor) {
        ActorResource actorResource = new ActorResource(actor);

        List<MovieResource> movies = new ArrayList<>();
        try {
            for (Movie movie : actor.getMovies()) {
                movies.add(new MovieResource(movie));
            }
            actorResource.setMovies(movies);
        } catch (LazyInitializationException e) {
            // ok, no movies
        }
        return actorResource;
    }
}
