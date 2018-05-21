package org.gandji.mymoviedb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.Driver;

/**
 * Created by gandji on 20/05/2017.
 */
//@Import(MyMovieDBConfiguration.class)
@SpringBootApplication
//@EntityScan(basePackages = {"org.gandji"})
public class MyMovieDBTestsConfiguration  extends MyMovieDBConfiguration {
}
