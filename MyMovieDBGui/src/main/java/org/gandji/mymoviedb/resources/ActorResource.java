package org.gandji.mymoviedb.resources;

import org.gandji.mymoviedb.data.Actor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import java.util.List;

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
