package org.gandji.mymoviedb.services;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.MyMovieDBConfiguration;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.filefinder.VideoFileWorker;
import org.gandji.mymoviedb.gui.MovieGuiService;
import org.gandji.mymoviedb.gui.ScanADirectoryWorker;
import org.gandji.mymoviedb.gui.widgets.PreferencesWindow;
import org.gandji.mymoviedb.javafx.JavaFXPrimaryStage;
import org.gandji.mymoviedb.resources.MovieResource;
import org.gandji.mymoviedb.resources.MovieResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

/**
 * Created by gandji on 08/12/2019.
 */
@Component
@Slf4j
public class MyMovieDBJSCommands {

    @Autowired
    LaunchServices launchServices;

    @Autowired
    MovieGuiService movieGuiService;

    @Autowired
    HibernateMovieDao hibernateMovieDao;

    @Autowired
    MovieResourceAssembler movieResourceAssembler;

    @Autowired
    SpringTemplateEngine pageTemplateResolver;

    @Autowired
    JavaFXPrimaryStage primaryStage;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    PreferencesWindow preferencesWindow;

    @Value("${application.version}")
    String myMovieDBVersionString;

    public String initialPage() {
        List<MovieResource> movies = hibernateMovieDao.findAllByOrderByCreated(0,25)
                .stream()
                .map(hibernateMovieDao::populateMovie)
                .map(movieResourceAssembler::toResource).collect(Collectors.toList());

        Context context = new Context(Locale.FRENCH);
        context.setVariable("movies",movies);
        context.setVariable("mmdb", this);
        context.setVariable("mymoviedb_version", myMovieDBVersionString);

        return pageTemplateResolver.process("mymoviedb_start_page_materialize",context);
    }

    public void internetInfoPage(String movieId) {
        Movie movie = hibernateMovieDao.findOne(Long.parseLong(movieId));
        movieGuiService.openInfoUrl(movie.getInfoUrl());
    }

    public void internetCritics(String movieId) {
        Movie movie = hibernateMovieDao.findOne(Long.parseLong(movieId));
        // this is how you call java script from java
        primaryStage.getWebEngine().executeScript("M.toast({html: 'Displaying critics in browser', classes: 'rounded teal'});");
        movieGuiService.internetCritics(movie);
    }

    public String displayMovie(String movieId) {

        Movie movie = hibernateMovieDao.findOne(Long.parseLong(movieId));
        hibernateMovieDao.populateMovie(movie);
        MovieResource movieResource = movieResourceAssembler.toResource(movie);

        Context context = new Context(Locale.FRENCH);
        context.setVariable("movie",movieResource);

        Set<String> templates = new HashSet<>();
        templates.add("oneMovie");
        String ret = pageTemplateResolver.process("movies", templates, context);
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

    public void playMovie(String id) throws IllegalArgumentException {

        Movie movie = hibernateMovieDao.findOne(Long.parseLong(id));
        log.info("Playing movie "+movie.getTitle());
        movieGuiService.playTheMovie(movie);

    }

    public void preferences() {
        preferencesWindow.configureForStandalone();
        preferencesWindow.setVisible(true);
    }

    public void addAFile() {
        launchServices.addFileInBackground(null, false);
    }

    public void scanADirectory() {
        launchServices.scanADirectoryInBackground();
    }

    public void deleteMovie(String id) {
        Movie movie = hibernateMovieDao.findOne(Long.parseLong(id));
        log.info("Deleting movie "+movie.getTitle());
        launchServices.deleteMovie(movie,null);
    }
}
