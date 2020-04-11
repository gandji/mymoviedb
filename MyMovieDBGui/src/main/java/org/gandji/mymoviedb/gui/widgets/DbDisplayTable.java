package org.gandji.mymoviedb.gui.widgets;

import org.gandji.mymoviedb.gui.MovieDataModelPoster;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.gui.MovieGuiService;

import java.awt.*;
import java.util.logging.Logger;

import org.gandji.mymoviedb.services.LaunchServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by gandji on 22/11/2017.
 */
@Component
public class DbDisplayTable extends JTable implements ActionListener {

    Logger LOG = Logger.getLogger(DbDisplayTable.class.getName());
    
    // the popup for db display
    private JMenuItem menuItemAddAFileToMovie;
    private JMenuItem menuItemMovieDetails;
    private JMenuItem menuItemPlayMovie;
    private JMenuItem menuItemInternetCritics;
    private JMenuItem menuItemOpenInfoUrl;

    private final JLabel titleLabel = new JLabel("Films");

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MovieGuiService movieGuiService;

    @Autowired
    private LaunchServices launchServices;

    private JTabbedPane tabbedPane;

    @Autowired
    private MovieDataModelPoster movieDataModelPoster;

    public DbDisplayTable() {
    }

    @PostConstruct
    public void postInit() {

        // the tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);

        // construct the popup menu for film rows
        JPopupMenu popupMenu = new JPopupMenu();
        menuItemMovieDetails = new JMenuItem("Movie details");
        menuItemAddAFileToMovie = new JMenuItem("Add a file for this movie");
        menuItemPlayMovie = new JMenuItem("Play the file");
        menuItemInternetCritics = new JMenuItem("See the critics on internet");
        menuItemOpenInfoUrl = new JMenuItem("Open Internet Info Page");

        menuItemMovieDetails.addActionListener(this);
        menuItemAddAFileToMovie.addActionListener(this);
        menuItemPlayMovie.addActionListener(this);
        menuItemInternetCritics.addActionListener(this);
        menuItemOpenInfoUrl.addActionListener(this);

        popupMenu.add(menuItemMovieDetails);
        popupMenu.add(menuItemAddAFileToMovie);
        popupMenu.add(menuItemPlayMovie);
        popupMenu.add(menuItemInternetCritics);
        popupMenu.add(menuItemOpenInfoUrl);

        // sets the popup menu for the table
        setComponentPopupMenu(popupMenu);

        // configure table
        setAutoCreateRowSorter(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        setRowMargin(5);

        // ??
        // this.getModel().addTableModelListener(this);

        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                JTable table = (JTable) evt.getSource();
                Point p = evt.getPoint();
                int row = table.rowAtPoint(p);
                if (evt.getClickCount() == 2) {
                    displayMovieDetails(row);
                }
            }
        });

        // set data model
        this.setModel(movieDataModelPoster);

        for (Integer colIndex = 0; colIndex< movieDataModelPoster.getColumnCount(); colIndex++) {
            this.getColumnModel().getColumn(colIndex)
                    .setPreferredWidth(movieDataModelPoster.getPreferredWidth(colIndex));
        }
        // beurk!
        if (movieDataModelPoster.getRowCount()>0) {
            ImageIcon poster =(ImageIcon) movieDataModelPoster.getValueAt(0,0);
            if (null != poster) {
                this.setRowHeight(Math.max(20,poster.getIconHeight()));
            }
        }
        this.setRowHeight(Math.max(268,getRowHeight()));

        movieDataModelPoster.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (titleLabel != null) {
                    if (movieDataModelPoster.getRowCount()>200) {
                        titleLabel.setText("Films");
                    } else {
                        titleLabel.setText(""+movieDataModelPoster.getRowCount()+" films");
                    }
                }
            }
        });
    }

    // popup menu actions

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu != null) {
            if (menu == menuItemAddAFileToMovie) {
                addAFileToMovie();
            } else if (menu == menuItemMovieDetails) {
                int selectedRow = this.convertRowIndexToModel(this.getSelectedRow());
                displayMovieDetails(selectedRow);
            } else if (menu == menuItemPlayMovie) {
                playSelectedMovie();
            } else if (menu == menuItemInternetCritics) {
                internetCritics();
            } else if (menu == menuItemOpenInfoUrl) {
                openInfoUrl();
            }
        }
    }

    private void addAFileToMovie() {
        Movie movie = getSelectedMovie();
        if (null==movie) {
            // it is a bug
            LOG.severe("No movie in selected row");
            return;
        }

        launchServices.addFileInBackground(movie,false);
    }

    private Movie getSelectedMovie() {
        return rowToMovie(this.getSelectedRow());
    }

    private Movie rowToMovie(int row) {
        int selectedRow = this.convertRowIndexToModel(row);
        // get the movie, with the "-1" trick
        Movie movie = (Movie) this.getModel().getValueAt(selectedRow, -1);
        return movie;
    }

    private void displayMovieDetails(int row) {
        // get the movie, with the "-1" trick
        Movie movie = rowToMovie(row);
        int i = tabbedPane.indexOfTab(movie.getTitle());
        if (-1==i) {
            MovieDescriptionPanel mdp = (MovieDescriptionPanel)applicationContext.getBean("movieDescriptionPanel");
            mdp.setData(movie);
            tabbedPane.addTab(movie.getTitle(), mdp.getPanel());
            i = tabbedPane.indexOfTab(movie.getTitle());
            tabbedPane.setTabComponentAt(i, new ButtonTabComponent(tabbedPane));
            tabbedPane.setSelectedIndex(i);
        } else {
            tabbedPane.setSelectedIndex(i);
        }
    }

    private void playSelectedMovie() {
        movieGuiService.playTheMovie(getSelectedMovie());
    }

    private void internetCritics(){
        movieGuiService.internetCritics(getSelectedMovie());
    }

    private void openInfoUrl() {
        movieGuiService.openInfoUrl(getSelectedMovie().getInfoUrl());
    }

    public JTabbedPane getTabbedPane() {
        return this.tabbedPane;
    }

    public JLabel getTitleLabel() {
        return this.titleLabel;
    }
}
