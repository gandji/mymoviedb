/*
 * Copyright (C) 2017 gandji <gandji@free.fr>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.gandji.mymoviedb.tools;

import org.gandji.mymoviedb.data.*;
import org.gandji.mymoviedb.data.repositories.VideoFileRepository;
import org.gandji.mymoviedb.filefinder.FileUtils;
import org.gandji.mymoviedb.scrapy.MovieInfoSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
public class RepairDatabase {

    private static final Logger LOG = Logger.getLogger(RepairDatabase.class.getName());

    @Autowired
    private MovieInfoSearchService movieInfoSearchService;

    @Autowired
    private VideoFileRepository videoFileRepository;

    @Autowired
    private HibernateVideoFileDao videoFileDao;

    @Autowired
    private HibernateMovieDao hibernateMovieDao;

    public void doRepair() {
       fixNullHashCodes();
       fixMissingActors();
        fixMissingCreated();
    }

    private void fixMissingCreated() {
        Pageable pageRequest = new PageRequest(0,50);
        Page<Movie> moviePage = hibernateMovieDao.findAll(pageRequest);
        while (moviePage.hasContent()) {
            List<Movie> movies = moviePage.getContent();

            for (Movie movie : movies) {
                movie.getCreated();
                hibernateMovieDao.save(movie);
            }
            pageRequest = moviePage.nextPageable();
            if (null == pageRequest) {
                break;
            }
            moviePage = hibernateMovieDao.findAll(pageRequest);
        }
    }

    public void fixMissingActors() {

        Pageable pageRequest = new PageRequest(0,50);

        Page<Movie> moviePage = hibernateMovieDao.findAll(pageRequest);

        LOG.info("num of page in db "+moviePage.getTotalPages());

        while (moviePage.hasContent()) {
            List<Movie> movies = moviePage.getContent();

            for (Movie movie : movies) {

                List<Actor> actors = hibernateMovieDao.findActorsForMovie(movie);
                if (!actors.isEmpty()) {
                    //LOG.info("Movie " + movie.getTitle() + " has actors!");
                }
                else if (actors.isEmpty()) {
                    LOG.info("Movie " + movie.getTitle() + " has null actors.");

                    URL infoUrl = movie.getInfoUrl();
                    if (null!=infoUrl) {
                        Movie movieWithActors = movieInfoSearchService.getOneFilmFromUrl(infoUrl.toExternalForm());

                        LOG.info("MOVIE: " + movie.getTitle());
                        for (Actor actor : movieWithActors.getActors()) {
                            LOG.info("    adding actor : " + actor.getName());
                        }
                        hibernateMovieDao.addActorsToMovie(movie.getId(), movieWithActors.getActors());
                        LOG.info("MOVIE = " + movie.getTitle()+":  updated actors");
                    } else {
                        LOG.info("MOVIE = "+movie.getTitle()+" has no internet info URL");
                    }
                }

                List<Genre> genres = hibernateMovieDao.findGenresForMovie(movie);
                if (!genres.isEmpty()){
                    //LOG.info("Movie " + movie.getTitle() + " has genres!");
                }
                else if (genres.isEmpty()) {
                    LOG.info("Movie " + movie.getTitle() + " has null genres.");

                    URL infoUrl = movie.getInfoUrl();
                    if (null!=infoUrl) {
                        Movie movieWithGenres = movieInfoSearchService.getOneFilmFromUrl(infoUrl.toExternalForm());

                        LOG.info("MOVIE: " + movie.getTitle());
                        for (Genre genre : genres) {
                            LOG.info("    adding genre : " + genre.getName());
                        }
                        hibernateMovieDao.addGenresToMovie(movie.getId(), movieWithGenres.getGenres());
                        LOG.info("MOVIE = " + movie+": updated genres");
                    } else {
                        LOG.info("MOVIE = "+movie.getTitle()+" has no internet info URL");
                    }
                }

                if (movie.getPosterBytes()==null) {
                    URL infoUrl = movie.getInfoUrl();
                    if (null!=infoUrl) {
                        LOG.info("Movie " + movie.getTitle() + " has no poster, fetching...");
                        Movie movieWithPoster = movieInfoSearchService.getOneFilmFromUrl(infoUrl.toExternalForm());
                        hibernateMovieDao.addPoster(movie.getId(), movieWithPoster.getPosterBytes());
                    } else {
                        LOG.info("MOVIE = "+movie.getTitle()+" has nor poster, and has no internet info URL, cannot update poster");
                    }
                } else {
                    //LOG.info("Movie has poster OK");
                }

            }

            pageRequest = moviePage.nextPageable();
            if (null == pageRequest) {
                break;
            }
            moviePage = hibernateMovieDao.findAll(pageRequest);
        }

    }

    public void fixNullHashCodes() {
        // find files with null hash and compute hash
        List<VideoFile> files = videoFileRepository.findNullHashCodes();
        if (files.isEmpty()) {
            LOG.info("No file with null hash code found");
        }else {
            LOG.info("Found null hash Code files: ");
            for (VideoFile videoFile : files) {
                LOG.info("  -->  "+videoFile.getFileName());
                LOG.info("       "+videoFile.getDirectory());
                LOG.info("       "+videoFile.getId());
                videoFile.setHashCode(FileUtils.computeHash(Paths.get(videoFile.getDirectory(),
                        videoFile.getFileName())));
                LOG.info("          update file hash to "+videoFile.getHashCode());
                videoFileDao.save(videoFile);
            }
        }
    }
}
