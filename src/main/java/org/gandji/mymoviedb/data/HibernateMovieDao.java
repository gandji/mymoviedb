/*
 * Copyright (C) 2017 gandji <gandji@free.fr>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gandji.mymoviedb.data;

import org.gandji.mymoviedb.data.repositories.MovieRepository;
import org.gandji.mymoviedb.services.MovieFileServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
public abstract class HibernateMovieDao {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    protected MovieFileServices movieFileServices;

    protected MovieRepository movieRepository;

    void setMovieRepository(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Transactional
    public Movie save(Movie movie) {
        return entityManager.merge(movie);
    }

    public long count() { return movieRepository.count(); }

    @Transactional
    public void deleteMovie(Movie movie) {
        movieRepository.delete(movie);
    }

    // @todo this duplicates code in videofileworker.saveMovie
    @Transactional
    public Movie updateOrCreateMovie(Movie selectedMovie, Path filePath) {
        List<Movie> ml = movieRepository.findByInfoUrl(selectedMovie.getInfoUrl());
        if (!ml.isEmpty()) {
            // if there, add file to it and save
            Movie updateMovie = ml.get(0);
            if (null != filePath) {
                VideoFile videoFile = new VideoFile();
                movieFileServices.updateVideoFile(videoFile,filePath);
                updateMovie.addFile(videoFile);
            }
            return save(updateMovie);

        } else {
            // if not there,
            // truncate long summaries
            if (selectedMovie.getSummary().length() > 1024) {
                selectedMovie.setSummary(selectedMovie.getSummary().substring(0, 1024));
            }
            // then add "selectedMovie" and save
            if (null != filePath) {
                VideoFile videoFile = new VideoFile();
                movieFileServices.updateVideoFile(videoFile, filePath);
                selectedMovie.addFile(videoFile);
            }
            return save(selectedMovie);
        }
    }

    @Transactional
    public Movie updateOrCreateMovie(Movie selectedMovie, VideoFile videoFile){
        List<Movie> ml = movieRepository.findByInfoUrl(selectedMovie.getInfoUrl());
        if (!ml.isEmpty()) {
            // if there, add file to it and save
            Movie updateMovie = ml.get(0);
            if (null != videoFile) {
                updateMovie.addFile(videoFile);
            }
            return save(updateMovie);

        } else {
            // if not there,
            // truncate long summaries
            if (selectedMovie.getSummary().length() > 1024) {
                selectedMovie.setSummary(selectedMovie.getSummary().substring(0, 1024));
            }
            // then add file and save
            if (null != videoFile) {
                selectedMovie.addFile(videoFile);
            }
            return save(selectedMovie);
        }

    }

    public Movie findOne(Long id) {
        return movieRepository.findOne(id);
    }

    @Transactional
    public Movie newGenres(Movie movie, Set<Genre> genres) {
        Movie updateMovie = findOne(movie.getId());
        updateMovie.clearGenres();
        for (Genre genre : genres) {
            updateMovie.addGenreByName(genre.getName());
        }
        return save(updateMovie);
    }

    @Transactional
    public Movie newActors(Movie movie, Set<Actor> actors) {
        Movie updateMovie = findOne(movie.getId());
        updateMovie.clearActors();
        for (Actor actor : actors) {
            updateMovie.addActorByName(actor.getName());
        }
        return save(updateMovie);
    }
    // TODO fix pagination in movie DAO
    // paginating, see:
    // https://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-part-seven-pagination/
    public Page<Movie> findAll(int offset, int pageSize) {
        Pageable pageRequest = createPageRequest(offset, pageSize);
        Page<Movie> searchResultPage = findAll(pageRequest);
        return searchResultPage;
    }

    public Page<Movie> findAllByOrderByCreated(int offset, int pageSize) {
        Pageable pageRequest = createPageRequest(offset, pageSize);
        Page<Movie> searchResultPage = findAllByOrderByCreated(pageRequest);
        return searchResultPage;
    }

    private Page<Movie> findAllByOrderByCreated(Pageable pageRequest) {
        return movieRepository.findAllByOrderByCreatedDesc(pageRequest);
    }

    private Pageable createPageRequest(int offset, int pageSize) {
        return new PageRequest(offset,pageSize);
    }

    public Page<Movie> findAll(Pageable pageRequest) {
        return movieRepository.findAll(pageRequest);
    }

    public Iterable<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Transactional
    public Path findOneFileForMovie(Movie movie) {
        Movie movieAndFiles = movieRepository.findOne(movie.getId());
        if (movieAndFiles.getFiles().isEmpty()) {
            return null;
        }
        VideoFile vf = (VideoFile) movieAndFiles.getFiles().toArray()[0];
        return Paths.get(vf.getDirectory(), vf.getFileName());

    }

    @Transactional
    public List<Actor> findActorsForMovie(Movie movie) {
        Movie movieAndActors = movieRepository.findOne(movie.getId());
        ArrayList<Actor> al = new ArrayList<>();
        for (Actor actor :movieAndActors.getActors()) {
            al.add(actor);
        }
        return al;
    }

    @Transactional
    public List<Genre> findGenresForMovie(Movie movie) {
        Movie movieAndGenres = movieRepository.findOne(movie.getId());
        ArrayList<Genre> gl = new ArrayList<>();
        for (Genre genre : movieAndGenres.getGenres()) {
            gl.add(genre);
        }
        return gl;
    }

    @Transactional
    public List<VideoFile> findVideoFilesForMovie(Movie movie) {
        Movie movieAndFiles = movieRepository.findOne(movie.getId());
        ArrayList<VideoFile> vfl = new ArrayList<>();
        for (VideoFile file : movieAndFiles.getFiles()) {
            vfl.add(file);
        }
        return vfl;
    }

    @Transactional
    public void addActorsToMovie(Long movieId, Set<Actor> actors) {
        Movie movie = movieRepository.findOne(movieId);
        for (Actor actor : actors) {
            movie.addActorByName(actor.getName());
        }
        movieRepository.save(movie);
    }

    @Transactional
    public void addGenresToMovie(Long movieId, Set<Genre> genres) {
        Movie  movie = movieRepository.findOne(movieId);
        for (Genre genre : genres) {
            movie.addGenreByName(genre.getName());
        }
        movieRepository.save(movie);
    }

    @Transactional
    public void addPoster(Long movieId, byte[] poster) {
        Movie  movie = movieRepository.findOne(movieId);
        movie.setPosterBytes(poster);
        movieRepository.save(movie);
    }

    public abstract List<Movie> findByDirectorKeywords(String kwds);

    public abstract List<Movie> findByTitleKeywords(String kwds);

    public abstract List<Movie> findByAlternateTitleKeywords(String kwds);

    public abstract List<Movie> findByGenreName(String genre);

    public abstract List<Movie> findByCommentsKeywords(String kwds);

    public abstract List<Movie> findByActorsKeywords(String kwds);

    public abstract List<Movie> findByInfoUrl(URL infoUrl);

    public abstract Iterable<Movie> searchInternal(String titleKeywords, String directorKeywords,
                                                   String actorsKeywords, String genreKeyword,
                                                   String commentsKeywords, String qualiteVideoKeyword);


}
