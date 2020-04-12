package org.gandji.mymoviedb.gui.widgets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * Created by gandji on 27/10/2017.
 */
@Component
public class PreferencesWindow extends JFrame {

    @Autowired
    PreferencesPanel preferencesPanel;

    public PreferencesWindow() throws HeadlessException {
        super();
    }

    @PostConstruct
    public void postConstruct() {
        //preferencesPanel.setControllingWidget(this);
        setTitle("Preferences");
        setContentPane(preferencesPanel.getInternalPanel());
        pack();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    public Container getInternalContentPane() {
        return preferencesPanel.getInternalPanel();
    }

    public void configureForStandalone() {
        preferencesPanel.setControllingWidget(this);
        preferencesPanel.getInternalPanel().setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.RAISED),
                new EmptyBorder(10, 10, 10, 10)));
        setContentPane(getInternalContentPane());
        setLocationRelativeTo(null);
    }
}
