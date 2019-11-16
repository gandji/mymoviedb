package org.gandji.mymoviedb.api;

import org.gandji.mymoviedb.data.Actor;
import org.gandji.mymoviedb.data.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
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
        ActorResource actorResource = new ActorResource(actor,
                ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ActorRestController.class).oneActor(actor.getId())).withSelfRel(),
                ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ActorRestController.class).all()).withRel("all_actors"));

        List<MovieResource> movies = new ArrayList<>();
        for (Movie movie : actor.getMovies()) {
            movies.add(new MovieResource(movie));
        }
        actorResource.setMovies(movies);
        return actorResource;
    }
}
