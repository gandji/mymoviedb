package org.gandji.mymoviedb;

import org.gandji.mymoviedb.MyMovieDBConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by gandji on 20/05/2017.
 */
@SpringBootApplication
@PropertySource(value = {"classpath:test.properties","classpath:mymoviedb_common.properties"})
public class MyMovieDBTestsConfiguration {
}
