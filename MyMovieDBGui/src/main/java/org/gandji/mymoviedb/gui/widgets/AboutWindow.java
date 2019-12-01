package org.gandji.mymoviedb.gui.widgets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

/**
 * Created by gandji on 07/05/2018.
 */
@Component
public class AboutWindow extends JFrame {

    @Autowired
    private String myMovieDBVersion;

    private JPanel panel;

    public AboutWindow() {
        super("About MyMovieDB");
    }

    @PostConstruct
    public void init() {

        GridLayout layout = new GridLayout(4,1);
        layout.setVgap(25);
        layout.setHgap(25);
        this.panel = new JPanel(layout);

        JLabel progamName = new JLabel("MyMovieDB");
        this.panel.add(progamName);
        progamName.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel versionString = new JLabel(myMovieDBVersion);
        this.panel.add(versionString);
        versionString.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel authorString = new JLabel("gandji");
        this.panel.add(authorString);
        authorString.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel githubString = new JLabel("https://github.com/gandji/mymoviedb");
        this.panel.add(githubString);
        githubString.setHorizontalAlignment(SwingConstants.CENTER);

        this.panel.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
        setContentPane(this.panel);
        pack();
    }
}
