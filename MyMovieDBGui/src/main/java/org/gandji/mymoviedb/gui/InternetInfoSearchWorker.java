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

import java.nio.file.Path;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.gui.widgets.NewLayout;
import org.gandji.mymoviedb.gui.widgets.ResultChooserDialog;
import org.gandji.mymoviedb.gui.widgets.UserInputMovie;
import org.gandji.mymoviedb.scrapy.MovieFoundCallback;
import org.gandji.mymoviedb.scrapy.MovieInfoSearchService;
import org.gandji.mymoviedb.data.Movie;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
@Scope("prototype")
@Slf4j
public class InternetInfoSearchWorker extends SwingWorker<Integer, Movie> implements MovieFoundCallback, ApplicationContextAware {

    @Autowired
    private MovieInfoSearchService movieInfoSearchService;

    private ApplicationContext applicationContext;

    // arghh I need the mainframe for userinputdialog
    @Autowired
    private NewLayout mainFrame;

    private Path file;
    private List<String> kwds;
    private ResultChooserDialog resultChooserDialog;

    private boolean cancelRequested = false;

    public void setFile(Path file) { this.file = file; }

    public void setKwds(List<String> kwds) {
        this.kwds = kwds;
    }

    public void setResultChooserDialog(ResultChooserDialog resultChooserDialog) {
        this.resultChooserDialog = resultChooserDialog;
    }

    public boolean isCancelRequested() { return cancelRequested; }

    public void setCancelRequested(boolean cancelRequested) { this.cancelRequested = cancelRequested; }

    @Override
    protected Integer doInBackground() throws Exception {
        List<Movie> movies = null;
        log.info("Searching info on internet");
        try {
            movies = movieInfoSearchService.searchInternetInfoForMovies(kwds, this);
        } catch (InterruptedException ex) {
            return 0;
        }
        if (null == movies || movies.isEmpty()) {
            log.info("No result on IMDB for kwds: " + kwds);
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        UserInputMovie userInputMovie = (UserInputMovie) applicationContext.getBean("userInputMovie",mainFrame,true);
                        userInputMovie.setFile(file);
                        userInputMovie.setText("No IMDB movie found for : "+kwds);
                        userInputMovie.setVisible(true);
                    }
                });
            resultChooserDialog.setVisible(false);
            resultChooserDialog.dispose();
            return 1;
        }
        return 0;
    }

    // called back (by us) from the service at each movie info found
    @Override
    public MovieFoundResult found(Movie movie) {
        // tell EventDispatchThread we have a movie coming in
        publish(movie);
        if (isCancelRequested()) {
            return MovieFoundResult.STOP;
        }
        return MovieFoundResult.CONTINUE;
    }

    // this swing callback receives data asynchronously on the EvetnDispatchThread
    // when "publish" is called in the worker
    @Override
    protected void process(List<Movie> movies) {
        for (Movie movie : movies) {
            resultChooserDialog.addMovie(movie);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
    }

}
