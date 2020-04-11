package org.gandji.mymoviedb.services;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.MyMovieDBConfiguration;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.HibernateVideoFileDao;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.data.VideoFile;
import org.gandji.mymoviedb.filefinder.VideoFileWorker;
import org.gandji.mymoviedb.gui.ScanADirectoryWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;

@Component
@Slf4j
public class LaunchServices {

    @Autowired
    HibernateMovieDao hibernateMovieDao;

    @Autowired
    HibernateVideoFileDao hibernateVideoFileDao;

    @Autowired
    ApplicationContext applicationContext;

    public void addFileInBackground(Movie movie, boolean limitPopups) {
        Preferences prefs = Preferences.userNodeForPackage(MyMovieDBConfiguration.class);
        String lastDir = prefs.get("lastdirused", System.getProperty("user.home") + "/Downloads/Video");

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(lastDir));
        int rep = chooser.showOpenDialog(null);
        if (rep == JFileChooser.APPROVE_OPTION) {
            try {
                prefs.put("lastdirused",chooser.getSelectedFile().getParent());

                Path fileToProcess = chooser.getSelectedFile().toPath();

                VideoFileWorker videoFileWorker = (VideoFileWorker) applicationContext.getBean("videoFileWorker");
                videoFileWorker.setFile(fileToProcess);
                videoFileWorker.setLimitPopups(limitPopups);
                videoFileWorker.setMovie(movie);

                videoFileWorker.execute();

            } catch (Exception ex) {
                log.info("Cannot access file " + chooser.getSelectedFile().toString(), ex);
            }
        }
    }

    public void scanADirectoryInBackground() {
        Preferences prefs = Preferences.userNodeForPackage(MyMovieDBConfiguration.class);
        String lastDir = prefs.get("lastdirused", System.getProperty("user.home") + "/Downloads/Video");

        /*
        In this case, you have two choices.
        You could provide SwingWorker with a callback interface,
        which it would be able to call from done
        once the SwingWorker has completed.

        Or you could attach a PropertyChangeListener to the SwingWorker
        and monitor the state value, waiting until it equals StateValue.Done
        */
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(lastDir));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Choose directory");
        chooser.setAcceptAllFileFilterUsed(false);
        int rep = chooser.showOpenDialog(null);
        if (rep == JFileChooser.APPROVE_OPTION) {
            prefs.put("lastdirused", chooser.getSelectedFile().getParent());
            Path dirToProcess = chooser.getSelectedFile().toPath();
            ScanADirectoryWorker sdw = (ScanADirectoryWorker) applicationContext.getBean("scanADirectoryWorker");
            sdw.setDirToProcess(dirToProcess);
            sdw.execute();

        } else /* CANCEL_OPTION or ERROR_OPTION*/ {
            //System.out.println("No Selection ");
        }
    }

    public void deleteMovie(Movie movie, JComponent parent) {
        Object[] options = {"Remove movie and files from database (do not delete files)",
                "Remove movie and files from database and delete the files!",
                "Cancel"};
        int reply = JOptionPane.showOptionDialog(parent,
                "Are ou sure to delete movie " + movie.getTitle() + "?",
                "Delete a movie",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]
        );
        boolean deleteInDB = false;
        boolean deleteRealFiles = false;
        if (reply == JOptionPane.YES_OPTION) {
            deleteInDB = true;
            deleteRealFiles = false;
            log.info("Deleting movie from database: " + movie.getTitle());
        } else if (reply == JOptionPane.NO_OPTION) {
            log.info("Deleting movie and files for " + movie.getTitle());
            deleteInDB = true;
            deleteRealFiles = true;
        }
        if (deleteInDB) {
            List<VideoFile> vfs = hibernateMovieDao.findVideoFilesForMovie(movie);
            for (VideoFile vf : vfs) {
                log.debug("   removing file from DB: " + vf.getFileName());
                hibernateVideoFileDao.deleteFile(vf);
                if (deleteRealFiles) {
                    try {
                        log.debug("   deleting file " + vf.getFileName());
                        Files.deleteIfExists(Paths.get(vf.getDirectory(), vf.getFileName()));
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(parent,
                                "Could not delete file " + vf.getFileName() + "\nSee log for details",
                                "Warning",
                                JOptionPane.WARNING_MESSAGE);
                        e1.printStackTrace();
                    }
                }
            }
            hibernateMovieDao.deleteMovie(movie);
        }

    }

}
