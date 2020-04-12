package org.gandji.mymoviedb.gui.widgets;

import lombok.extern.slf4j.Slf4j;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

@Slf4j
public class WindowSizeLocationPersist extends ComponentAdapter {

    final Preferences preferences;

    public WindowSizeLocationPersist(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public void componentResized(ComponentEvent e) {

        log.info(String.format("Window [%s] at %dx%d (+%d+%d)",
                e.getComponent().getName(),
                e.getComponent().getSize().width,
                e.getComponent().getSize().height,
                e.getComponent().getLocation().x,
                e.getComponent().getLocation().y));
        preferences.putInt(e.getComponent().getName()+".width",e.getComponent().getSize().width);
        preferences.putInt(e.getComponent().getName()+".height",e.getComponent().getSize().height);
        try {
            preferences.flush();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        log.info(String.format("Window [%s] at %dx%d (+%d+%d)",
                e.getComponent().getName(),
                e.getComponent().getSize().height,
                e.getComponent().getSize().width,
                e.getComponent().getLocation().x,
                e.getComponent().getLocation().y));
        preferences.putInt(e.getComponent().getName()+".x",e.getComponent().getLocation().x);
        preferences.putInt(e.getComponent().getName()+".y",e.getComponent().getLocation().y);
        try {
            preferences.flush();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }}
