package org.gandji.mymoviedb.gui.widgets;

import org.gandji.mymoviedb.data.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

/**
 * Created by gandji on 08/10/2017.
 */
@Component
@Scope("prototype")
public class MovieDescriptionWindow extends JFrame implements MovieHolder {

    @Autowired
    private MovieDescriptionPanel movieDescriptionPanel;

    public MovieDescriptionWindow() throws HeadlessException {
        super();
    }
    @PostConstruct
    public void postConstruct() {
        movieDescriptionPanel.setMovieHolder(this);
        setContentPane(movieDescriptionPanel.getPanel());
        pack();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    @Override
    public void setData(Movie movie) {
        movieDescriptionPanel.setData(movie);
    }

    @Override
    public void close() {
        setVisible(false);
    }

}
