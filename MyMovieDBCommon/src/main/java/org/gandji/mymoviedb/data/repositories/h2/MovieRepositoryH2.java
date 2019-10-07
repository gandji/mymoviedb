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

package org.gandji.mymoviedb.data.repositories.h2;

import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.data.repositories.MovieRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by gandji on 16/02/2018.
 */
@Profile("h2")
@Repository
public interface MovieRepositoryH2 extends MovieRepository {
    @Query(value="select * from movie left join movie_actors as ma on ma.movies_id=movie.id " +
            "                     join actor on actor.id=actors_id" +
            " where actor.name like :#{#name} ;", nativeQuery = true)
    List<Movie> findByActorName(@Param("name") String name);
}
