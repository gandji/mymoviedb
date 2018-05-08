package org.gandji.mymoviedb.gui;

import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by gandji on 22/11/2017.
 */
@Component
public class MovieDataModelPoster extends MovieDataModel {

    @PostConstruct
    void postConstruct() {
        addDisplayedColumn(MovieDataModel.Role.POSTER);
    }
}
