package org.gandji.mymoviedb.services;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.MyMovieDBPreferences;
import org.gandji.mymoviedb.gui.widgets.MovieDescriptionDialog;
import org.gandji.mymoviedb.gui.widgets.MovieDescriptionPanel;
import org.gandji.mymoviedb.gui.widgets.NewLayout;
import org.gandji.mymoviedb.gui.widgets.ResultChooserDialog;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.HibernateVideoFileDao;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.data.VideoFile;
import org.gandji.mymoviedb.filefinder.FileNameUtils;
import org.gandji.mymoviedb.filefinder.FileUtils;
import org.gandji.mymoviedb.gui.InternetInfoSearchWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gandji on 30/09/2017.
 */
@Component
@Slf4j
public class MovieDaoGuiServices extends MovieDaoServices {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HibernateMovieDao hibernateMovieDao;

    @Autowired
    private DialogsService dialogsService;

    @Autowired
    private HibernateVideoFileDao videoFileDao;

    @Autowired
    private FileNameUtils fileNameUtils;

    @Autowired
    private MyMovieDBPreferences myMovieDBPreferences;

    private ResultChooserDialog resultChooserDialog;

    /**
     * Movie could be null, or could be id==null
     */
    public int addFileOrFindMovieInfo(Path fileToProcess, Movie maybeMovie, boolean limitPopups, NewLayout mainFrame) {

        // compute file hash
        String hashCode = FileUtils.computeHash(fileToProcess);
        // find file by hash, if it is already in DB, we have to react
        List<VideoFile> mflByHash = null;
        try {
            mflByHash = videoFileDao.findByHashCode(hashCode);
        } catch (Exception e) {
            log.info("Caught exception while searching by hash: " + e.getMessage());
            mflByHash = null;
        }

        // if we are given a movie, point to it
        if (null != maybeMovie) {
            // find movie anew
            List<Movie> possibleMovies = hibernateMovieDao.findByInfoUrl(maybeMovie.getInfoUrl());
            // if it is not there, it is a bug
            if (null == possibleMovies || possibleMovies.isEmpty()) {
                log.error("Cannot find movie in DB: " + maybeMovie.getId());
                return 0;
            }
            Movie movie = possibleMovies.get(0);

            if (null != mflByHash && !mflByHash.isEmpty()) {
                final VideoFile videoFileSameHash = mflByHash.get(0);
                // file is already in db
                // if it has the same movie, it is not a problem
                Movie fileMovie = videoFileDao.findMovieForFile(hashCode);
                log.info("File " + fileToProcess.getFileName().toString() + " is already in DB for movie " + fileMovie.getTitle());
                log.info("     movie is " + fileMovie);
                if (movie.getId().equals(fileMovie.getId())) {
                    // same file, same movie, nothing to do!
                    if (!limitPopups) {
                        dialogsService.showMessageDialog(null,
                                "File " + fileToProcess.getFileName().toString() + " is already in DB\n"
                                        + "under the name: " + videoFileSameHash.getFileName() + "\n"
                                        + " for movie " + fileMovie.getTitle(),
                                "Information", DialogsService.MessageType.INFO);
                    }
                } else {
                    // file is already in DB, but with different movie
                    // ask user what to do
                    Object[] options = {"Change file to refer to movie " + movie.getTitle(),
                            "Leave as it is"};
                    Integer n = JOptionPane.showOptionDialog(mainFrame,
                            "File " + fileToProcess.getFileName() + "\n"
                                    + "is already in DB under the name : " + videoFileSameHash.getFileName() + "\n"
                                    + "for a different movie: " + fileMovie.getTitle() + "\n",
                            "File already in local DB",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);
                    if (0 == n) {
                        // ok, we change the movie for this file
                        log.info("Switching file " + fileToProcess.getFileName() + " over to movie " + movie.getTitle());
                        videoFileDao.deleteFile(videoFileSameHash);
                        addFileToMovie(movie, fileToProcess);
                    }
                }
                return 0;
            }

            // ok, file is not in DB, add file to maybeMovie
            log.info("Adding file " + fileToProcess.toString() + " to movie: " + movie.getTitle());
            // REMOVE movie = checkActorsAndSaveMovie(movie);
            movie = addFileToMovie(movie, fileToProcess);
            return 0;
        }

        // we have no movie

        return addFileNoMovie(fileToProcess, mflByHash, limitPopups, mainFrame);
    }

    private int addFileNoMovie(Path file, List<VideoFile> mflByHash, boolean limitPopups, NewLayout mainFrame) {
        if (null != mflByHash && !mflByHash.isEmpty()) {
            final VideoFile videoFileSameHash = mflByHash.get(0);
            // file is already in db
            log.info("File " + file.getFileName().toString() + " is already in DB with path: "
            +videoFileSameHash.getDirectory()+" -- "+videoFileSameHash.getFileName());
            if (!myMovieDBPreferences.isKeepDuplicateFilesOnScan()) {
                // replace old file path
                log.info("Updating file info in DB from "+videoFileSameHash.getFileName()+" to "+file.getFileName().toString());
                populateVideoFile(videoFileSameHash,file);
                videoFileDao.save(videoFileSameHash);
            } else {
                // find movie
                Movie movie = videoFileDao.findMovieForFile(videoFileSameHash.getHashCode());
                // add file to movie
                if (null!=movie) {
                    log.info("Adding file to movie: "+movie.getTitle());
                    VideoFile newVideoFile = new VideoFile();
                    populateVideoFile(newVideoFile,file);
                    newVideoFile.setQualiteVideo(videoFileSameHash.getQualiteVideo());
                    newVideoFile.setVersion(videoFileSameHash.getVersion());
                    if (movie.getId() == null) {
                        movie = checkActorsAndSaveMovie(movie);
                    }
                    addFileToMovie(movie, newVideoFile);
                } else {
                    log.info("Could not find movie for file : "+videoFileSameHash.getFileName());
                }
            }
            if (!limitPopups) {
                dialogsService.showMessageDialog(null,
                        "File " + file.getFileName().toString() + " is already in DB\n"
                                + "under the name: " + videoFileSameHash.getFileName(),
                        "Information", DialogsService.MessageType.INFO);
            }
            return 0;
        }
        // hash was not found, try file name, you never know....
        log.info("File hash is not in DB for file "+file.getFileName().toString()+", try file name, you never know....");
        List<VideoFile> mfl = videoFileDao.findByFileName(file.getFileName().toString());
        if (!mfl.isEmpty()) {
            // @todo file name is already in db, but different hash!
            log.info("File with name " + file.getFileName().toString() + " is already in DB, but with different hash");
            if (!limitPopups) {
                dialogsService.showMessageDialog(null,
                        "A file with the same file name:\n"
                                + file.getFileName().toString()
                                + " is already in DB\n"
                                + "but seems to be different!\n",
                        "Information", DialogsService.MessageType.INFO);
            }
            return 0;
        }
        // ok, we have to extract title keywords
        ArrayList<String> kwds = fileNameUtils.extractKeywords(file.toFile());
        // assemble query string from keywords
        String kwdsConcat = kwds.get(0);
        for (Integer i = 1; i < kwds.size(); i++) {
            kwdsConcat = kwdsConcat + "+" + kwds.get(i);
        }
        String kwdsConcatCopy = new String(kwdsConcat.toString());// copy for messages
        // try to find movie in DB by title keywords
        log.info("Searching DB for kwds: " + kwdsConcat);
        List<Movie> tempList = null;
        try {
            // @todo this is ugly! we have to remove duplicates!
            tempList = hibernateMovieDao.findByTitleKeywords(kwdsConcat);
            tempList.addAll(hibernateMovieDao.findByAlternateTitleKeywords(kwdsConcat));
            // remove duplicates
            Set<Long> uniqueMovieIds = new HashSet<>();
            List<Movie> uniqueMovies = new ArrayList<>();
            for (Movie movie : tempList) {
                if (!uniqueMovieIds.contains(movie.getId())) {
                    uniqueMovieIds.add(movie.getId());
                    uniqueMovies.add(movie);
                }
            }
            tempList = uniqueMovies;
        } catch (Exception e) {
            // error while interrogating DB
            if (!limitPopups) {
                dialogsService.showMessageDialog(null,
                        "Exception caught while searching for: " + kwdsConcatCopy + "\n"
                                + e.getMessage(),
                        "Information", DialogsService.MessageType.INFO);
            }
            e.printStackTrace();
            // it's OK, we have not found in DB, go on
        }
        final List<Movie> moviesList = tempList;
        if (moviesList != null && !moviesList.isEmpty()) {
            // use resultChooserDialog
            // to display the movies we found in DB
            // give user a chance to choose
            log.info("Found some movies in local DB for keywords " + kwdsConcat);
            for (Movie mov : moviesList) {
                log.info("   found: " + mov.getTitle() + " by " + mov.getDirector());
            }
            // we run dialog and wait
            resultChooserDialog = (ResultChooserDialog) applicationContext.getBean("resultChooserDialog");
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        resultChooserDialog.setLocalDb();
                        resultChooserDialog.setPath(file);
                        resultChooserDialog.setMovies(moviesList);
                        resultChooserDialog.setVisible(true);
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
                return 0;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return 0;
            }
            // if we have been cancelled:
            if (resultChooserDialog.isCancelAll()) {
                log.debug("File processing totally cancelled");
                return 1;
            } else if (resultChooserDialog.isCancel()) {
                log.debug("No movie in local DB satisfies the user");
            } else if (resultChooserDialog.isEnterMovieMyself()) {
                launchMovieDescriptionDialog(file,mainFrame,true);
                return 0;
            } else {
                    // user pressed OK
                    Movie foundMovie = resultChooserDialog.getSelectedMovie();
                    if (null != foundMovie) {
                        // add file to DB
                        if (foundMovie.getId()==null) {
                            foundMovie = checkActorsAndSaveMovie(foundMovie);
                        }
                        foundMovie = addFileToMovie(foundMovie, file);
                        return 0;
                    } else {
                        log.error("No movie in local DB for file " + file.toString());
                        return 0;
                    }
            }
        } else {
            log.info("Found no movies in local db for keywords: "+kwds);
        }

        // before searching imdb, we have to set up the resultChooserDialog with empty movie list
        List<Movie> moviesList2 = new ArrayList<Movie>();
        moviesList2.clear();

        if (resultChooserDialog==null) {
            resultChooserDialog = (ResultChooserDialog) applicationContext.getBean("resultChooserDialog");
        }

        // title keywords not found in local DB, try search on imdb
        InternetInfoSearchWorker internetInfoSearchWorker = (InternetInfoSearchWorker) applicationContext.getBean("internetInfoSearchWorker");
        internetInfoSearchWorker.setFile(file);
        internetInfoSearchWorker.setKwds(kwds);
        internetInfoSearchWorker.setResultChooserDialog(resultChooserDialog);
        internetInfoSearchWorker.execute();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    resultChooserDialog.setInternetMovieInfoSearching();
                    resultChooserDialog.setPath(file);
                    resultChooserDialog.setMovies(moviesList2);
                    resultChooserDialog.setInternetInfoSearchWorker(internetInfoSearchWorker);
                    resultChooserDialog.setVisible(true);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (resultChooserDialog.isCancelAll()) {
            internetInfoSearchWorker.setCancelRequested(true);
            return 1;
        } else if (resultChooserDialog.isCancel()) {
            internetInfoSearchWorker.setCancelRequested(true);
            log.info("No movie in IMDB satisfies the user");
            return 0;
        } else if (resultChooserDialog.isEnterMovieMyself()) {
            launchMovieDescriptionDialog(file,mainFrame,true);
            return 0;
        } else {
            // ok we have to save the movie
            Movie foundMovie = resultChooserDialog.getSelectedMovie();
            if (null != foundMovie) {
                // add file to DB
                if (foundMovie.getId()==null) {
                    foundMovie = checkActorsAndSaveMovie(foundMovie);
                }
                foundMovie = addFileToMovie(foundMovie, file);
                return 0;
            } else {
                log.info("No movie in IMDB for file " + file.toString());
                return 0;
            }
        }

    }

    private void launchMovieDescriptionDialog(Path path, JFrame parent, boolean modal) {
        MovieDescriptionPanel movieDescriptionPanel = (MovieDescriptionPanel) applicationContext.getBean("movieDescriptionPanel");
        MovieDescriptionDialog movieDescriptionDialog = (MovieDescriptionDialog) applicationContext.getBean("movieDescriptionDialog",parent, modal, movieDescriptionPanel);
        Movie movie = new Movie();
        VideoFile videoFile = new VideoFile();

        populateVideoFile(videoFile,path);
        movie.addFile(videoFile);

        movieDescriptionDialog.setModal(true);
        movieDescriptionDialog.setData(movie);
        movieDescriptionDialog.setVisible(true);

    }
}
