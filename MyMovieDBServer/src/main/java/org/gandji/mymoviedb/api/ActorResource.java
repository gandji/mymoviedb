package org.gandji.mymoviedb.api;

import org.gandji.mymoviedb.data.Actor;
import org.gandji.mymoviedb.data.Movie;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import java.util.List;
import java.util.Set;

/**
 * Created by gandji on 25/10/2019.
 */
public class ActorResource extends ResourceSupport {

    Actor actor;

    List<MovieResource> movies;


    public ActorResource() {
        super();
    }

    public ActorResource(Actor actor) {
        super();
        this.actor = actor;
        ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ActorRestController.class).oneActor(actor.getId())).withSelfRel();
        ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ActorRestController.class).all()).withRel("all_actors");
    }

    public ActorResource(Actor actor, Link... links) {
        this(actor);
        add(links);
    }

    public String getName() {
        return actor.getName();
    }

    public void setName(String name) {
    }

    public List<MovieResource> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieResource> movies) {
        this.movies = movies;
    }
}
