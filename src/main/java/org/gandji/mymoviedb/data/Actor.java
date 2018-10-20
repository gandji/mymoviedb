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

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Entity
@Table(name="ACTOR")
public class Actor {

    public Actor() {
        movies = new HashSet<>();
    }

    static String normalize(String actorName) {
        actorName = actorName.replace(",", "");
        return Normalizer.normalize(actorName, Form.NFD);
    }
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO,generator="actor_seq")
    @SequenceGenerator(name="actor_seq")
    private Long id;
    
    private String name;
    
    @ManyToMany(mappedBy="actors",cascade={})
    private Set<Movie> movies = null;

    public Actor(String _name){
        name = Actor.normalize(_name);
        movies = new HashSet<>();
    } 

    public String getName() {
        return name;
    }

    public Set<Movie> getMovies() {
        return movies;
    }
    
    public void addMovie(Movie movie) {
        if (!hasMovie(movie)) {
            this.movies.add(movie);
        }
    }

    public Long getId() {
        return id;
    }

    private boolean hasMovie(Movie movie) {
        return movies.contains(movie);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object actor) {
        if (!(actor instanceof Actor)) { return false; }
        return name.equals(((Actor)actor).getName());
    }

    public void setId(Long id) {
        this.id = id;
    }
}
