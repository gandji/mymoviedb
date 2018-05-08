package org.gandji.mymoviedb;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Created by gandji on 20/05/2017.
 */
@Import(MyMovieDBConfiguration.class)
@SpringBootApplication
//@EntityScan(basePackages = {"org.gandji"})
public class MyMovieDBTestsConfiguration {
}
