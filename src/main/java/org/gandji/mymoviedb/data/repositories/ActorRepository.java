package org.gandji.mymoviedb.data.repositories;

import org.gandji.mymoviedb.data.Actor;
import org.hibernate.annotations.NamedNativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.SqlResultSetMapping;
import java.util.List;

/**
 * Created by gandji on 13/02/2018.
 */
public interface ActorRepository extends CrudRepository<Actor,Long> {

    Iterable<Actor> findByName(String name);

}
