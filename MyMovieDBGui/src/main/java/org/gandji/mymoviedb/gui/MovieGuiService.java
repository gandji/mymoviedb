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
package org.gandji.mymoviedb.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.enumeration.ExternalSource;
import com.omertron.themoviedbapi.model.FindResults;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.model.person.ExternalID;
import com.omertron.themoviedbapi.model.tv.TVBasic;
import com.omertron.themoviedbapi.model.tv.TVInfo;
import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.MyMovieDBPreferences;
import org.gandji.mymoviedb.data.HibernateVideoFileDao;
import org.gandji.mymoviedb.gui.widgets.MovieDescriptionDialog;
import org.gandji.mymoviedb.gui.widgets.MovieDescriptionPanel;
import org.gandji.mymoviedb.gui.widgets.NewLayout;
import org.gandji.mymoviedb.scrapy.GoogleSearch;
import org.gandji.mymoviedb.scrapy.MovieInfoSearchService;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.data.VideoFile;
import org.gandji.mymoviedb.services.DialogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
@Slf4j
public class MovieGuiService {

    @Value("${mymoviedb.tmdb.apikey}")
    private String tmdbApiKey;

    @Autowired
    private MovieInfoSearchService movieInfoSearchService;

    @Autowired
    private HibernateMovieDao hibernateMovieDao;

    @Autowired
    private HibernateVideoFileDao hibernateVideoFileDao;
    
    @Autowired
    private GoogleSearch googleSearch;

    @Autowired
    private NewLayout mainFrame;

    @Autowired
    private MyMovieDBPreferences preferences;

    @Autowired
    private DialogsService dialogsService;

    @Autowired
    private ApplicationContext applicationContext;

    public void playTheFile(Path fileToPlay) {
      // launch the default action for this file (works on linux?)
        if (!Desktop.isDesktopSupported()) {
            dialogsService.showMessageDialog(mainFrame,
                    "Cannot launch  default video player!",
                    "Warning",
                    DialogsService.MessageType.WARN);
            return;
        }
        try {
            dialogsService.externalOpenFile(fileToPlay);
        } catch (Exception e) {
            dialogsService.showMessageDialog(mainFrame,
                    "Cannot open file " + fileToPlay + "\n"
                    + "Make sure the file is there, and that it is associated to a video player.",
                    "Error",
                    DialogsService.MessageType.WARN);
        }
    }
    
    public void playTheMovie(Movie movie) {
        if (null == movie) {
            dialogsService.showMessageDialog(mainFrame,
                    "Cannot find movie!",
                    "Internal error",
                    DialogsService.MessageType.ERROR);
            return;
        }
        String fileToPlay = null;
        List<VideoFile> files = null;
        Set<VideoFile> filesSet = null;
        try {
            filesSet = movie.getFiles();
            // if movies has the files, ok
            files = new ArrayList<>();
            for (VideoFile videoFile : filesSet) {
                files.add(videoFile);
            }
        } catch(Exception e) {
            files = null;
        }

        if (null == files) {
            files = hibernateMovieDao.findVideoFilesForMovie(movie);
        }


        if (null==files || files.isEmpty()) {
            // no files found
             dialogsService.showMessageDialog(mainFrame,
                    "No files for movie "+movie.getTitle(),
                    "Warning",
                    DialogsService.MessageType.WARN);
            return;
        } else if (1==files.size()) {
            //only one file found
            //@todo correctly join paths!!
            fileToPlay = files.get(0).getDirectory()+"/"+files.get(0).getFileName();
        } else {
            //more than one file found
            Integer nFiles = files.size();
            String[] fileNames = new String[nFiles];
            for (Integer i = 0; i < files.size(); i++) {
                fileNames[i] = files.get(i).getDirectory()+"/"+files.get(i).getFileName();
            }
            fileToPlay = (String) JOptionPane.showInputDialog(mainFrame,
                    "More than one file for movie: " + movie.getTitle() + "\n"
                    + "Choose the one you want to see:",
                    "Choose the file",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    fileNames,
                    files.get(0).getDirectory()+"/"+files.get(0).getFileName());

        }
        if ((fileToPlay == null) || (fileToPlay.length() == 0)) {
            return;
        }
        playTheFile(Paths.get(fileToPlay));

    }

    public void internetCritics(Movie movie) {
        if (null == movie) {
            dialogsService.showMessageDialog(mainFrame,
                    "Cannot find movie!",
                    "Internal error",
                    DialogsService.MessageType.ERROR);
            return;
        }
        log.info("Searching for movie title : "+movie.getTitle());
        String url = googleSearch.getResult("telerama"+" "+movie.getTitle());
        // launch the default action for urls (works on linux?)
        if (!Desktop.isDesktopSupported()){
            dialogsService.showMessageDialog(mainFrame,
                    "Cannot launch  default browser!",
                    "Warning",
                    DialogsService.MessageType.WARN);
            return;
        }
        try {
            dialogsService.externalOpenUrl(url);
        } catch (IOException e) {
            dialogsService.showMessageDialog(mainFrame,
                    "Cannot open "+url+"\n",
                    "Error",
                    DialogsService.MessageType.WARN);
            return;
        }
    }
    public void openInfoUrl(URL movieUrl) {

        MovieInfoSearchService.UrlType movieUrlType = MovieInfoSearchService.UrlType.fromUrlString(movieUrl.toString());
        MovieInfoSearchService.UrlType preferencesUrlType = preferences.getInternetTarget();

        String infoUrl = null;
        if (movieUrlType.equals(preferencesUrlType)) {
            infoUrl = movieUrl.toString();
        } else if (movieUrlType.equals(MovieInfoSearchService.UrlType.TMDB)) {
            log.info("Compute tmdb url from : "+movieUrl.toString());

            MovieInfoSearchService.TmdbDescriptor tmdbDescriptor =
                    movieInfoSearchService.getTmbdIdFromUrl(movieUrl.toString());

            TheMovieDbApi tmdbApi = null;
            try {
                tmdbApi = new TheMovieDbApi(tmdbApiKey);
                if (tmdbDescriptor.type.equals("movie")) {
                    MovieInfo movieInfo = tmdbApi.getMovieInfo(tmdbDescriptor.id, "fr");
                    infoUrl = "http://www.imdb.com/title/" + movieInfo.getImdbID();
                } else if (tmdbDescriptor.type.equals("tv")) {
                    TVInfo tvInfo = tmdbApi.getTVInfo(tmdbDescriptor.id,"fr");
                    ExternalID externalID = tvInfo.getExternalIDs();
                    infoUrl = "http://www.imdb.com/title/"+externalID.getImdbId();
                    log.info("Using imdb url <"+infoUrl+"> for TV show "+tvInfo.getName());
                }
            } catch (MovieDbException e) {
                e.printStackTrace();
            }


        } else if (movieUrlType.equals(MovieInfoSearchService.UrlType.IMDB)) {
            Pattern extractId = Pattern.compile("https?://www.imdb.com/title/(tt[0-9]+)");
            Matcher movieMatcher = extractId.matcher(movieUrl.toString());

            if (!movieMatcher.find()) {
                log.warn("Could not match id in <" + movieUrl.toString() + ">");
                return;
            }
            String movieId = movieMatcher.group(1);

            log.info("Searching tmdb for imdb id <"+movieId+">");

            try {
                TheMovieDbApi tmdbApi = new TheMovieDbApi(tmdbApiKey);

                FindResults fr = tmdbApi.find(movieId, ExternalSource.IMDB_ID,"fr");
                if (!fr.getMovieResults().isEmpty()){
                    // take first, we need the movie info
                    MovieInfo movieInfo = tmdbApi.getMovieInfoImdb(movieId,"fr");
                    infoUrl = "http://www.themoviedb.org/movie/"+movieInfo.getId();
                } else if (!fr.getTvResults().isEmpty()) {
                    TVBasic tvBasic = fr.getTvResults().get(0);
                    infoUrl = "http://www.themoviedb.org/tv/"+tvBasic.getId();
                }

            } catch (MovieDbException e) {
                e.printStackTrace();
            }

        }
        if (null==infoUrl) {
            infoUrl = movieUrl.toString();
        }
        try {
            dialogsService.externalOpenUrl(infoUrl);
        } catch (IOException e) {
            dialogsService.showMessageDialog(mainFrame,
                    "Cannot open "+infoUrl+"\n",
                    "Error",
                    DialogsService.MessageType.WARN);
            return;
        }
    }

    public void launchMovieDescriptionDialog(Movie movie, Path path, JFrame parent, boolean modal) {
        MovieDescriptionPanel movieDescriptionPanel = (MovieDescriptionPanel) applicationContext.getBean("movieDescriptionPanel");
        MovieDescriptionDialog movieDescriptionDialog = (MovieDescriptionDialog) applicationContext.getBean("movieDescriptionDialog",parent, modal, movieDescriptionPanel);

        if (movie != null && path != null) {
            throw new IllegalArgumentException("Either movie or path, not both");
        }

        Movie movie_;
        if (movie == null) {
            movie_ = new Movie();
        } else {
            movie_ = movie;
        }

        if (path != null) {
            VideoFile videoFile = new VideoFile();
            hibernateVideoFileDao.populateVideoFile(videoFile, path);
            movie_.addFile(videoFile);
        }

        movieDescriptionDialog.setModal(true);
        movieDescriptionDialog.setData(movie_);
        movieDescriptionDialog.setVisible(true);

    }
}
