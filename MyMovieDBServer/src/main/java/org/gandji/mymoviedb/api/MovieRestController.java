package org.gandji.mymoviedb.api;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.hateoas.core.Relation;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gandji on 21/09/2019.
 */
@RestController()
@Slf4j
public class MovieRestController {

    @Autowired
    private HibernateMovieDao hibernateMovieDao;

    @Autowired
    private MovieResourceAssembler movieResourceAssembler;

    @GetMapping(value = "/mymoviedb/api/movies",produces = MediaTypes.HAL_JSON_VALUE)
    ResponseEntity<MovieResources> all() {
       List<MovieResource> movies = hibernateMovieDao.findAllByOrderByCreated(0,10)
               .stream()
               //.map(movie -> movieResourceAssembler.toResource(movie)).collect(Collectors.toList());
               .map(movieResourceAssembler::toResource).collect(Collectors.toList());
        return ResponseEntity.ok().header("Access-Control-Allow-Origin","*").body(new MovieResources(movies,
                ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(MovieRestController.class).all()).withSelfRel(),
                ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ActorRestController.class).all()).withRel("all_actors")
                ));
    }

    @GetMapping(value = "/mymoviedb/api/movies/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    ResponseEntity<MovieResource> oneMovie(@PathVariable Long id) {
        Movie movie = hibernateMovieDao.findOne(id);
        return ResponseEntity.ok().body(movieResourceAssembler.toResource(movie));
    }

}
