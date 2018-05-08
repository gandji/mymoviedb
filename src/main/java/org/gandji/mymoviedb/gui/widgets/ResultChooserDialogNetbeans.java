/*
 * Copyright (C) 2017 gandji <gandji@free.fr>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gandji.mymoviedb.gui.widgets;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.gui.MovieDataModel;
import org.gandji.mymoviedb.gui.MovieDataModelText;
import org.gandji.mymoviedb.gui.MovieGuiService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
@Scope("prototype")
public class ResultChooserDialogNetbeans extends javax.swing.JDialog implements ApplicationContextAware, TableModelListener {

    private static final Logger LOG = Logger.getLogger(ResultChooserDialog.class.getName());

    private Path filePath = null;
    private Movie selectedMovie = null;
    private boolean cancelAll = false;
    private boolean cancel = false;
    private boolean Ok = false;
    private ApplicationContext applicationContext = null;
    // @todo find better way to get to my parent
    private NewLayout parent;

    @Autowired
    private MovieGuiService movieGuiService;

    @Autowired
    private MovieDataModelText movieDataModel;

    public void setLocalDb() {
        infoLabel.setText("Movies found in local DB, select the right movie in this list, or press cancel if none.");
    }
    
    public void setImdb() {
        infoLabel.setText("Movies found in IMDB, please select the right movie or cancel if none suits: ");
    }

    public boolean isCancelAll() { return cancelAll; }

    public void setAllCancelled(boolean cancelAll) {
        this.cancelAll = cancelAll;
    }

    public boolean isCancel() { return cancel; }

    public void setCancel(boolean cancel) { this.cancel = cancel; }

    public boolean isOk() { return Ok; }

    public void setOk(boolean ok) { Ok = ok; }

    /**
     * Creates new form ResultChooserDialog
     */
    public ResultChooserDialogNetbeans(NewLayout parent) {
        super(parent, true /* modal */);
        this.parent = parent;
        initComponents();
        // default is to tell main frame:
        setImdb();
    }

    @PostConstruct
    public void postConstruct() {
        table.getModel().addTableModelListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        fileNameDisplay = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        infoLabel = new javax.swing.JLabel();
        playFileButton = new javax.swing.JButton();
        cancelAllButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        enterMovieMyselfButton = new javax.swing.JButton();
        gotoImdbPageButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Select movie for file :");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(table);

        playFileButton.setText("play file");
        playFileButton.setFocusable(false);
        playFileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playFileButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        playFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playFileButtonActionPerformed(evt);
            }
        });

        cancelAllButton.setText("Cancel All");
        cancelAllButton.setFocusable(false);
        cancelAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cancelAllButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cancelAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelAllButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setFocusable(false);
        cancelButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cancelButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.setFocusable(false);
        okButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        okButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        enterMovieMyselfButton.setText("Enter movie myself");
        enterMovieMyselfButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterMovieMyselfButtonActionPerformed(evt);
            }
        });

        gotoImdbPageButton.setText("Open IMDB");
        gotoImdbPageButton.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gotoImdbPage(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fileNameDisplay))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelAllButton)
                        .addGap(18, 18, 18)
                        .addComponent(enterMovieMyselfButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(playFileButton).addComponent(gotoImdbPageButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileNameDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cancelAllButton)
                    .addComponent(playFileButton)
                    .addComponent(cancelButton)
                    .addComponent(okButton)
                    .addComponent(enterMovieMyselfButton)
                .addComponent(gotoImdbPageButton))
                .addGap(30, 30, 30))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        setOk(true);
        setCancel(false);
        setAllCancelled(false);

        selectedMovie = (Movie) table.getModel().getValueAt(table.getSelectedRow(), -1);
        if (null == selectedMovie) {
            String message = "Please select a movie or press cancel.";
            JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (null == this.filePath) {
            String message = "Error: I have no file for this movie!";
            JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
                    JOptionPane.ERROR_MESSAGE);
        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        selectedMovie = null;
        setCancel(true);
        setOk(false);
        setAllCancelled(false);
        setVisible(false);
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void cancelAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelAllButtonActionPerformed
        selectedMovie = null;
        setOk(false);
        setCancel(false);
        setAllCancelled(true);
        setVisible(false);
        dispose();
    }//GEN-LAST:event_cancelAllButtonActionPerformed

    private void playFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playFileButtonActionPerformed
        Desktop dt = Desktop.getDesktop();
        try {
            dt.open(filePath.toFile());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Cannot open file " + filePath.getFileName() + "\n"
                    + "Make sure the file is there, and that it is associated to a video player.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_playFileButtonActionPerformed

    private void gotoImdbPage(java.awt.event.ActionEvent event) {
        selectedMovie = (Movie) table.getModel().getValueAt(table.getSelectedRow(), -1);
        movieGuiService.openInfoUrl(this.selectedMovie);
    }

    private void enterMovieMyselfButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterMovieMyselfButtonActionPerformed
        LOG.info("Instantiating user input");
        UserInputMovie userInputMovie = (UserInputMovie) applicationContext.getBean("userInputMovie",parent,true);
        userInputMovie.setFile(filePath);
        userInputMovie.setText("No IMDB movie found for : "+filePath.getFileName().toString());
        userInputMovie.setVisible(true);
        dispose();
        
    }//GEN-LAST:event_enterMovieMyselfButtonActionPerformed

    public void setMovies(List<Movie> movies) {
        movieDataModel.setMovies(movies);
        movieDataModel.fireTableDataChanged();
    }

    public void setPath(Path path) {
        filePath = path;
        fileNameDisplay.setText(path.getFileName().toString());
    }

    public Movie getSelectedMovie() {
        return selectedMovie;
    }

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
            java.util.logging.Logger.getLogger(ResultChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ResultChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ResultChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ResultChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        /*java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ResultChooserDialog dialog = new ResultChooserDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });*/
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelAllButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton enterMovieMyselfButton;
    private javax.swing.JButton gotoImdbPageButton;
    private javax.swing.JTextField fileNameDisplay;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton okButton;
    private javax.swing.JButton playFileButton;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

    public void addMovie(Movie movie) {
        ((MovieDataModel) table.getModel()).addMovie(movie);
        ((MovieDataModel) table.getModel()).fireTableDataChanged();
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
    }

}
