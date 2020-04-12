package org.gandji.mymoviedb.gui.widgets;

import org.gandji.mymoviedb.data.Movie;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

/**
 * Created by gandji on 08/10/2017.
 */
@Component
@Scope("prototype")
public class MovieDescriptionDialog extends JDialog {

    MovieDescriptionPanel movieDescriptionPanel;

    JFrame parent;

    private static String WINDOW_NAME = MovieDescriptionDialog.class.getSimpleName();

    public MovieDescriptionDialog(JFrame parent, boolean modal, MovieDescriptionPanel panel) throws HeadlessException {
        super(parent, modal);
        this.parent = parent;
        this.movieDescriptionPanel = panel;
    }
    @PostConstruct
    public void postConstruct() {
        setContentPane(movieDescriptionPanel.getPanel());
        pack();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setName(WINDOW_NAME);
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        setLocation(prefs.getInt(WINDOW_NAME+".x",50),
                prefs.getInt(WINDOW_NAME+".y", 50));
        setSize(prefs.getInt(WINDOW_NAME+".width",800),
                prefs.getInt(WINDOW_NAME+".height", 825));
        addComponentListener(new WindowSizeLocationPersist(prefs));
    }

    // REMOVE @Override
    public void setData(Movie movie) {
        movieDescriptionPanel.setData(movie);
    }

}
