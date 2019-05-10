package org.gandji.mymoviedb.gui.widgets;

import org.gandji.mymoviedb.data.HibernateActorDao;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.HibernateVideoFileDao;
import org.gandji.mymoviedb.data.repositories.MovieRepository;
import org.gandji.mymoviedb.gui.MovieDataModelPoster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

/**
 * Created by gandji on 02/04/2018.
 */
@Component
public class StatisticsPanel extends JPanel {
    Logger LOG = LoggerFactory.getLogger(StatisticsPanel.class);

    /* need this one two trick spring into instantiating
     * the movie repository early
     */
    @Autowired
    MovieRepository movieRepository;

    @Autowired
    private MovieDataModelPoster movieDataModelPoster;

    @Autowired
    HibernateMovieDao hibernateMovieDao;

    @Autowired
    HibernateActorDao hibernateActorDao;

    @Autowired
    HibernateVideoFileDao hibernateVideoFileDao;

    @Autowired
    HistogramPerActorPanel actorsPanel;

    @Autowired
    HistogramPerGenrePanel genresPanel;

    public StatisticsPanel() throws HeadlessException {
    }

    private JLabel movieCountLabel;
    private JLabel fileCountLabel;
    private JLabel actorsCountLabel;

    private JLabel movieCountWidget;
    private JLabel fileCountWidget;
    private JLabel actorCountWidget;

    @PostConstruct
    private void postConstruct() {
        build();
        refresh();
    }

    private void build() {

        removeAll();

        movieCountLabel = new JLabel("Movies");
        fileCountLabel = new JLabel("Files");
        actorsCountLabel = new JLabel("Actors");

        movieCountWidget = new JLabel();
        fileCountWidget = new JLabel();
        actorCountWidget = new JLabel();

        GridLayout gridLayout = new GridLayout(3,2);
        Panel topPanel = new Panel(gridLayout);
        topPanel.add(movieCountLabel); topPanel.add(movieCountWidget);
        topPanel.add(fileCountLabel); topPanel.add( fileCountWidget);
        topPanel.add(actorsCountLabel);  topPanel.add( actorCountWidget);

        JTabbedPane bottomPanel = new JTabbedPane();

        bottomPanel.addTab("Actors",actorsPanel.getComponent());

        bottomPanel.addTab("Genres",genresPanel.getComponent());


        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(topPanel);
        add(bottomPanel);
    }

    public void refresh() {
        /* the hibernate movie dao is instantiated early enough,
         * but without the movie repository!!!
         * using the movie repository here is OK, though.
         * I guess the HibernateMovieDao implementations have not called
         * setMovieRepository yet...
         */
        movieCountWidget.setText(String.format("%d", movieRepository.count()));
        fileCountWidget.setText(String.format("%d", hibernateVideoFileDao.count()));
        actorCountWidget.setText(String.format("%d",hibernateActorDao.count()));

        actorsPanel.refresh();
        genresPanel.refresh();

    }
}
