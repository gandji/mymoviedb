package org.gandji.mymoviedb.gui.widgets;

import javafx.geometry.VerticalDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneUI;
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

        GridLayout layout = new GridLayout(2,1);
        layout.setVgap(25);
        layout.setHgap(25);
        this.panel = new JPanel(layout);

        JLabel progamName = new JLabel("MyMovieDB");
        this.panel.add(progamName);
        progamName.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel versionString = new JLabel(myMovieDBVersion);
        this.panel.add(versionString);
        versionString.setHorizontalAlignment(SwingConstants.CENTER);

        this.panel.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
        setContentPane(this.panel);
        pack();
    }
}
