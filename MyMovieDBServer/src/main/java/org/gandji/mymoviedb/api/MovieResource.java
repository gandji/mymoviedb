package org.gandji.mymoviedb.api;

import org.gandji.mymoviedb.data.Actor;
import org.gandji.mymoviedb.data.Genre;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.data.VideoFile;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.Relation;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by gandji on 19/10/2019.
 */
@Relation(collectionRelation = "movies")
public class MovieResource extends /*Resource<Movie>*/ ResourceSupport {

    List<ActorResource> actors;
    List<VideoFileResource> videoFiles;
    Movie movie;

    String title;
    String alternateTitle;
    String posterBytesAsString;
    Set<Genre> genres;
    Set<VideoFile> files;
    String director;
    String year;
    String comments;
    String duree;

    public MovieResource() {
        super();
    }

    public MovieResource(Movie movie, Link... links) {
        this(movie);
        add(links);
    }
    public MovieResource(Movie movie) {
        super();
        this.movie = movie;

        this.title = movie.getTitle();
        this.alternateTitle = movie.getAlternateTitle();
        this.posterBytesAsString = Base64.getEncoder().encodeToString(movie.getPosterBytes());
        this.genres = movie.getGenres();
        this.files = movie.getFiles();
        this.director = movie.getDirector();
        this.year = movie.getYear();
        this.comments = movie.getComments();
        this.duree = movie.getDuree();

        Link toMovies = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(MovieRestController.class).all()).withRel("all_movies");
        Link toSelf = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(MovieRestController.class).oneMovie(movie.getId())).withSelfRel();
        this.add(toMovies,toSelf);
    }

    public Long getDbId() {
        return this.movie.getId();
    }
    public String getTitle() {
            return title;
    }

    public String getAlternateTitle() {
        return alternateTitle;
    }

    public String getPosterAsString() {
        return posterBytesAsString;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public Set<VideoFile> getFiles() {
        return files;
    }

    public String getDirector() {
        return director;
    }

    public String getYear() {
        return movie.getYear();
    }

    public String getSummary() {
        return movie.getSummary();
    }

    public String getDuree() {
        return movie.getDuree();
    }

    public String getComments() {
        return movie.getComments();
    }

    public List<ActorResource> getActors() {
        return this.actors;
    }

    public List<VideoFileResource> getVideoFiles() {
        return videoFiles;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlternateTitle(String alternateTitle) {
        this.alternateTitle = alternateTitle;
    }

    public void setPosterBytesAsString(String posterBytesAsString) {
        this.posterBytesAsString = posterBytesAsString;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public void setFiles(Set<VideoFile> files) {
        this.files = files;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public void setActors(List<ActorResource> actors) {
        this.actors = actors;
    }

    public void setVideoFiles(List<VideoFileResource> videoFiles) {
        this.videoFiles = videoFiles;
    }
}
