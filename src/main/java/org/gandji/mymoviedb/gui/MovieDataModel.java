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
package org.gandji.mymoviedb.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import org.gandji.mymoviedb.MyMovieDBPreferences;
import org.gandji.mymoviedb.data.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
public class MovieDataModel extends AbstractTableModel {

    private static final Logger LOG = Logger.getLogger(MovieDataModel.class.getName());

    private List<Movie> movies = null;
    private List<ColumnDescription> displayedColumns = null;

    @Autowired
    private MyMovieDBPreferences myMovieDBPreferences;

    public MovieDataModel() {
        this.movies = null;
        this.displayedColumns = new ArrayList<>();
    }

    public void setMovies(Iterable<Movie> movies) {
        List<Movie> movieList = new ArrayList<>();
        movies.forEach(movie -> movieList.add(movie));
        this.movies = movieList;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public void addMovie(Movie movie) {
        if (null==this.movies) {
            this.movies = new ArrayList<Movie>();
        }
        this.movies.add(movie);
    }

    public enum Role {
        POSTER, TITLE, YEAR, DIRECTOR,RATING,IMDBURL,DUREE, LAST_SEEN,SUMMARY
    };

    static class ColumnDescription {

        Role role;
        String name;
        Integer preferredWidth;

        private ColumnDescription(Role role, String name, Integer preferredWidth) {
            this.role = role;
            this.name = name;
            this.preferredWidth = preferredWidth;
        }

        public Object getValueFor(Movie movie, MyMovieDBPreferences myMovieDBPreferences){
            switch (this.role){
                case POSTER:
                    try {
                        byte[] bytes = movie.getPosterBytes();
                        if (null != bytes) {
                            BufferedImage poster = ImageIO.read(new ByteArrayInputStream(bytes));
                            myMovieDBPreferences.setPosterHeight(poster.getHeight());
                            myMovieDBPreferences.setPosterWidth(poster.getWidth());
                            return new ImageIcon(ImageIO.read(new ByteArrayInputStream(bytes)));
                        } else {
                            BufferedImage poster = new BufferedImage(myMovieDBPreferences.getPosterWidth(),
                                    myMovieDBPreferences.getPosterHeight(),BufferedImage.TYPE_3BYTE_BGR);
                            Graphics graphics = poster.getGraphics();
                            graphics.drawString(movie.getTitle(),10,40);
                            if (movie.getAlternateTitle()!=null) {
                                graphics.drawString(" ("+movie.getAlternateTitle()+")",
                                        10,60);
                            }
                            graphics.drawString(" by " + movie.getDirector(),
                                        10,80);
                            return new ImageIcon(poster);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                case TITLE: return movie.getTitle();
                case YEAR: return movie.getYear();
                case DIRECTOR: return movie.getDirector();
                case RATING: return movie.getRating();
                case IMDBURL: return movie.getInfoUrl();
                case DUREE: return movie.getDuree();
                case LAST_SEEN: return movie.getLastSeen();
                case SUMMARY: return movie.getSummary();
                default: return null;
            }
        }
    }

    // TODO user customization of displayed columns
    static private Map<Role,ColumnDescription> allColumns = null;

    static {

        allColumns = new HashMap<>();

        allColumns.put(Role.POSTER, new ColumnDescription(Role.POSTER, "Films",190));
        allColumns.put(Role.TITLE, new ColumnDescription(Role.TITLE, "Title",120));
        allColumns.put(Role.YEAR, new ColumnDescription(Role.YEAR, "Year",35));
        allColumns.put(Role.DIRECTOR, new ColumnDescription(Role.DIRECTOR, "Director",80));
        allColumns.put(Role.RATING, new ColumnDescription(Role.RATING, "Rating", 20));
        allColumns.put(Role.IMDBURL, new ColumnDescription(Role.IMDBURL, "Imdb URL", 40));
        allColumns.put(Role.DUREE, new ColumnDescription(Role.DUREE, "Duration",40));
        allColumns.put(Role.LAST_SEEN, new ColumnDescription(Role.LAST_SEEN, "Last seen",40));
        allColumns.put(Role.SUMMARY, new ColumnDescription(Role.SUMMARY, "Summary",250));

    };

    public void addDisplayedColumn(Role role) {
        displayedColumns.add(allColumns.get(role));
    }

    public void resetDisplayedColumns() {
        if (null != displayedColumns) {
            displayedColumns.clear();
        }
    }

    public Integer getPreferredWidth(Integer colIndex) {
        return displayedColumns.get(colIndex).preferredWidth;
    }

    @Override
    public int getRowCount() {
        if (null!=movies){
            return movies.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getColumnCount() {
        return displayedColumns.size();
    }

    // override to return ImageIcon class on the poster column
    @Override
    public Class<?> getColumnClass(int column) {
        if (getRowCount() > 0) {
            Object valueAt = getValueAt(0, column);
            if (null != valueAt) {
                return valueAt.getClass();
            } else {
                return String.class;
            }
        }

        return super.getColumnClass(column);
    }

    // trick: i use -1 to access the movie object!
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex<0) { return null;}// means no selection
        if (null != movies) {
            if (columnIndex == -1) {
                return movies.get(rowIndex);
            } else {
                return displayedColumns.get(columnIndex).getValueFor(movies.get(rowIndex),myMovieDBPreferences);
            }
        } else {
            return null;
        }
    }
    
    @Override
    public String getColumnName(int column) {
        return displayedColumns.get(column).name;
    }

}
    
