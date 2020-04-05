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
package org.gandji.mymoviedb.gui.widgets;

import java.lang.reflect.MalformedParametersException;
import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.scrapy.MovieInfoSearchService;
import org.gandji.mymoviedb.services.MovieDaoGuiServices;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.data.VideoFile;
import org.gandji.mymoviedb.gui.MovieGuiService;
import org.gandji.mymoviedb.services.MovieDaoServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
@Scope("prototype")
@Slf4j
public class UserInputMovie extends javax.swing.JDialog {

    @Autowired
    private MovieInfoSearchService movieInfoSearchService;

    @Autowired
    private MovieGuiService movieGuiService;

    private Path file;
    private MovieHolder movieHolder;

    @Autowired
    private MovieDaoGuiServices movieFileGuiServices;

    @Autowired
    private MovieDaoServices movieDaoServices;

    public void setFile(Path file) { this.file = file; }

    public void setMovieHolder(MovieHolder movieHolder) { this.movieHolder = movieHolder; }

    public void setText(String text) {
        this.firstLineLabel.setText(text);
    }

    /**
     * Creates new form UserInputMovie
     */
    public UserInputMovie(NewLayout mainFrame) {
        super(mainFrame, true);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        firstLineLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        urlInput = new javax.swing.JTextField();
        playTheFileButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        firstLineLabel.setText("No IMDB results for: ");

        jLabel2.setText("Please search TMDB yourself and enter url here:");

        urlInput.setText("Enter tmbd url in the form : http://www.themoviedb.org/movie/1234-titre-du-film");

        playTheFileButton.setText("Play the file");
        playTheFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playTheFileButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(urlInput, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(firstLineLabel)
                            .addComponent(jLabel2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(playTheFileButton)
                                .addGap(30, 30, 30)
                                .addComponent(okButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(firstLineLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(urlInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(playTheFileButton)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void playTheFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playTheFileButtonActionPerformed
        if (null != this.file) {
            movieGuiService.playTheFile(this.file);
        }
    }//GEN-LAST:event_playTheFileButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        String userInfoUrl = urlInput.getText();

        if (userInfoUrl==null || userInfoUrl.length()<=0) {
            return;
        }

            log.info("Adding movie from Internet Info url " + userInfoUrl);
            Movie movie =null;

            try {
                movie = movieInfoSearchService.getOneFilmFromUrl(userInfoUrl);
            } catch (MalformedParametersException e) {
                // treated below
            }

            if(null==movie) {
                MovieInfoSearchService.UrlType db = MovieInfoSearchService.UrlType.fromUrlString(userInfoUrl);
                if (db==null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane optionPane = new JOptionPane(
                                    "Could not open URL: \n"
                                            + userInfoUrl + "\n"
                                            + "Check the URL and try again.",
                                    JOptionPane.WARNING_MESSAGE);
                            JDialog dialog = optionPane.createDialog("Warning");
                            dialog.setAlwaysOnTop(true); // to show top of all other application
                            dialog.setVisible(true);
                        }
                    });
                } else if (db== MovieInfoSearchService.UrlType.TMDB){
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane optionPane = new JOptionPane(
                                    "Could not open URL: \n"
                                            + userInfoUrl + "\n"
                                            + "Make sure you configured an API key for TMDB (sse docs).\n"
                                            + "If you already configured an API key for TMDB, check the url.",
                                    JOptionPane.WARNING_MESSAGE);
                            JDialog dialog = optionPane.createDialog("Warning");
                            dialog.setAlwaysOnTop(true); // to show top of all other application
                            dialog.setVisible(true);
                        }
                    });
                }
            }

            // update mdw if present, if not we are responsible for saving movie grrrr
            if (null != movieHolder) {
                if (file!=null) {
                    VideoFile videoFile = new VideoFile();
                    movieFileGuiServices.populateVideoFile(videoFile,file);
                    movie.addFile(videoFile);
                }
                movieHolder.setData(movie);
            } else {
                movie = movieDaoServices.checkActorsAndSaveMovie(movie);
                movie = movieDaoServices.addFileToMovie(movie, this.file);
            }
            this.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException|InstantiationException
                |IllegalAccessException|UnsupportedLookAndFeelException ex) {
            log.error(ex.getMessage());
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UserInputMovie dialog = new UserInputMovie(new NewLayout());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel firstLineLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton okButton;
    private javax.swing.JButton playTheFileButton;
    private javax.swing.JTextField urlInput;
    // End of variables declaration//GEN-END:variables
}
