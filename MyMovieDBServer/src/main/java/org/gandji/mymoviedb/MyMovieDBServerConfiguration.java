package org.gandji.mymoviedb;

import org.gandji.mymoviedb.MyMovieDBCommonConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * Created by gandji on 21/09/2019.
 */
@Import({MyMovieDBCommonConfiguration.class})
public class MyMovieDBServerConfiguration {
}
