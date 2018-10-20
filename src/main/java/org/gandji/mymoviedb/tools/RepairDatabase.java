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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
public class RepairDatabase {

    private static final Logger logger = LoggerFactory.getLogger(RepairDatabase.class.getName());

    @Autowired
    private MovieInfoSearchService movieInfoSearchService;

    @Autowired
    private VideoFileRepository videoFileRepository;

    @Autowired
    private HibernateActorDao hibernateActorDao;

    @Autowired
    private HibernateVideoFileDao videoFileDao;

    @Autowired
    private HibernateMovieDao hibernateMovieDao;

    public void doRepair() {
       //fixNullHashCodes();
       //fixMissingActors();
        //fixMissingCreated();
        fixDoubleActors();
    }

    private void fixDoubleActors() {
        Set<String> actorNames = new HashSet<>();
        Map<String,Integer> actorCount = new HashMap<>();
        int totalActors = 0;
        //for (Actor actor : hibernateActorDao.findAll()) {
        for (Actor actor : hibernateActorDao.findAll()) {
            totalActors++;
            String name = actor.getName();
            if (actorNames.contains(name)) {
                actorCount.put(name,actorCount.get(name) + 1);
            } else  {
                actorNames.add(name);
                actorCount.put(name,1);
            }
        }

        // log
        logger.info("Found "+totalActors+" actor entries, and "+actorNames.size()+" names.");
        actorCount.forEach((name, count) ->
        {
            if (count > 1) {
                fixOneActorDoublon(name);
            }
        });

    }

    private void fixOneActorDoublon(String name) {
        Iterator<Actor> actorIt = hibernateActorDao.findByName(name).iterator();
        logger.info(" Fixing doublons for <"+name+">");
        Actor target = null; // l'acteur qui va remplacer les doublons
        List<Actor> actorsToRemove = new ArrayList<>();
        while(actorIt.hasNext()) {
            // pour chaque doublon....
            Actor actor = actorIt.next();
            logger.info("   -> "+actor.getId());
            List<Movie> movies = hibernateActorDao.findMoviesForActor(actor);
            Actor actorToRemove = null;
            for (Movie movie : movies) {
                logger.info("            "+movie.getTitle());
                if (target != null) {
                    // ...on a deja choisi l'acteur de remplacement parmi les doublons
                    List<Actor> movieActors = hibernateMovieDao.findActorsForMovie(movie);
                    ListIterator<Actor> movieActorIt = movieActors.listIterator();
                    while(movieActorIt.hasNext()) {
                        Actor movieActor = movieActorIt.next();
                        if (movieActor.getName().equals(target.getName())) {
                            // on enleve du film l'acteur avec ce nom
                            movie = hibernateMovieDao.removeActor(movie, actor);
                            actorsToRemove.add(actor);
                            // on ajoute le premier doublon
                            movie = hibernateMovieDao.addActor(movie, target);
                            logger.info("     swapped actor "+movieActor.getId()+":"+movieActor.getName()
                                    +" for "+target.getId()+":"+target.getName());
                        }
                    }
                }
            }
            if (target==null) {
                // premiere iteration de la liste des doublons,
                // on choisit cet acteur pour remplacer les autres
                target = actor;
                logger.info("                  :target: "+actor.getId());
            }

        }
        // remove orphan actors
        for (Actor actor : actorsToRemove) {
            List<Movie> movies = hibernateActorDao.findMoviesForActor(actor);
            if (movies.isEmpty()) {
                logger.info("Deleting orphan actor "+actor.getName()+":"+actor.getId());
                hibernateActorDao.delete(actor);
            } else {
                logger.info("Not deleting actor "+actor.getName()+":"+actor.getId()
                +" because has "+movies.size()+" movies");
            }
        }
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

    /**
     * This is broken and must be repaired: enforce manually unicity of actor names
     */
    @Deprecated
    public void fixMissingActors() {

        Pageable pageRequest = new PageRequest(0,50);

        Page<Movie> moviePage = hibernateMovieDao.findAll(pageRequest);

        logger.info("num of page in db "+moviePage.getTotalPages());

        while (moviePage.hasContent()) {
            List<Movie> movies = moviePage.getContent();

            for (Movie movie : movies) {

                List<Actor> actors = hibernateMovieDao.findActorsForMovie(movie);
                if (!actors.isEmpty()) {
                    //logger.info("Movie " + movie.getTitle() + " has actors!");
                }
                else if (actors.isEmpty()) {
                    logger.info("Movie " + movie.getTitle() + " has null actors.");

                    URL infoUrl = movie.getInfoUrl();
                    if (null!=infoUrl) {
                        Movie movieWithActors = movieInfoSearchService.getOneFilmFromUrl(infoUrl.toExternalForm());

                        logger.info("MOVIE: " + movie.getTitle());
                        for (Actor actor : movieWithActors.getActors()) {
                            logger.info("    adding actor : " + actor.getName());
                        }
                        // TODO fix fixMissingActors : hibernateMovieDao.addActorsToMovie(movie.getId(), movieWithActors.getActors());
                        logger.info("MOVIE = " + movie.getTitle()+":  updated actors");
                    } else {
                        logger.info("MOVIE = "+movie.getTitle()+" has no internet info URL");
                    }
                }

                List<Genre> genres = hibernateMovieDao.findGenresForMovie(movie);
                if (!genres.isEmpty()){
                    //logger.info("Movie " + movie.getTitle() + " has genres!");
                }
                else if (genres.isEmpty()) {
                    logger.info("Movie " + movie.getTitle() + " has null genres.");

                    URL infoUrl = movie.getInfoUrl();
                    if (null!=infoUrl) {
                        Movie movieWithGenres = movieInfoSearchService.getOneFilmFromUrl(infoUrl.toExternalForm());

                        logger.info("MOVIE: " + movie.getTitle());
                        for (Genre genre : genres) {
                            logger.info("    adding genre : " + genre.getName());
                        }
                        hibernateMovieDao.addGenresToMovie(movie.getId(), movieWithGenres.getGenres());
                        logger.info("MOVIE = " + movie+": updated genres");
                    } else {
                        logger.info("MOVIE = "+movie.getTitle()+" has no internet info URL");
                    }
                }

                if (movie.getPosterBytes()==null) {
                    URL infoUrl = movie.getInfoUrl();
                    if (null!=infoUrl) {
                        logger.info("Movie " + movie.getTitle() + " has no poster, fetching...");
                        Movie movieWithPoster = movieInfoSearchService.getOneFilmFromUrl(infoUrl.toExternalForm());
                        hibernateMovieDao.addPoster(movie.getId(), movieWithPoster.getPosterBytes());
                    } else {
                        logger.info("MOVIE = "+movie.getTitle()+" has nor poster, and has no internet info URL, cannot update poster");
                    }
                } else {
                    //logger.info("Movie has poster OK");
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
            logger.info("No file with null hash code found");
        }else {
            logger.info("Found null hash Code files: ");
            for (VideoFile videoFile : files) {
                logger.info("  -->  "+videoFile.getFileName());
                logger.info("       "+videoFile.getDirectory());
                logger.info("       "+videoFile.getId());
                videoFile.setHashCode(FileUtils.computeHash(Paths.get(videoFile.getDirectory(),
                        videoFile.getFileName())));
                logger.info("          update file hash to "+videoFile.getHashCode());
                videoFileDao.save(videoFile);
            }
        }
    }
}
