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
package org.gandji.mymoviedb.filefinder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.services.MovieFileServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author gandji <gandji@free.fr>
*/
@Component
@Scope("prototype")
public class VideoFileWorker extends SwingWorker<Integer, Movie> {

    private static final Logger LOG = Logger.getLogger(VideoFileWorker.class.getName());

    private Path file;
    private Movie movie;
    private boolean limitPopups = false;


    @Autowired
    private MovieFileServices movieFileServices;

    public VideoFileWorker() {
        this.file = null;
        this.movie = null;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    public void setLimitPopups(boolean limitPopups) {
        this.limitPopups = limitPopups;
    }

    public Movie getMovie() { return movie; }

    public void setMovie(Movie movie) { this.movie = movie; }

    /*
        * input: videoFile, resultChooserDialog for feedback
        *
        * 1. compute hash of file, search by hash in db
        *    if file in DB, inform user, and we are done
        * 2. try to find it by file name, you never know
        *    if we find the filename in db , inform user, and we are done
        * 3. extract title keywords
        * 4. search in db for keywords:
        *    if we found it: present the choices to the user, let him choose
         */
    @Override
    protected Integer doInBackground() throws Exception {
        boolean isVideoFile;
        Integer cancelAll = 0;
        try {
            isVideoFile = FileUtils.isVideoFile(file);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "while isVideoFile: ", ex.getMessage());
            isVideoFile = false;
        }
        if (isVideoFile) {
            cancelAll = movieFileServices.addFileOrFindMovie(file,movie,limitPopups);
        } else {
            LOG.log(Level.INFO,"FILE " + file.toString() + "      IS NOT A VIDEO FILE.");
        }
        return cancelAll;
    }

    protected void done() {

    }
}
