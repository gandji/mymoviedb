package org.gandji.mymoviedb;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by gandji on 14/01/2018.
 */
@Profile("mysql")
@PropertySource("classpath:application-mysql.properties")
public class MySqlConfiguration extends MyMovieDBConfiguration {
}
