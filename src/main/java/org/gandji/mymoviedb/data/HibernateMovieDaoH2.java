/*
 * This file is part of MyMovieDB.
 *
 * MyMovieDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyMovieDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>
 */

package org.gandji.mymoviedb.data;

import org.gandji.mymoviedb.data.repositories.h2.MovieRepositoryH2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * Created by gandji on 14/02/2018.
 */
@Component
@Profile("h2")
public class HibernateMovieDaoH2 extends HibernateMovieDao {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateMovieDaoH2.class);

    @Autowired
    private MovieRepositoryH2 movieRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MovieRowMapper movieRowMapper;

    @PostConstruct
    void init() {
        super.setMovieRepository(movieRepository);
    }

    @Override
    public List<Movie> findByDirectorKeywords(String kwds) {
        return findByKeywords(kwds);
    }

    private List<Movie> findByKeywords(String kwds) {
        MoviesTitleSearch group = new MoviesTitleSearch();
        //MoviesGroupSearchData group = new MoviesGroupSearchData();

        if (null!=kwds && !"".equals(kwds)) {

            String[] words = kwds.split("[+ ]");
            for (String word : words) {
                String queryString = "SELECT * from ft_search(";
                LOG.info("Searching for keyword <" + word + ">");
                queryString = queryString.concat("'" + word + "'");
                queryString = queryString.concat(",0,0)");

                jdbcTemplate.query(queryString, (Object[]) null, group);
            }
        }
        LOG.info("Movie group has "+group.movies.size()+" movies");

        return group.movies;
    }

    @Override
    public List<Movie> findByTitleKeywords(String kwds) {
        return findByKeywords(kwds);
    }

    @Override
    public List<Movie> findByAlternateTitleKeywords(String kwds) {
        return findByKeywords(kwds);
    }

    @Override
    @Transactional
    public List<Movie> findByGenreName(String genre) {
        LOG.info("H2 DAO cannot do genre search");
        return Collections.emptyList();
    }

    @Override
    public List<Movie> findByCommentsKeywords(String kwds) {
        return findByKeywords(kwds);
    }

    @Override
    public List<Movie> findByActorsKeywords(String kwds) {
        MoviesActorSearch group = new MoviesActorSearch();

        if (null!=kwds && !"".equals(kwds)) {

            String[] words = kwds.split("[+ ]");
            for (String word : words) {
                String queryString = "SELECT * from ft_search_data(";
                LOG.info("Searching for keyword <" + word + ">");
                queryString = queryString.concat("'" + word + "'");
                queryString = queryString.concat(",10,0)");

                jdbcTemplate.query(queryString, (Object[]) null, group);
            }
        }
        LOG.info("Movie group has "+group.movies.size()+" movies");

        return group.movies;
    }

    @Override
    public List<Movie> findByInfoUrl(URL infoUrl) {
        return movieRepository.findByInfoUrl(infoUrl);
    }

    private class MoviesTitleSearch implements RowCallbackHandler {

        public List<Movie> movies = new ArrayList<>();

        @Override
        public void processRow(ResultSet resultSet) throws SQLException {
            LOG.trace("GOT Result "+resultSet.toString());
            ResultSetMetaData metadata = resultSet.getMetaData();
            int nCols = metadata.getColumnCount();
            for (int i=1; i<=nCols; i++ ) {
                LOG.trace(" COL "+i);
                LOG.trace("     colname: "+metadata.getColumnName(i)+ "  colclassname:"+metadata.getColumnClassName(i)+"   typename: "+metadata.getColumnTypeName(i));
                LOG.trace("   TABLE NAME "+metadata.getTableName(i));
                LOG.trace("   value: "+resultSet.getString(i));
            }
            String queryString = "select * from "+resultSet.getString(1);
            List<Movie> thisRowMovies = jdbcTemplate.query(queryString,(Object[])null,movieRowMapper);
            LOG.debug("   SUB QUERY: "+queryString+ " > returns "+movies.size()+ " movies");
            this.movies.addAll(thisRowMovies);
        }
    }

    private class MoviesActorSearch implements RowCallbackHandler {

        public List<Movie> movies = new ArrayList<>();

        @Override
        public void processRow(ResultSet resultSet) throws SQLException {
            LOG.trace("GOT Result "+resultSet.toString());
            ResultSetMetaData metadata = resultSet.getMetaData();
            int nCols = metadata.getColumnCount();
            for (int i=1; i<=nCols; i++ ) {
                LOG.trace(" COL "+i);
                LOG.trace("     colname: "+metadata.getColumnName(i)+ "  colclassname:"+metadata.getColumnClassName(i)+"   typename: "+metadata.getColumnTypeName(i));
                LOG.trace("   TABLE NAME "+metadata.getTableName(i));
                LOG.trace("   value: "+resultSet.getString(i));
                LOG.trace("   label: "+metadata.getColumnLabel(i));
            }
            String schema = resultSet.getString("SCHEMA");
            String  table = resultSet.getString("TABLE");
            Array columns = resultSet.getArray("COLUMNS");
            Array    keys = resultSet.getArray("KEYS");

            LOG.trace(" columns array has BaseTypeName "+columns.getBaseTypeName()+"     "+columns.toString());
            LOG.trace("    keys array has BaseTypeName "+keys.getBaseTypeName()+"     "+keys.toString());

            ResultSetMetaData colData = resultSet.getArray("COLUMNS").getResultSet(1,1).getMetaData();
            nCols = colData.getColumnCount();
            for (int i=1; i<=nCols; i++ ) {
                LOG.trace("> COL " + i);
                LOG.trace(">     colname: " + colData.getColumnName(i) + "  colclassname:" + colData.getColumnClassName(i) + "   typename: " + colData.getColumnTypeName(i));
                LOG.trace(">   TABLE NAME " + colData.getTableName(i));
                LOG.trace(">   value: " + resultSet.getString(i));
                LOG.trace(">   label: " + colData.getColumnLabel(i));
            }

            LOG.info(" select * from "+resultSet.getString("SCHEMA")+"."+resultSet.getString("TABLE")
                    +" where "+resultSet.getString("COLUMNS")+"="+resultSet.getString("KEYS")+";");
            String column = resultSet.getArray("COLUMNS").getArray(0,1).toString();
            String value = resultSet.getArray("KEYS").getArray(0,1).toString();
            String queryString = "select * from movie " +
                    "left outer join movies_actors as ma on movie.id=ma.movies_id " +
                    "left outer join actor as a on a.id=ma.actors_id where a."+column+"="+value;
            LOG.debug("   SUB QUERY: "+queryString+ " >");
            List<Movie> thisRowMovies = jdbcTemplate.query(queryString,(Object[])null,movieRowMapper);
            LOG.info("    returns "+movies.size()+ " movies");
            this.movies.addAll(thisRowMovies);
        }
    }

    private class MoviesGroupSearchData implements RowCallbackHandler {
        public List<Movie> movies = new ArrayList<>();
        @Override
        public void processRow(ResultSet resultSet) throws SQLException {
            LOG.trace("GOT Result "+resultSet.toString());
            ResultSetMetaData metadata = resultSet.getMetaData();
            int nCols = metadata.getColumnCount();
            for (int i=1; i<=nCols; i++ ) {
                LOG.trace(" COL "+i);
                LOG.trace("     colname: "+metadata.getColumnName(i)+ "  colclassname:"+metadata.getColumnClassName(i)+"   typename: "+metadata.getColumnTypeName(i));
                LOG.trace("   TABLE NAME "+metadata.getTableName(i));
                LOG.trace("   value: "+resultSet.getString(i));
                LOG.trace("   label: "+metadata.getColumnLabel(i));
                // cols are in order
                // colname  colclassname      typename   tablename         value
                // SCHEMA   String            VARCHAR     FT_SEARCH_DATA   PUBLIC
                // TABLE    String            VARCHAR     FT_SEARCH_DATA   ACTOR or MOVIE or GENRE
                // COLUMNS  java.sql.Array    ARRAY       FT_SEARCH_DATA   (NAME or ID)
                // KEYS     java.sql.Array    ARRAY       FT_SEARCH_DATA   (keyword)
                // SCORE    java.lang.Double  DOUBLE      FT_SEARCH_DATA   1.0
            }
            String schema = resultSet.getString("SCHEMA");
            String  table = resultSet.getString("TABLE");
            Array columns = resultSet.getArray("COLUMNS");
            Array    keys = resultSet.getArray("KEYS");

            LOG.trace(" columns array has BaseTypeName "+columns.getBaseTypeName()+"     "+columns.toString());
            LOG.trace("    keys array has BaseTypeName "+keys.getBaseTypeName()+"     "+keys.toString());

            ResultSetMetaData colData = resultSet.getArray("COLUMNS").getResultSet(1,1).getMetaData();
            nCols = colData.getColumnCount();
            for (int i=1; i<=nCols; i++ ) {
                LOG.trace("> COL " + i);
                LOG.trace(">     colname: " + colData.getColumnName(i) + "  colclassname:" + colData.getColumnClassName(i) + "   typename: " + colData.getColumnTypeName(i));
                LOG.trace(">   TABLE NAME " + colData.getTableName(i));
                LOG.trace(">   value: " + resultSet.getString(i));
                LOG.trace(">   label: " + colData.getColumnLabel(i));
            }

            LOG.info(" select * from "+resultSet.getString("SCHEMA")+"."+resultSet.getString("TABLE")
                    +" where "+resultSet.getString("COLUMNS")+"="+resultSet.getString("KEYS")+";");
        }
    }

    @Override
    public Iterable<Movie> searchInternal(String titleKeywords, String directorKeywords, String actorsKeywords, String genreKeyword, String commentsKeywords, String qualiteVideoKeyword) {
        MoviesTitleSearch group = new MoviesTitleSearch();
        //MoviesGroupSearchData group = new MoviesGroupSearchData();

        Set<Movie> matchingMovies = new HashSet<>();

        boolean matchingMovieInitialized= false;

        Set<Movie> titleMatchingMovies = new HashSet<>();
        if (null!=titleKeywords && !"".equals(titleKeywords)) {
           titleMatchingMovies.addAll(findByTitleKeywords(titleKeywords));
           matchingMovies.addAll(titleMatchingMovies);
           matchingMovieInitialized = true;
        }

        Set<Movie> directorMatchingMovies = new HashSet<>();
        boolean useDirector = false;
        if (null!=directorKeywords && !"".equals(directorKeywords)) {
            directorMatchingMovies.addAll(findByDirectorKeywords(directorKeywords));
            if (!matchingMovieInitialized) {
                matchingMovies.addAll(directorMatchingMovies);
                matchingMovieInitialized = true;
            }
            useDirector = true;
        }

        Set<Movie> commentsMatchingMovies = new HashSet<>();
        boolean useComments = false;
        if (null!=commentsKeywords && !"".equals(commentsKeywords)) {
            commentsMatchingMovies.addAll(findByCommentsKeywords(commentsKeywords));
            if (!matchingMovieInitialized) {
                matchingMovies.addAll(commentsMatchingMovies);
                matchingMovieInitialized = true;
            }
            useComments = true;
        }

        Set<Movie> actorsMatchingMovies = new HashSet<>();
        boolean useActors = false;
        if (null!=actorsKeywords && !"".equals(actorsKeywords)) {
            actorsMatchingMovies.addAll(findByActorsKeywords(actorsKeywords));
            if (!matchingMovieInitialized) {
                matchingMovies.addAll(actorsMatchingMovies);
                matchingMovieInitialized = true;
            }
            useActors = true;
        }

        Set<Movie> genresMatchingMovies = new HashSet<>();
        boolean useGenres = false;
        if (null!=genreKeyword && !"Any".equals(genreKeyword)) {
            genresMatchingMovies.addAll(findByGenreName(genreKeyword));
            if (!matchingMovieInitialized) {
                matchingMovies.addAll(genresMatchingMovies);
                matchingMovieInitialized = true;
            }
            useGenres = true;
        }

        if (!matchingMovieInitialized) { return findAll();}

        if (useDirector) {
            matchingMovies.retainAll(directorMatchingMovies);
        }

        if (useComments) {
            matchingMovies.retainAll(commentsMatchingMovies);
        }

        if (useActors) {
            matchingMovies.retainAll(actorsMatchingMovies);
        }

        if (useGenres) {
            matchingMovies.retainAll(genresMatchingMovies);
        }

        return matchingMovies;
    }

}
