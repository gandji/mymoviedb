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

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.gandji.mymoviedb.data.repositories.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
public class HibernateActorDao {

    @Autowired
    private ActorRepository actorRepository;

    public HibernateActorDao() {
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Actor actor) {
        /*EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(actor);
        transaction.commit();*/
        actorRepository.save(actor);
    }

    public Iterable<Actor> findAll() {
        return actorRepository.findAll();
    }

    public Page<Actor> findAll(int offset, int pageSize) {
        Pageable pageRequest = PageRequest.of(offset, pageSize);
        Page<Actor> searchResultPage = actorRepository.findAll(pageRequest);
        return searchResultPage;

    }

    public Iterable<Actor> findByName(String name) {
        return actorRepository.findByName(name);
    }

    public long count() {return actorRepository.count();}

    @Transactional
    public List<Movie> findMoviesForActor(Actor actor) {
        Actor actorAndMovie = actorRepository.findById(actor.getId()).get();
        List<Movie> movl = new ArrayList<>();
        for (Movie movie : actorAndMovie.getMovies()) {
            movl.add(movie);
        }
        return movl;
    }

    public void delete(Actor actor) {
        actorRepository.delete(actor);
    }

    @Transactional
    public Actor removeMovie(Actor actor, Movie movie) {
        Actor actorInDB = actorRepository.findById(actor.getId()).get();
        boolean removed = actorInDB.getMovies().remove(movie);
        if (removed) {
            entityManager.merge(actorInDB);
        }
        return actorInDB;
    }

    public Actor findOne(Long id) {
        return actorRepository.findById(id).get();
    }
}
