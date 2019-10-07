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

package org.gandji.mymoviedb.data.repositories;

import org.gandji.mymoviedb.data.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * Created by gandji on 18/02/2018.
 */
@NoRepositoryBean
public interface MovieRepository extends JpaRepository<Movie,Long> {
    Optional<Movie> findById(Long id);

    List<Movie> findByInfoUrl(URL infoUrl);
    List<Movie> findByDirector(String kwds);
    List<Movie> findByGenres(String genr);

    List<Movie> findByActorName(String name);

    Page<Movie> findAllByOrderByCreatedDesc(Pageable pageRequest);
}
