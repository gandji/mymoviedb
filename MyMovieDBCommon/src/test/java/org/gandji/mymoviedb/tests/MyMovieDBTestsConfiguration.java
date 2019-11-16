package org.gandji.mymoviedb.tests;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by gandji on 20/05/2017.
 */
// REMOVE unused @Import(MyMovieDBConfiguration.class)
@SpringBootApplication
@PropertySource(value = {"classpath:mymoviedb_common.properties", "classpath:test.properties"})
//@EntityScan(basePackages = {"org.gandji.mymoviedb.data"})
@ComponentScan(basePackages = {"org.gandji.mymoviedb"})
public class MyMovieDBTestsConfiguration {
}
