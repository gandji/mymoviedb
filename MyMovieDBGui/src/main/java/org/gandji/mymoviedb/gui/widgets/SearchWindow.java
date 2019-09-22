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

import java.awt.event.KeyEvent;
import java.util.*;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.swing.*;

import org.gandji.mymoviedb.data.*;
import org.gandji.mymoviedb.gui.MovieDataModelPoster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
public class SearchWindow extends javax.swing.JFrame {

    private static final Logger LOG = Logger.getLogger(SearchWindow.class.getName());
    
    @Autowired
    private HibernateGenreDao hibernateGenreDao;

    @Autowired
    private MovieDataModelPoster movieDataModelPoster;

    @Autowired
    private HibernateMovieDao hibernateMovieDao;

    /**
     * Creates new form SearchWindow
     */
    public SearchWindow() {
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleWindowLabel = new javax.swing.JLabel();
        titleKeywordsLabel = new javax.swing.JLabel();
        titleKeywordsInput = new javax.swing.JTextField();
        directorLabel = new javax.swing.JLabel();
        directorInput = new javax.swing.JTextField();
        /* closeButton = new javax.swing.JButton();*/
        searchButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        actorsLabel = new javax.swing.JLabel();
        actorsInput = new javax.swing.JTextField();
        genresLabel = new javax.swing.JLabel();
        genreComboBox = new javax.swing.JComboBox<>();
        qualiteVideoLabel = new javax.swing.JLabel();
        qualiteVideoComboBox = new javax.swing.JComboBox<>();
        commentsLabel = new javax.swing.JLabel();
        commentsInput = new javax.swing.JTextField();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        titleWindowLabel.setText("Search DB");

        titleKeywordsLabel.setText("Title keywords:");

        titleKeywordsInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                titleKeywordsInputKeyPressed(evt);
            }
        });

        directorLabel.setText("Director:");

        directorInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                directorInputKeyPressed(evt);
            }
        });

        /* closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });*/

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        actorsLabel.setText("Actor(s):");

        actorsInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                actorsInputKeyPressed(evt);
            }
        });

        genresLabel.setText("Genre:");

        qualiteVideoLabel.setText("File quality:");

        commentsLabel.setText("Comments:");

        commentsInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                directorInputKeyPressed(evt);
                directorInputKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(resetButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        //.addComponent(closeButton)
                    )
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(164, 164, 164)
                        .addComponent(titleWindowLabel)
                        .addGap(0, 297, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(directorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(titleKeywordsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(titleKeywordsInput)
                            .addComponent(directorInput))))
                .addGap(14, 14, 14))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(actorsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .addComponent(genresLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(commentsLabel, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(qualiteVideoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(actorsInput)
                    .addComponent(genreComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(commentsInput)
                    .addComponent(qualiteVideoComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleWindowLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleKeywordsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(titleKeywordsInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(directorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(directorInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(actorsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(actorsInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genresLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genreComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(commentsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(commentsInput, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(qualiteVideoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(qualiteVideoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    //.addComponent(closeButton)
                    .addComponent(searchButton)
                    .addComponent(resetButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @PostConstruct
    public void postConstruct() {
        genreComboBox.addItem("Any");
        List<Genre> genreList = hibernateGenreDao.findAll();
        for (Genre genre : genreList) {
            genreComboBox.addItem(genre.getName());
        }
        qualiteVideoComboBox.addItem("Any");
        for (VideoFile.QualiteVideo qualiteVideo : VideoFile.QualiteVideo.values()) {
            qualiteVideoComboBox.addItem(qualiteVideo.toString());
        }
        qualiteVideoComboBox.addItem("Unknown");
        pack();
    }

    /* private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed
    */

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        titleKeywordsInput.setText("");
        directorInput.setText("");
        actorsInput.setText("");
        commentsInput.setText("");
        genreComboBox.setSelectedIndex(0);
        qualiteVideoComboBox.setSelectedIndex(0);
        movieDataModelPoster.setMovies(hibernateMovieDao.findAllByOrderByCreated(0, 300).getContent());
        movieDataModelPoster.fireTableDataChanged();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        doSearch();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void titleKeywordsInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_titleKeywordsInputKeyPressed
        if (evt.getKeyCode()==KeyEvent.VK_ENTER) {
            doSearch();
        }
    }//GEN-LAST:event_titleKeywordsInputKeyPressed

    private void directorInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_directorInputKeyPressed
        if (evt.getKeyCode()==KeyEvent.VK_ENTER) {
            doSearch();
        }
    }//GEN-LAST:event_directorInputKeyPressed

    private void actorsInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_actorsInputKeyPressed
       if (evt.getKeyCode()==KeyEvent.VK_ENTER) {
            doSearch();
        }
    }//GEN-LAST:event_actorsInputKeyPressed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        genreComboBox.removeAllItems();
        genreComboBox.addItem("Any");
        List<Genre> genreList = hibernateGenreDao.findAll();
        for (Genre genre : genreList) {
            genreComboBox.addItem(genre.getName());
        }
        qualiteVideoComboBox.removeAllItems();
        qualiteVideoComboBox.addItem("Any");
        for (VideoFile.QualiteVideo qualiteVideo : VideoFile.QualiteVideo.values()) {
            qualiteVideoComboBox.addItem(qualiteVideo.toString());
        }
        qualiteVideoComboBox.addItem("Unknown");
    }//GEN-LAST:event_formComponentShown

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SearchWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SearchWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SearchWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SearchWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SearchWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField actorsInput;
    //private javax.swing.JButton closeButton;
    private javax.swing.JTextField directorInput;
    private javax.swing.JTextField commentsInput;
    private javax.swing.JComboBox<String> genreComboBox;
    private javax.swing.JComboBox<String> qualiteVideoComboBox;
    private javax.swing.JLabel titleWindowLabel;
    private javax.swing.JLabel titleKeywordsLabel;
    private javax.swing.JLabel directorLabel;
    private javax.swing.JLabel actorsLabel;
    private javax.swing.JLabel genresLabel;
    private javax.swing.JLabel qualiteVideoLabel;
    private javax.swing.JLabel commentsLabel;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField titleKeywordsInput;
    // End of variables declaration//GEN-END:variables

    private void doSearch() {
        String titleKeywords = titleKeywordsInput.getText();
        String directorKeywords = directorInput.getText();
        String actorsKeywords = actorsInput.getText();
        String genreKeyword = (String) genreComboBox.getSelectedItem();
        String commentsKeywords = commentsInput.getText();
        String qualiteVideoKeyword = (String) qualiteVideoComboBox.getSelectedItem();

        Iterable<Movie> uniqueMovies = hibernateMovieDao.searchInternal(titleKeywords,directorKeywords,
                actorsKeywords,genreKeyword,commentsKeywords,qualiteVideoKeyword);

        movieDataModelPoster.setMovies(uniqueMovies);
        movieDataModelPoster.fireTableDataChanged();
    }
}