package org.gandji.mymoviedb.services;

import org.gandji.mymoviedb.data.*;
import org.gandji.mymoviedb.data.repositories.MovieRepository;
import org.gandji.mymoviedb.filefinder.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by gandji on 21/09/2019.
 */
@Component
public class MovieDaoServices {

    @Autowired
    HibernateActorDao hibernateActorDao;

    @Autowired
    HibernateVideoFileDao hibernateVideoFileDao;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Autowired
    HibernateMovieDao hibernateMovieDao;

    public Movie checkActorsAndSaveMovie(Movie movie) {
        // TODO: configure unicity of actors, for now we enforce unicity of actors by hand, grrrr.....
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Set<Actor> actors;
        if (movie.getId() == null) {
            actors = movie.getActors();
            for (Actor actor : actors) {
                if (actor.getId() != null) {
                    continue;
                }

                Iterable<Actor> actorsFromDB_ = hibernateActorDao.findByName(actor.getName());
                Iterator<Actor> actorsFromDB = actorsFromDB_.iterator();
                if (actorsFromDB.hasNext()) {
                    Actor actorFromDB = actorsFromDB.next();
                    actor.setId(actorFromDB.getId());
                } else {
                    // olololo, I really must automate this
                    hibernateActorDao.save(actor);
                }
            }
            // truncate long summaries
            if (movie.getSummary().length() > 1024) {
                movie.setSummary(movie.getSummary().substring(0, 1024));
            }

        } else {
            // if the movie is in DB, update of actors must be done through addActor
        }
        movie = entityManager.merge(movie);
        transaction.commit();
        return movie;
    }

    public Movie addFileToMovie(Movie selectedMovie, Path filePath) {

        VideoFile videoFile = null;
        if (null != filePath) {
            videoFile = new VideoFile();
            hibernateVideoFileDao.populateVideoFile(videoFile,filePath);
        }

        return addFileToMovie(selectedMovie,videoFile);

    }

    public Movie addFileToMovie(Movie selectedMovie, VideoFile videoFile){

        if (selectedMovie.getId()==null) {
            throw new IllegalStateException(String.format("Movie must be persisted before updating video file"));
        }

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        // add file to movie, just to setMovie(selectedMovie) in the file...
        selectedMovie = hibernateMovieDao.populateMovie(selectedMovie);
        if (null != videoFile) {
            selectedMovie.addFile(videoFile);
        }
        hibernateVideoFileDao.save(videoFile);
        transaction.commit();
        return selectedMovie;

    }

}
