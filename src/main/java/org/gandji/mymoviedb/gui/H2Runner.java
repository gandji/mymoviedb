package org.gandji.mymoviedb.gui;

import org.gandji.mymoviedb.data.Actor;
import org.gandji.mymoviedb.data.Genre;
import org.gandji.mymoviedb.data.Movie;
import org.h2.fulltext.FullText;
import org.h2.fulltext.FullTextLucene;
import org.h2.jdbc.JdbcSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.ExcludeDefaultListeners;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * Created by gandji on 14/01/2018.
 */
@Component
@Profile("h2")
public class H2Runner extends MyMovieDBRunner {
    
    Logger LOG = LoggerFactory.getLogger(H2Runner.class);

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
        LOG.info("Initializing H2 database for MyMovieDB <" + schema + ">");

        // ca marche avec native fulltext
        FullText ftl = new FullText();
        // ca ne marche pas avec lucene FullTextLucene ftl = new FullTextLucene();
        Connection connection = null;
        if (true) {
            try {
                connection = jdbcTemplate.getDataSource().getConnection();
                ftl.init(connection);
                LOG.info("Intialization of full text OK");

                String schemaName = "PUBLIC";

                LOG.info("table class annotation name for movie= " + Movie.class.getAnnotation(Table.class).name());

                try {
                    ftl.createIndex(connection, schemaName, Movie.class.getAnnotation(Table.class).name(), "TITLE,ALTERNATE_TITLE,COMMENTS,DIRECTOR");
                    LOG.info("Initialization of Movie full text index for Movies OK");
                } catch (Exception e) {
                    LOG.warn("Seems Movies full text index is already there");
                    LOG.debug(e.getMessage());
                }

                try {
                    ftl.createIndex(connection, schemaName, Genre.class.getAnnotation(Table.class).name(), "NAME");
                    LOG.info("Initialization of Movie full text index for Genres OK");
                } catch (Exception e) {
                    LOG.warn("Seems genre full text index is already there");
                    LOG.debug(e.getMessage());
                }
                try {
                    ftl.createIndex(connection, schemaName, Actor.class.getAnnotation(Table.class).name(), "NAME");
                    LOG.info("Initialization of Movie full text index for Actors OK");
                } catch (Exception e) {
                    LOG.warn("Seems Actors full text index is already there");
                    LOG.debug(e.getMessage());
                }
            } catch (SQLException e) {
                LOG.warn("Could not initialize fulltext: " + e.getMessage());
            }
        }


        fillDbForTests();

        /*FullTextEntityManager ftem = Search.getFullTextEntityManager(entityManager);
        try {
            LOG.info("Creating (Lucene) indexes...");
            ftem.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            LOG.warn("Interrupted while creating indexes.");
            e.printStackTrace();
        }*/

        try {
            if (null != connection) {
                ftl.reindex(connection);
            }
        } catch (SQLException e) {
            LOG.warn("Could not reindex database");
            e.printStackTrace();
        }
    }


}
