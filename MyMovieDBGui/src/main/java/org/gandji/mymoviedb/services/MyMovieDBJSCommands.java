package org.gandji.mymoviedb.services;

import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.gui.MovieGuiService;
import org.gandji.mymoviedb.resources.MovieResource;
import org.gandji.mymoviedb.resources.MovieResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gandji on 08/12/2019.
 */
@Component
@Slf4j
public class MyMovieDBJSCommands {

    @Autowired
    MovieGuiService movieGuiService;

    @Autowired
    HibernateMovieDao hibernateMovieDao;

    @Autowired
    MovieResourceAssembler movieResourceAssembler;

    @Autowired
    SpringTemplateEngine pageTemplateResolver;

    public String initialPage() {
        List<MovieResource> movies = hibernateMovieDao.findAllByOrderByCreated(0,25)
                .stream()
                .map(hibernateMovieDao::populateMovie)
                .map(movieResourceAssembler::toResource).collect(Collectors.toList());

        Context context = new Context(Locale.FRENCH);
        context.setVariable("movies",movies);
        context.setVariable("mmdb", this);

        return pageTemplateResolver.process("mymoviedb_start_page_materialize",context);
    }

    public void internetInfoPage(String movieId) {
        Movie movie = hibernateMovieDao.findOne(Long.parseLong(movieId));
        movieGuiService.openInfoUrl(movie.getInfoUrl());
    }

    public String displayMovie(String movieId) {

        Movie movie = hibernateMovieDao.findOne(Long.parseLong(movieId));
        hibernateMovieDao.populateMovie(movie);
        MovieResource movieResource = movieResourceAssembler.toResource(movie);

        Context context = new Context(Locale.FRENCH);
        context.setVariable("movie",movieResource);

        Set<String> params = new HashSet<>();
        params.add("oneMovie");
        String ret = pageTemplateResolver.process("movies", params, context);
        return ret;
    }

    public String searchKeywords(String query){

                    Iterable<Movie> moviesIterable = hibernateMovieDao.searchInternalAll(query);

                    List<Movie> moviesList = new ArrayList<>();
                    Iterator<Movie> moviesIt = moviesIterable.iterator();
                    while (moviesIt.hasNext()) {
                        moviesList.add(moviesIt.next());
                    }
                    List<MovieResource> movies = moviesList
                            .stream()
                            .map(hibernateMovieDao::populateMovie)
                            .map(movieResourceAssembler::toResource).collect(Collectors.toList());

        Context context = new Context(Locale.FRENCH);
        context.setVariable("movies",movies);
        context.setVariable("mmdb", this);

        Set<String> params = new HashSet<>();
        params.add("moviesCarousel");
        return pageTemplateResolver.process("mymoviedb_start_page_materialize",params,context);

    }

    public void playMovie(String id) {

        Movie movie = hibernateMovieDao.findOne(Long.parseLong(id));
        log.info("Playing movie "+movie.getTitle());
        movieGuiService.playTheMovie(movie);

    }

}
