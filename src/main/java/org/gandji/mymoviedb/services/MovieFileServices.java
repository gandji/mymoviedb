package org.gandji.mymoviedb.services;

import org.gandji.mymoviedb.MyMovieDBPreferences;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.HibernateVideoFileDao;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.data.VideoFile;
import org.gandji.mymoviedb.filefinder.FileNameUtils;
import org.gandji.mymoviedb.filefinder.FileUtils;
import org.gandji.mymoviedb.gui.InternetInfoSearchWorker;
import org.gandji.mymoviedb.gui.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class MovieFileServices {

    private static final Logger LOG = LoggerFactory.getLogger(MovieFileServices.class);
    
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HibernateMovieDao hibernateMovieDao;

    @Autowired
    private HibernateVideoFileDao videoFileDao;

    @Autowired
    private NewLayout mainFrame;

    @Autowired
    private FileNameUtils fileNameUtils;

    @Autowired
    private MyMovieDBPreferences myMovieDBPreferences;

    private ResultChooserDialog resultChooserDialog;

    public void updateVideoFile(VideoFile file, Path path) {
        file.setFileName(path.getFileName().toString());
        file.setDirectory(path.getParent().toString());
        file.setDriveLabel(file.computeCurrentDriveLabel());
        String hashCode = FileUtils.computeHash(path);
        file.setHashCode(hashCode);
    }

    public int addFileOrFindMovie(Path fileToProcess, Movie maybeMovie, boolean limitPopups) {

        // compute file hash
        String hashCode = FileUtils.computeHash(fileToProcess);
        // find file by hash, if it is already in DB, we have to react
        List<VideoFile> mflByHash = null;
        try {
            mflByHash = videoFileDao.findByHashCode(hashCode);
        } catch (Exception e) {
            LOG.info("Caught exception while searching by hash: " + e.getMessage());
            mflByHash = null;
        }

        // if we are given a movie, point to it
        if (null != maybeMovie) {
            // find movie anew
            List<Movie> possibleMovies = hibernateMovieDao.findByInfoUrl(maybeMovie.getInfoUrl());
            // if it is not there, it is a bug
            if (null == possibleMovies || possibleMovies.isEmpty()) {
                LOG.error("Cannot find movie in DB: " + maybeMovie.getId());
                return 0;
            }
            Movie movie = possibleMovies.get(0);

            if (null != mflByHash && !mflByHash.isEmpty()) {
                final VideoFile videoFileSameHash = mflByHash.get(0);
                // file is already in db
                // if it has the same movie, it is not a problem
                Movie fileMovie = videoFileDao.findMovieForFile(hashCode);
                LOG.info("File " + fileToProcess.getFileName().toString() + " is already in DB for movie " + fileMovie.getTitle());
                LOG.info("     movie is " + fileMovie);
                if (movie.getId().equals(fileMovie.getId())) {
                    // same file, same movie, nothing to do!
                    if (!limitPopups) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                JOptionPane optionPane = new JOptionPane(
                                        "File " + fileToProcess.getFileName().toString() + " is already in DB\n"
                                                + "under the name: " + videoFileSameHash.getFileName() + "\n"
                                                + " for movie " + fileMovie.getTitle(),
                                        JOptionPane.INFORMATION_MESSAGE);
                                JDialog dialog = optionPane.createDialog("Information");
                                dialog.setAlwaysOnTop(true); // to show top of all other application
                                dialog.setVisible(true); // to visible the dialog
                            }
                        });
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
                        LOG.info("Switching file " + fileToProcess.getFileName() + " over to movie " + movie.getTitle());
                        videoFileDao.deleteFile(videoFileSameHash);
                        hibernateMovieDao.updateOrCreateMovie(movie, fileToProcess);
                    }
                }
                return 0;
            }

            // ok, file is not in DB, add file to maybeMovie
            LOG.info("Adding file " + fileToProcess.toString() + " to movie: " + movie.getTitle());
            hibernateMovieDao.updateOrCreateMovie(movie, fileToProcess);
            return 0;
        }

        // we have no movie

        return addFileNoMovie(fileToProcess,limitPopups);
    }

    public int addFileNoMovie(Path file, boolean limitPopups) {
        // compute hash
        String hashCode = FileUtils.computeHash(file);

        // try to find file in db, by hash
        List<VideoFile> mflByHash = null;
        try {
            mflByHash = videoFileDao.findByHashCode(hashCode);
        } catch (Exception e) {
            LOG.error("Exception while searching by hash: " + e.getMessage());
            mflByHash = null;
        }
        if (null != mflByHash && !mflByHash.isEmpty()) {
            final VideoFile videoFileSameHash = mflByHash.get(0);
            // file is already in db
            LOG.info("File " + file.getFileName().toString() + " is already in DB with path: "
            +videoFileSameHash.getDirectory()+" -- "+videoFileSameHash.getFileName());
            if (!myMovieDBPreferences.isKeepDuplicateFilesOnScan()) {
                // replace old file path
                LOG.info("Updating file info in DB from "+videoFileSameHash.getFileName()+" to "+file.getFileName().toString());
                updateVideoFile(videoFileSameHash,file);
                videoFileDao.save(videoFileSameHash);
            } else {
                // find movie
                Movie movie = videoFileDao.findMovieForFile(videoFileSameHash.getHashCode());
                // add file to movie
                if (null!=movie) {
                    LOG.info("Adding file to movie: "+movie.getTitle());
                    VideoFile newVideoFile = new VideoFile();
                    updateVideoFile(newVideoFile,file);
                    newVideoFile.setQualiteVideo(videoFileSameHash.getQualiteVideo());
                    newVideoFile.setVersion(videoFileSameHash.getVersion());
                    hibernateMovieDao.updateOrCreateMovie(movie, newVideoFile);
                } else {
                    LOG.info("Could not find movie for file : "+videoFileSameHash.getFileName());
                }
            }
            if (!limitPopups) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane optionPane = new JOptionPane(
                                "File " + file.getFileName().toString() + " is already in DB\n"
                                        + "under the name: " + videoFileSameHash.getFileName(),
                                JOptionPane.INFORMATION_MESSAGE);
                        JDialog dialog = optionPane.createDialog("Information");
                        dialog.setAlwaysOnTop(true); // to show top of all other application
                        dialog.setVisible(true); // to visible the dialog
                    }
                });
            }
            return 0;
        }
        // hash was not found, try file name, you never know....
        LOG.info("File hash is not in DB for file "+file.getFileName().toString()+", try file name, you never know....");
        List<VideoFile> mfl = videoFileDao.findByFileName(file.getFileName().toString());
        if (!mfl.isEmpty()) {
            // @todo file name is already in db, but different hash!
            LOG.info("File with name " + file.getFileName().toString() + " is already in DB, but with different hash");
            if (!limitPopups) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane optionPane = new JOptionPane(
                                "A file with the same file name:\n"
                                        + file.getFileName().toString()
                                        + " is already in DB\n"
                                        + "but seems to be different!\n",
                                JOptionPane.ERROR_MESSAGE);
                        JDialog dialog = optionPane.createDialog("Information");
                        dialog.setAlwaysOnTop(true); // to show top of all other application
                        dialog.setVisible(true);
                    }
                });
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
        LOG.info("Searching DB for kwds: " + kwdsConcat);
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
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane optionPane = new JOptionPane(
                                "Exception caught while searching for: " + kwdsConcatCopy + "\n"
                                        + e.getMessage(), JOptionPane.INFORMATION_MESSAGE);
                        JDialog dialog = optionPane.createDialog("Information");
                        dialog.setAlwaysOnTop(true);
                        dialog.setVisible(true);
                    }
                });
            }
            e.printStackTrace();
            return 0;
        }
        final List<Movie> moviesList = tempList;
        if (moviesList != null && !moviesList.isEmpty()) {
            // use resultChooserDialog
            // to display the movies we found in DB
            // give user a chance to choose
            LOG.info("Found some movies in local DB for keywords " + kwdsConcat);
            for (Movie mov : moviesList) {
                LOG.info("   found: " + mov.getTitle() + " by " + mov.getDirector());
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
                LOG.debug("File processing totally cancelled");
                return 1;
            } else if (resultChooserDialog.isCancel()) {
                LOG.debug("No movie in local DB satisfies the user");
            } else if (resultChooserDialog.isEnterMovieMyself()) {
                launchMovieDescriptionDialog(file,mainFrame,true);
                return 0;
            } else {
                    // user pressed OK
                    Movie foundMovie = resultChooserDialog.getSelectedMovie();
                    if (null != foundMovie) {
                        // add file to DB
                        hibernateMovieDao.updateOrCreateMovie(foundMovie, file);
                        return 0;
                    } else {
                        LOG.error("No movie in local DB for file " + file.toString());
                        return 0;
                    }
            }
        } else {
            LOG.info("Found no movies in local db for keywords: "+kwds);
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
            LOG.info("No movie in IMDB satisfies the user");
            return 0;
        } else if (resultChooserDialog.isEnterMovieMyself()) {
            launchMovieDescriptionDialog(file,mainFrame,true);
            return 0;
        } else {
            // ok we have to save the movie
            Movie foundMovie = resultChooserDialog.getSelectedMovie();
            if (null != foundMovie) {
                // add file to DB
                hibernateMovieDao.updateOrCreateMovie(foundMovie, file);
                return 0;
            } else {
                LOG.info("No movie in IMDB for file " + file.toString());
                return 0;
            }
        }

    }

    private void launchMovieDescriptionDialog(Path path, JFrame parent, boolean modal) {
        MovieDescriptionPanel movieDescriptionPanel = (MovieDescriptionPanel) applicationContext.getBean("movieDescriptionPanel");
        MovieDescriptionDialog movieDescriptionDialog = (MovieDescriptionDialog) applicationContext.getBean("movieDescriptionDialog",parent, modal, movieDescriptionPanel);
        Movie movie = new Movie();
        VideoFile videoFile = new VideoFile();

        updateVideoFile(videoFile,path);
        movie.addFile(videoFile);

        movieDescriptionDialog.setModal(true);
        movieDescriptionDialog.setData(movie);
        movieDescriptionDialog.setVisible(true);

    }
}
