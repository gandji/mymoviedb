/*
 * Copyright (C) 2017 gandji <gandji@free.fr>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gandji.mymoviedb;

import java.sql.Driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 *
 * @author gandji <gandji@free.fr>
 * @todo modify the movie when "enter url" is pressed, do not create new one.
 * @todo check necessity of hibernate.cfg.xml
 * @todo when clicking prefs "keep file on update", it is not saved unless save button pushed is this really what we want?
 * @todo movie file device for usb: when loading/searching for a file to play or to display information:
 *             get the diskLabel
 *             from all the root fs's, get the drive letter (from getSystemDisplayName())
 *             check it is plugged in, present, active, etc...
 *             replace the drive letter in VideoFile path by the letter found
 * @todo enter actor and genres in movieDescriptionPanel
 * @todo update movie description window when ever a movie changes!!!
 * @todo in preferences: give browser to use? command line?
 * @todo enter movie by hand:genres, actors, poster
 * @todo see iso problem in simplemagic
 * @todo user configurable regexps: implement friendly add/remove regexp
 * @todo search criteria: file version, file name
 */
@Import({H2Configuration.class,MySqlConfiguration.class})
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "nullAuditorBean")
public class MyMovieDBConfiguration {

    protected static final Logger LOG = LoggerFactory.getLogger(MyMovieDBConfiguration.class);

    @Autowired
    private Environment env;

    @Value("${application.version}")
    String myMovieDBVersionString;

    @Value("${spring.datasource.url}")
    String dataSourceUrl;

    @Value("${spring.datasource.username}")
    String dataSourceUsername;

    @Value("${spring.datasource.password}")
    String dataSourcePassword;

    @Value("${spring.datasource.driver-class-name}")
    String dataSourceDriver;

    public MyMovieDBConfiguration() {
    }

    /*
     * utilisation de jdbc pour la source de donnees
     */
    @Bean
    DataSource dataSource() {

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
            LOG.info("Could not find driver class: " + dataSourceDriver);
        }

        return dataSource;
    }

    @Bean
    String myMovieDBVersion() {
        return myMovieDBVersionString;
    }
}
