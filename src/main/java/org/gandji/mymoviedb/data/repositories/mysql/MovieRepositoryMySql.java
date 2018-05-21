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

package org.gandji.mymoviedb.data.repositories.mysql;

import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.data.repositories.MovieRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.net.URL;
import java.util.List;

/**
 * Created by gandji on 14/02/2018.
 */
@Profile("mysql")
public interface MovieRepositoryMySql extends MovieRepository { //PagingAndSortingRepository<Movie,Long> {
    @Query(value="select * from movie where MATCH title AGAINST (:#{#kwds} in natural language mode)",
      nativeQuery = true)
    List<Movie> findByTitleKeywords(@Param("kwds") String kwds);

    @Query(value="select * from movie where MATCH alternate_title AGAINST (:#{#kwds} in natural language mode)",
            nativeQuery = true)
    List<Movie> findByAlternateTitleKeywords(@Param("kwds") String kwds);

    @Query(value="select * from movie where match Director against (:#{#kwds} in natural language mode)",
            nativeQuery = true)
    List<Movie> findByDirectorKeywords(@Param("kwds") String kwds);

    @Query(value="select * from movie where match Comments against (:#{#kwds} in natural language mode)",
            nativeQuery = true)
    List<Movie> findByCommentsKeywords(@Param("kwds") String kwds);

    @Query(value="select * from movie left outer join movie_actor as ma on ma.movies_id=movie.id          \n" +
            "                     join actor on actor.id=actors_id\n" +
            " where match actor.name against (:#{#kwds} in natural language mode);",
            nativeQuery = true)
    List<Movie> findByActorsKeywords(@Param("kwds") String kwds);

    List<Movie> findByInfoUrl(URL imdbUrl);

    @Query(value="select * from movie left outer join movie_genre as mg on mg.movies_id=movie.id \n" +
            "                  where genres_name like :#{#genr} ;",
            nativeQuery = true)
    List<Movie> findByGenreName(@Param("genr") String genr);

}
