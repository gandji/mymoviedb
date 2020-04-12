/*
 * Copyright (C) 2017 gandji <gandji@free.fr>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.gandji.mymoviedb.gui;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.gui.widgets.NewLayout;
import org.gandji.mymoviedb.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;

import javax.swing.*;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Slf4j
public abstract class MyMovieDBRunner implements CommandLineRunner{

    @Autowired
    private NewLayout newLayout;

    @Autowired
    private MovieDataModelPoster movieDataModelPoster;

    @Autowired
    private HibernateMovieDao hibernateMovieDao;

    @Autowired
    private HibernateKerDao hibernateKerDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(String... strings) throws Exception {

        vendorSpecificDatabaseInitialization();

        boolean useSwingApp = false;
        for (String activeProfile : applicationContext.getEnvironment().getActiveProfiles()) {
            if (activeProfile.equals("swing")){
                useSwingApp = true;
            }
        }

        if (!useSwingApp) {
            return;
        }

        //additionalLoggingConfiguration();

        populateWithDefaultRegexps();
        movieDataModelPoster.setMovies(hibernateMovieDao.findAllByOrderByCreated(0, 300).getContent());
        movieDataModelPoster.fireTableDataChanged();

        /* display the form using the AWT EventQueue */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                newLayout.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                newLayout.setVisible(true);

            }
        });

    }

    protected abstract void vendorSpecificDatabaseInitialization();

    /*
     REMOVE
     */
    @Deprecated
    public void additionalLoggingConfiguration() {
        // plug the log display
        //javaUtilLogAppender = new JavaUtilLogAppender(logDisplay.getLogTextArea());
        //log.getParent().addHandler(javaUtilLogAppender);
    }

    public void populateWithDefaultRegexps() {
        Iterable<String> dropRegexs =
    Arrays.asList(
        ".*x264.*","HDTV","BluRay","720[Pp]",".*1080[Pp].*",
            "MULT[Ii]","AC3","[xX][vV][iI][dD]","\\[\\w+\\]",
            "([Bb][dD]|[Bb][Rr]|[Dd][Vv][Dd])[Rr][Ii][Pp]","AYMO","PROPER","F[rR][eE][nN][cC][hH]",
            "TRUEFRENCH","AAC","PopHD","YIFY",
                        "\\s", "\\([^)]+", "[^)]+\\)$", "^$",
                        "SVR", "afrique31","HDLIGHT",".*[eE][mM][uU][lL][eE].*",
                        ".*[dD][iI][vV][xX].*",".*[dD][vV][dD].*","[cC][dD][12]",
                        "[lLe]","[lL]a","[lL]es",
                        "[mM][hH][dD]","JOKPIC","gismo65"
                );
        for (String dropk : dropRegexs) {
            try {
                List<KeywordExcludeRegexp> kl = hibernateKerDao.findByRegexpString(dropk);
                if (kl.isEmpty()) {
                    KeywordExcludeRegexp ker = new KeywordExcludeRegexp(dropk);
                    hibernateKerDao.save(ker);
                }
            } catch (DataIntegrityViolationException dv) {
                // la table contient deja la regexp, cest pas grave

            }
        }
        log.debug("Populated default keyword exclude regexps");
    }

}
