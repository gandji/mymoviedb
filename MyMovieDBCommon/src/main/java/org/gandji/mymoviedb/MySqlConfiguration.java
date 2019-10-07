package org.gandji.mymoviedb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by gandji on 14/01/2018.
 */
@Profile("mysql")
@PropertySource("classpath:application-mysql.properties")
@Configuration
@Slf4j
public class MySqlConfiguration extends MyMovieDBCommonConfiguration {
}
