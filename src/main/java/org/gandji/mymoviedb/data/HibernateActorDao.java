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

import org.gandji.mymoviedb.data.repositories.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void save(Actor actor) {
        // use annotation @Transactional or this:
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.merge(actor);
        transaction.commit();
    }

    @Transactional
    public Iterable<Actor> list() {
        return actorRepository.findAll();
    }

    @Transactional
    public long count() {
        return actorRepository.count();
    }

}
