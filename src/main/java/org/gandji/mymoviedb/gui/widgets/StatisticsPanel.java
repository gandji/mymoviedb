package org.gandji.mymoviedb.gui.widgets;

import org.gandji.mymoviedb.data.Actor;
import org.gandji.mymoviedb.data.repositories.ActorRepository;
import org.gandji.mymoviedb.data.repositories.MovieRepository;
import org.gandji.mymoviedb.data.repositories.VideoFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by gandji on 02/04/2018.
 */
@Component
public class StatisticsPanel extends JPanel {
    Logger LOG = LoggerFactory.getLogger(StatisticsPanel.class);

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    VideoFileRepository videoFileRepository;

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

        setLayout(gridLayout);
        add(movieCountLabel); add(movieCountWidget);
        add(fileCountLabel); add( fileCountWidget);
        add(actorsCountLabel);  add( actorCountWidget);
    }

    public void refresh() {
        movieCountWidget.setText(String.format("%d", movieRepository.count()));
        fileCountWidget.setText(String.format("%d", videoFileRepository.count()));
        countActors();
    }

    /**
     *  actors are duplicated, in fact one Actor in the actors table
     *  has only one movie.... FIXME
     *  so we count unique names to compute the true number of actors in the DB
     */
    private void countActors() {
        Iterator<Actor> actors = actorRepository.findAll().iterator();
        Set<String> names = new HashSet<>();
        actorRepository.findAll().forEach(actor -> names.add(actor.getName()));
        actorCountWidget.setText(String.format("%d",names.size()));
    }
}
