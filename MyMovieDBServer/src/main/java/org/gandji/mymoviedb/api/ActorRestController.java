package org.gandji.mymoviedb.api;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.data.Actor;
import org.gandji.mymoviedb.data.HibernateActorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gandji on 12/10/2019.
 */
@RestController
@Slf4j
public class ActorRestController {

    @Autowired
    private HibernateActorDao hibernateActorDao;

    @Autowired
    private ActorResourceAssembler actorResourceAssembler;

    @GetMapping(value = "/mymoviedb/api/actors", produces = MediaTypes.HAL_JSON_VALUE)
    ResponseEntity<ActorResources> all() {
        List<ActorResource> actors = hibernateActorDao.findAll(0,20)
                .stream().map(actorResourceAssembler::toResource).collect(Collectors.toList());
        return ResponseEntity.ok().body(new ActorResources(actors,
                ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ActorRestController.class).all()).withSelfRel(),
                ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(MovieRestController.class).all()).withRel("all_movies")));
    }

    @GetMapping(value = "/mymoviedb/api/actors/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    ResponseEntity<ActorResource> oneActor(@PathVariable Long id){
        Actor actor = hibernateActorDao.findOne(id);
        List<Link> movies = new ArrayList<>();
        actor.getMovies().forEach(movie -> {
            movies.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(MovieRestController.class).oneMovie(movie.getId())).withRel(movie.getTitle()));
        });

        return ResponseEntity.ok().body(actorResourceAssembler.toResource(actor));
    }
}
