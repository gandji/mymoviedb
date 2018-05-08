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
public class MovieDescriptionDialog extends JDialog implements MovieHolder {

    MovieDescriptionPanel movieDescriptionPanel;

    JFrame parent;

    public MovieDescriptionDialog(JFrame parent, boolean modal, MovieDescriptionPanel panel) throws HeadlessException {
        super(parent, modal);
        this.parent = parent;
        this.movieDescriptionPanel = panel;
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
    public void close() {setVisible(false);}

}
