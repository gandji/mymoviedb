package org.gandji.mymoviedb.data.repositories;

import org.gandji.mymoviedb.data.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by gandji on 13/02/2018.
 */
public interface ActorRepository extends JpaRepository<Actor,Long> {

    Iterable<Actor> findByName(String name);

}
