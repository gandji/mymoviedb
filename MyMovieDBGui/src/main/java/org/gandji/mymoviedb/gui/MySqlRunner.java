package org.gandji.mymoviedb.gui;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.data.Actor;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Component;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by gandji on 14/01/2018.
 */
@Component
@Profile("mysql")
@Slf4j
public class MySqlRunner extends MyMovieDBRunner {

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    HibernateMovieDao hibernateMovieDao;

    // biiiig hack from SO to check if index exists!
    // get list of indexes from "information_schema" to see if fulltext index is there
    void createFulltextIndexIfNotPresent(String dbName, String tableName, String fieldName) {
        String indexName = "fulltext_" + fieldName;
        try {
            int fulltextIndexOK = jdbcTemplate.execute(new PreparedStatementCreator() {
                @Override
                public java.sql.PreparedStatement createPreparedStatement(Connection cnctn) throws SQLException {
                    return cnctn.prepareStatement("select if ("
                            + "    exists("
                            + "        select index_name from information_schema.statistics "
                            + "        where table_schema = ? "
                            + "        and table_name = ? and index_name like ?"
                            + "    )"
                            + "    ,1"
                            + "    ,0) ;");
                }
            }, new PreparedStatementCallback<Integer>() {

                @Override
                public Integer doInPreparedStatement(java.sql.PreparedStatement ps) throws SQLException, DataAccessException {
                    ps.setNString(1, dbName);
                    ps.setNString(2, tableName);
                    ps.setNString(3, indexName);
                    Integer fulltextIndexPresent = 0;
                    if (ps.execute()) {
                        ResultSet rs = ps.getResultSet();
                        while (rs.next()) {
                            fulltextIndexPresent = rs.getInt(1);
                        }
                        rs.close();
                    }

                    return fulltextIndexPresent;
                }
            });

            if (0 == fulltextIndexOK) {
                log.info("Rebuilding fulltext index for "+dbName+"."+tableName+"."+fieldName);
                jdbcTemplate.execute(new PreparedStatementCreator() {
                    @Override
                    public java.sql.PreparedStatement createPreparedStatement(Connection cnctn) throws SQLException {
                        cnctn.setCatalog(dbName);
                        return cnctn.prepareStatement("create fulltext index "+indexName+" on "+tableName+"("+fieldName+");");
                    }
                }, new PreparedStatementCallback<Integer>() {
                    @Override
                    public Integer doInPreparedStatement(java.sql.PreparedStatement ps) throws SQLException, DataAccessException {
                        ps.execute();
                        return 0;
                    }
                });
            } else {
                log.debug("FULLTEXT INDEX OK FOR "+dbName+"."+tableName+"."+fieldName);
            }
        } catch (Exception e) {

        }

    }

    @Override
    protected void vendorSpecificDatabaseInitialization() {

        log.info("Initializing MySql database for MyMovieDB");

        // grrrr have to get schema from the datasource as a string
        // i dont know better than this:
        String schema = dataSourceUrl.substring(dataSourceUrl.lastIndexOf("/")+1);

        // remove ?xxx=yyy stuff
        int mark = schema.indexOf("?");
        if (mark>=0) {
            schema = schema.substring(0, mark);
        }

        createFulltextIndexIfNotPresent(schema,
                Movie.class.getAnnotation(Table.class).name(),
                "title");
        createFulltextIndexIfNotPresent(schema,
                Movie.class.getAnnotation(Table.class).name(),
                "alternate_title");
        createFulltextIndexIfNotPresent(schema,
                Movie.class.getAnnotation(Table.class).name(),
                "director");
        createFulltextIndexIfNotPresent(schema,
                Actor.class.getAnnotation(Table.class).name(),
                "name");
        createFulltextIndexIfNotPresent(schema,
                Movie.class.getAnnotation(Table.class).name(),
                "comments");
        createFulltextIndexIfNotPresent(schema,
                Actor.class.getAnnotation(Table.class).name(),
                "name");

        //fillDbForTests();
    }
}
