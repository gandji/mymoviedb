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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

import org.gandji.mymoviedb.gui.widgets.NewLayout;
import org.gandji.mymoviedb.services.MovieFileGuiServices;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.filefinder.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
@Scope("prototype")
public class ScanADirectoryWorker extends SwingWorker<Integer, Movie>  {

    private static final Logger LOG = Logger.getLogger(ScanADirectoryWorker.class.getName());

    private Path dirToProcess;
    
    @Autowired
    private MovieFileGuiServices movieFileGuiServices;

    @Autowired
    private NewLayout mainFrame;

    public void setDirToProcess(Path dirToProcess) {
        this.dirToProcess = dirToProcess;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        try {
            Path res = Files.walkFileTree(dirToProcess, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    boolean isVideoFile = FileUtils.isVideoFile(file);

                    Integer allCancelled = 0;
                    if (isVideoFile) {
                        allCancelled = movieFileGuiServices.addFileNoMovie(file, true,mainFrame);
                    }
                    if (1 == allCancelled) {
                        return FileVisitResult.TERMINATE;
                    } else {
                        return FileVisitResult.CONTINUE;
                    }
                }
            });
        } catch (Exception e) {
            LOG.severe("Error while processing directory: " + dirToProcess+" Exception : "+e+" message: "+e.getMessage());
        }
        return 0;
    }

}
