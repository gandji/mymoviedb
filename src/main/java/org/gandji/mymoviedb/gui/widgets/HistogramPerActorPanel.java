package org.gandji.mymoviedb.gui.widgets;

import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.data.repositories.MovieCountPerAttribute;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Created by gandji on 08/05/2019.
 */
@Component
public class HistogramPerActorPanel extends HistogramPanel {

    public  HistogramPerActorPanel() {
        super("Films by actor", "actors");

    }

    @Override
    public Iterable<MovieCountPerAttribute> countMoviesPerAttribute() {
        return entityManager.createNamedQuery("countMoviesPerActor").getResultList();
    }

    @Override
    public Iterable<Movie> listMoviesForItem(ChartEntity entity) {
        if (entity instanceof CategoryItemEntity) {
            CategoryItemEntity cie = (CategoryItemEntity) entity;
            String actorName = cie.getColumnKey().toString();
            Iterable<Movie> uniqueMovies = hibernateMovieDao.findByActorName(actorName);
            return uniqueMovies;
        } else {
            return Collections.emptyList();
        }

    }

}
