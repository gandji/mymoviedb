package org.gandji.mymoviedb.api;

import org.gandji.mymoviedb.data.Actor;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.data.VideoFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gandji on 18/10/2019.
 */
@Component
public class MovieResourceAssembler implements ResourceAssembler<Movie, MovieResource> {

    @Autowired
    private ActorResourceAssembler actorResourceAssembler;

    @Autowired
    private VideoFileResourceAssembler videoFileResourceAssembler;

    @Override
    public MovieResource toResource(Movie movie) {
        MovieResource movieResource = new MovieResource(movie);

        List<ActorResource> actors = new ArrayList<>();
        for (Actor actor : movie.getActors()) {
            ActorResource actorResource = actorResourceAssembler.toResource(actor);
            actors.add(actorResource);
        }
        movieResource.setActors(actors);

        List<VideoFileResource> videoFiles = new ArrayList<>();
        for (VideoFile videoFile : movie.getFiles()) {
            VideoFileResource videoFileResource = videoFileResourceAssembler.toResource(videoFile);
            videoFiles.add(videoFileResource);
        }
        movieResource.setVideoFiles(videoFiles);
        return movieResource;
    }
}
