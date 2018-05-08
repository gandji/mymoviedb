package org.gandji.mymoviedb.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by gandji on 02/12/2017.
 */
@Component
public class MovieRowMapper implements RowMapper<Movie> {
    @Autowired
    private HibernateMovieDao hibernateMovieDao;
    @Override
    public Movie mapRow(ResultSet resultSet, int i) throws SQLException {
        return hibernateMovieDao.findOne(resultSet.getLong("id"));
    }
}
