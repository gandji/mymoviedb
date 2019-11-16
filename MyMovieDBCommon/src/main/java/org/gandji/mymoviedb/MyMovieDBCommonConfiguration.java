package org.gandji.mymoviedb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Driver;

/**
 * Created by gandji on 21/09/2019.
 */
//@ComponentScan("org.gandji.mymoviedb")
@EntityScan(basePackages = {"org.gandji.mymoviedb.data"})
@EnableJpaRepositories
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "nullAuditorBean")
@Slf4j
public class MyMovieDBCommonConfiguration {

    @Value("${application.version}")
    private String myMovieDBVersionString;

    @Value("${spring.datasource.url}")
    String dataSourceUrl;

    @Value("${spring.datasource.username}")
    String dataSourceUsername;

    @Value("${spring.datasource.password}")
    String dataSourcePassword;

    @Value("${spring.datasource.driver-class-name}")
    String dataSourceDriver;

    /*
     * utilisation de jdbc pour la source de donnees
     */
    @Bean
    public DataSource dataSource() {

        // jdbc:
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setUsername(dataSourceUsername);
        dataSource.setPassword(dataSourcePassword);
        dataSource.setUrl(dataSourceUrl);

        try {
            Class<? extends Driver> driverClass = (Class<? extends Driver>) Class.forName(dataSourceDriver);
            // for jdbc:
            dataSource.setDriverClass(driverClass);
        } catch (ClassNotFoundException e) {
            log.info("Could not find driver class: " + dataSourceDriver);
        }

        return dataSource;
    }

    @Bean
    public String myMovieDBVersion() {
        return myMovieDBVersionString;
    }
}
