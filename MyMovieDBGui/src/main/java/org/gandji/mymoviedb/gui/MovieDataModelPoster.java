package org.gandji.mymoviedb.gui;

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
