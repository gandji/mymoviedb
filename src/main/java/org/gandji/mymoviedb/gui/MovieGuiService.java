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
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.enumeration.ExternalSource;
import com.omertron.themoviedbapi.model.FindResults;
import com.omertron.themoviedbapi.model.movie.MovieBasic;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.model.person.ExternalID;
import com.omertron.themoviedbapi.model.tv.TVBasic;
import com.omertron.themoviedbapi.model.tv.TVInfo;
import org.gandji.mymoviedb.MyMovieDBPreferences;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.data.VideoFile;
import org.gandji.mymoviedb.gui.widgets.NewLayout;
import org.gandji.mymoviedb.scrapy.GoogleSearch;
import org.gandji.mymoviedb.scrapy.MovieInfoSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
public class MovieGuiService {

    private static final Logger LOG = LoggerFactory.getLogger(MovieGuiService.class);

    @Value("${mymoviedb.tmdb.apikey}")
    private String tmdbApiKey;

    @Autowired
    private MovieInfoSearchService movieInfoSearchService;

    @Autowired
    private HibernateMovieDao hibernateMovieDao;
    
    @Autowired
    private GoogleSearch googleSearch;

    @Autowired
    private NewLayout mainFrame;

    @Autowired
    private MyMovieDBPreferences preferences;

    public void playTheFile(Path fileToPlay) {
      // launch the default action for this file (works on linux?)
        if (!Desktop.isDesktopSupported()){
            JOptionPane.showMessageDialog(mainFrame,
                    "Cannot launch  default video player!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Desktop dt = Desktop.getDesktop();
        File fd = null;
        try {
            fd = new File(fileToPlay.getParent().resolve(fileToPlay.getFileName()).toString());
            dt.open(fd);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Cannot open file " + fileToPlay + "\n"
                    + "Make sure the file is there, and that it is associated to a video player.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void playTheMovie(Movie movie) {
        if (null == movie) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Cannot find movie!",
                    "Internal error",
                    JOptionPane.ERROR_MESSAGE);
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
             JOptionPane.showMessageDialog(mainFrame,
                    "No files for movie "+movie.getTitle(),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(mainFrame,
                    "Cannot find movie!",
                    "Internal error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        LOG.info("Searching for movie title : "+movie.getTitle());
        String url = googleSearch.getResult("telerama"+" "+movie.getTitle());
        // launch the default action for urls (works on linux?)
        if (!Desktop.isDesktopSupported()){
            JOptionPane.showMessageDialog(mainFrame,
                    "Cannot launch  default browser!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Desktop dt = Desktop.getDesktop();
        try {
        dt.browse(URI.create(url));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Cannot open "+url+"\n",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    public void openInfoUrl(Movie movie) {

        MovieInfoSearchService.UrlType movieUrlType = MovieInfoSearchService.UrlType.fromUrlString(movie.getInfoUrl().toString());
        MovieInfoSearchService.UrlType preferencesUrlType = preferences.getInternetTarget();

        String infoUrl = null;
        if (movieUrlType.equals(preferencesUrlType)) {
            infoUrl = movie.getInfoUrl().toString();
        } else if (movieUrlType.equals(MovieInfoSearchService.UrlType.TMDB)) {
            LOG.info("Compute imdb url from : "+movie.getInfoUrl().toString());

            MovieInfoSearchService.TmdbDescriptor tmdbDescriptor =
                    movieInfoSearchService.getTmbdIdFromUrl(movie.getInfoUrl().toString());

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
                    LOG.info("Using imdb url <"+infoUrl+"> for TV show "+tvInfo.getName());
                }
            } catch (MovieDbException e) {
                e.printStackTrace();
            }


        } else if (movieUrlType.equals(MovieInfoSearchService.UrlType.IMDB)) {
            Pattern extractId = Pattern.compile("https?://www.imdb.com/title/(tt[0-9]+)");
            Matcher movieMatcher = extractId.matcher(movie.getInfoUrl().toString());

            if (!movieMatcher.find()) {
                LOG.warn("Could not match id in <" + movie.getInfoUrl().toString() + ">");
                return;
            }
            String movieId = movieMatcher.group(1);

            LOG.info("Searching tmdb for imdb id <"+movieId+">");

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
            infoUrl = movie.getInfoUrl().toString();
        }
        Desktop dt = Desktop.getDesktop();
        try {
        dt.browse(URI.create(infoUrl));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Cannot open "+infoUrl+"\n",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
