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

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

import org.gandji.mymoviedb.data.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
public class HibernateGenreDao {

    public HibernateGenreDao() {
    }

    @Autowired
    private GenreRepository genreRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Genre search(String genreName) { 
        List<Genre> genreList = entityManager
                .createQuery("from Genre where name=" + Actor.normalize(genreName), Genre.class)
                .getResultList();
        if (genreList.size() > 1) {
            throw new UnsupportedOperationException("More than one genre with name " + genreName);
        }
        if (genreList.size()==0){
            return null;
        }
        return genreList.get(0);
    }

    public void save(Genre genre) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(genre);
        transaction.commit();
    }

    public List<Genre> findAll() {
        return genreRepository.findAll();
    }
}
