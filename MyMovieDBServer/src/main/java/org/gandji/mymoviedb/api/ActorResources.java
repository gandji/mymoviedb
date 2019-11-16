package org.gandji.mymoviedb.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gandji on 25/10/2019.
 */
public class ActorResources extends Resources<ActorResource> {
    public ActorResources(List<ActorResource> actors) {
        super(actors);
    }
    public ActorResources(List<ActorResource> actors, Link... links) {
        super(actors, Arrays.asList(links));
    }
}
