package org.gandji.mymoviedb.gui.widgets;

import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.data.repositories.MovieCountPerAttribute;
import org.gandji.mymoviedb.gui.MovieDataModelPoster;
import org.jfree.chart.*;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.awt.*;
import java.util.Iterator;

/**
 * Created by gandji on 08/05/2019.
 */
public abstract class HistogramPanel {

    DefaultCategoryDataset dataset;
    JFreeChart chart;
    ChartPanel chartPanel;

    @Autowired
    HibernateMovieDao hibernateMovieDao;

    @Autowired
    EntityManager entityManager;

    @Autowired
    MovieDataModelPoster movieDataModelPoster;

    public Component getComponent() {
        return chartPanel;
    }

    public HistogramPanel(String title, String categoryName) {
        dataset = new DefaultCategoryDataset();
        chart = ChartFactory.createBarChart(title,categoryName,"films", dataset,
                PlotOrientation.HORIZONTAL,
                true,true,false);

        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600,10000));

    }

    @PostConstruct
    public void setCallbacks() {
        chartPanel.addChartMouseListener(new ChartMouseListener() {

            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                ChartEntity entity = event.getEntity();
                    Iterable<Movie> uniqueMovies = listMoviesForItem(entity);
                    movieDataModelPoster.setMovies(uniqueMovies);
                    movieDataModelPoster.fireTableDataChanged();
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {
            }
        });

    }

    public void refresh() {
        if (dataset.getRowCount()>0) {
            dataset.removeRow("Films");
        }
        Iterable<MovieCountPerAttribute> moviesPerAttributes = countMoviesPerAttribute();
        for (MovieCountPerAttribute moviesPerAttribute : moviesPerAttributes) {
            dataset.addValue(moviesPerAttribute.getnMovies(),"Films", moviesPerAttribute.getName());
        }
    }

    public abstract Iterable<Movie> listMoviesForItem(ChartEntity entity);
    public abstract Iterable<MovieCountPerAttribute> countMoviesPerAttribute();
}
