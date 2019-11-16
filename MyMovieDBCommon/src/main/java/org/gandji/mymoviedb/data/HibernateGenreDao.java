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
package org.gandji.mymoviedb.data;

import org.gandji.mymoviedb.data.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
public class HibernateGenreDao {

    @Autowired
    private GenreRepository genreRepository;

    public Genre search(String genreName) {
        return genreRepository.findByName(genreName);
        /* REMOVE pour mémoire List<Genre> genreList = entityManager
                .createQuery("from Genre where name=" + Actor.normalize(genreName), Genre.class)
                .getResultList();
        if (genreList.size() > 1) {
            throw new UnsupportedOperationException("More than one genre with name " + genreName);
        }
        if (genreList.size()==0){
            return null;
        }
        return genreList.get(0);
        */
    }

    @Transactional
    public void save(Genre genre) {
        genreRepository.save(genre);
    }

    public Iterable<Genre> findAll() {
        return genreRepository.findAll();
    }
}
