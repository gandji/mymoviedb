package org.gandji.mymoviedb.gui.widgets;

import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.data.repositories.MovieCountPerAttribute;
import org.gandji.mymoviedb.gui.MovieDataModelPoster;
import org.jfree.chart.*;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;

/**
 * Created by gandji on 08/05/2019.
 */
public abstract class HistogramPanel {

    Logger LOG = LoggerFactory.getLogger(HistogramPanel.class);

    DefaultCategoryDataset dataset;
    JFreeChart chart;
    ChartPanel chartPanel;
    JScrollPane scrollPane;

    @Autowired
    HibernateMovieDao hibernateMovieDao;

    @Autowired
    EntityManager entityManager;

    @Autowired
    MovieDataModelPoster movieDataModelPoster;

    public Component getComponent() {
        return scrollPane;
    }

    public HistogramPanel(String title, String categoryName) {
        dataset = new DefaultCategoryDataset();
        chart = ChartFactory.createBarChart(title,categoryName,"films", dataset,
                PlotOrientation.HORIZONTAL,
                true,true,false);

        chart.getPlot().setBackgroundPaint(UIManager.getColor("Panel.background"));
        //chart.getCategoryPlot().setFixedDomainAxisSpace(AxisSpace);
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0,Color.decode("0x5580d2"));
        chart.setBackgroundPaint(UIManager.getColor("Panel.background"));
        chart.removeLegend();

        chartPanel = new ChartPanel(chart);
        Integer ppi = Toolkit.getDefaultToolkit().getScreenResolution();
        chartPanel.setPreferredSize(new Dimension(600*ppi/96,800*ppi/96));

        scrollPane = new JScrollPane(chartPanel);
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
