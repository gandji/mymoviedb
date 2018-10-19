package org.gandji.mymoviedb;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by gandji on 20/05/2017.
 */
@Import(MyMovieDBConfiguration.class)
@SpringBootApplication
@PropertySource(value = "classpath:test.properties")
//@EntityScan(basePackages = {"org.gandji"})
public class MyMovieDBTestsConfiguration {
}
