package org.gandji.mymoviedb.gui;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.data.Actor;
import org.gandji.mymoviedb.data.Genre;
import org.gandji.mymoviedb.data.Movie;
import org.h2.fulltext.FullText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * Created by gandji on 14/01/2018.
 */
@Component
@Profile("h2")
@Slf4j
public class H2Runner extends MyMovieDBRunner {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Override
    protected void vendorSpecificDatabaseInitialization() {

        // grrrr have to get schema from the datasource as a string
        // i dont know better than this:

        String schema = dataSourceUrl.substring(dataSourceUrl.lastIndexOf("/") + 1);
        log.info("Initializing H2 database for MyMovieDB <" + schema + ">");

        // ca marche avec native fulltext
        FullText ftl = new FullText();
        // ca ne marche pas avec lucene FullTextLucene ftl = new FullTextLucene();
        Connection connection = null;
        if (true) {
            try {
                connection = jdbcTemplate.getDataSource().getConnection();
                ftl.init(connection);
                log.info("Intialization of full text OK");

                String schemaName = "PUBLIC";

                log.info("table class annotation name for movie= " + Movie.class.getAnnotation(Table.class).name());

                try {
                    ftl.createIndex(connection, schemaName, Movie.class.getAnnotation(Table.class).name(), "TITLE,ALTERNATE_TITLE,COMMENTS,DIRECTOR");
                    log.info("Initialization of Movie full text index for Movies OK");
                } catch (Exception e) {
                    log.warn("Seems Movies full text index is already there");
                    log.debug(e.getMessage());
                }

                try {
                    ftl.createIndex(connection, schemaName, Genre.class.getAnnotation(Table.class).name(), "NAME");
                    log.info("Initialization of Movie full text index for Genres OK");
                } catch (Exception e) {
                    log.warn("Seems genre full text index is already there");
                    log.debug(e.getMessage());
                }
                try {
                    ftl.createIndex(connection, schemaName, Actor.class.getAnnotation(Table.class).name(), "NAME");
                    log.info("Initialization of Movie full text index for Actors OK");
                } catch (Exception e) {
                    log.warn("Seems Actors full text index is already there");
                    log.debug(e.getMessage());
                }
            } catch (SQLException e) {
                log.warn("Could not initialize fulltext: " + e.getMessage());
            }
        }


        //fillDbForTests();

        /*FullTextEntityManager ftem = Search.getFullTextEntityManager(entityManager);
        try {
            log.info("Creating (Lucene) indexes...");
            ftem.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            log.warn("Interrupted while creating indexes.");
            e.printStackTrace();
        }*/

        try {
            if (null != connection) {
                ftl.reindex(connection);
            }
        } catch (SQLException e) {
            log.warn("Could not reindex database");
            e.printStackTrace();
        }
    }


}
