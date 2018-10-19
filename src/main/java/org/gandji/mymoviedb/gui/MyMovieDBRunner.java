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

import java.util.Arrays;
import java.util.List;

import org.gandji.mymoviedb.data.*;
import org.gandji.mymoviedb.gui.widgets.NewLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;

import javax.swing.*;

/**
 *
 * @author gandji <gandji@free.fr>
 */
public abstract class MyMovieDBRunner implements CommandLineRunner{

    private static final Logger LOG = LoggerFactory.getLogger(MyMovieDBRunner.class);
    
    @Autowired
    private NewLayout newLayout;

    @Autowired
    private MovieDataModelPoster movieDataModelPoster;

    @Autowired
    private HibernateMovieDao hibernateMovieDao;

    @Autowired
    private HibernateKerDao hibernateKerDao;

    @Override
    public void run(String... strings) throws Exception {

        //additionalLoggingConfiguration();

        vendorSpecificDatabaseInitialization();

        populateWithDefaultRegexps();
        movieDataModelPoster.setMovies(hibernateMovieDao.findAllByOrderByCreated(0, 300).getContent());
        movieDataModelPoster.fireTableDataChanged();

        /* display the form using the AWT EventQueue */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                newLayout.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                newLayout.setVisible(true);
            }
        });
    }

    protected abstract void vendorSpecificDatabaseInitialization();

    /*
     REMOVE
     */
    @Deprecated
    public void additionalLoggingConfiguration() {
        // plug the log display
        //javaUtilLogAppender = new JavaUtilLogAppender(logDisplay.getLogTextArea());
        //LOG.getParent().addHandler(javaUtilLogAppender);
    }

    public void populateWithDefaultRegexps() {
        Iterable<String> dropRegexs =
    Arrays.asList(
        ".*x264.*","HDTV","BluRay","720[Pp]",".*1080[Pp].*",
            "MULT[Ii]","AC3","[xX][vV][iI][dD]","\\[\\w+\\]",
            "([Bb][dD]|[Bb][Rr]|[Dd][Vv][Dd])[Rr][Ii][Pp]","AYMO","PROPER","F[rR][eE][nN][cC][hH]",
            "TRUEFRENCH","AAC","PopHD","YIFY",
                        "\\s", "\\([^)]+", "[^)]+\\)$", "^$",
                        "SVR", "afrique31","HDLIGHT",".*[eE][mM][uU][lL][eE].*",
                        ".*[dD][iI][vV][xX].*",".*[dD][vV][dD].*","[cC][dD][12]",
                        "[lLe]","[lL]a","[lL]es",
                        "[mM][hH][dD]","JOKPIC"
                );
        for (String dropk : dropRegexs) {
            try {
                List<KeywordExcludeRegexp> kl = hibernateKerDao.findByRegexpString(dropk);
                if (kl.isEmpty()) {
                    KeywordExcludeRegexp ker = new KeywordExcludeRegexp(dropk);
                    hibernateKerDao.save(ker);
                }
            } catch (DataIntegrityViolationException dv) {
                // la table contient deja la regexp, cest pas grave

            }
        }
        LOG.debug("Populated default keyword exclude regexps");
    }

    protected void fillDbForTests() {
        {
            Movie movie = new Movie();
            movie.setTitle("Men in Black 3 (2012)");
            movie.setYear("(2012)");
            movie.setInfoUrlAsString("http://www.imdb.com/title/tt1409024");
            movie.setDirector("Barry Sonnenfeld");
            movie.addActorByName("Will Smith");
            movie.addActorByName("Tommy Lee Jones");
            movie.addActorByName("Josh Brolin");
            movie.setSummary("Agent J travels in time to M.I.B.'s early days in 1969 to stop an alien from assassinating his friend Agent K and changing history.");
            movie.setDuree("1h 46min");
            movie.addGenreByName("Action");
            movie.addGenreByName("Adventure");
            movie.addGenreByName("Comedy");
            List<Movie> ml = hibernateMovieDao.findByInfoUrl(movie.getInfoUrl());
            if (ml.isEmpty()) {
                LOG.info("Saving movie "+movie.getTitle());
                hibernateMovieDao.save(movie);
            }
        }
        {
            Movie movie = new Movie();
            movie.setTitle("Mononoke-hime (1997)");
            movie.setYear("(1997)");
            movie.setInfoUrlAsString("http://www.imdb.com/title/tt0119698");
            movie.setDirector("Hayao Miyazaki");
            movie.addActorByName("Yôji Matsuda");
            movie.addActorByName("Yuriko Ishida");
            movie.addActorByName("Yûko Tanaka");
            movie.setSummary("On a journey to find the cure for a Tatarigami's curse, Ashitaka finds himself in the middle of a war between the forest gods and Tatara, a mining colony. In this quest he also meets San, the Mononoke Hime.");
            movie.setDuree("2h 14min");
            movie.addGenreByName("Animation");
            movie.addGenreByName("Adventure");
            movie.addGenreByName("Fantasy");
            List<Movie> ml = hibernateMovieDao.findByInfoUrl(movie.getInfoUrl());
            if (ml.isEmpty()) {
                LOG.info("Saving movie "+movie.getTitle());
                hibernateMovieDao.save(movie);
            }
        }
        {
            Movie movie = new Movie();
            movie.setTitle("Back to the Future (1985)");
            movie.setYear("(1985)");
            movie.setInfoUrlAsString("http://www.imdb.com/title/tt0088763");
            movie.setDirector("Robert Zemeckis");
            movie.addActorByName("Christopher Lloyd");
            movie.addActorByName("Michael J. Fox");
            movie.addActorByName("Lea Thompson");
            movie.setSummary("Marty McFly, a 17-year-old high school student, is accidentally sent 30 years into the past in a time-traveling DeLorean invented by his close friend, the maverick scientist Doc Brown.");
            movie.setDuree("1h 56min");
            movie.addGenreByName("Animation");
            movie.addGenreByName("Adventure");
            movie.addGenreByName("Fantasy");
            VideoFile vf = new VideoFile();
            vf.setDirectory("C:\\");
            vf.setFileName("Back.to.the.Future.1985.720p.BrRip.x264.YIFY.mp4");
            vf.setMovie(movie);
            List<Movie> ml = hibernateMovieDao.findByInfoUrl(movie.getInfoUrl());
            if (ml.isEmpty()) {
                LOG.info("Saving movie "+movie.getTitle());
                hibernateMovieDao.save(movie);
            }
        }
    }
}
