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

import org.gandji.mymoviedb.data.repositories.MovieCountPerAttribute;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@NamedNativeQuery(name="countMoviesPerGenre",query="select g.name,count(name) FROM "
        +" genre as g "
        +" join movie_genres as mg on mg.genres_name=g.name"
        +" left join movie as m on m.id=mg.movies_id"
        +" GROUP BY g.name"
        +" order by count(name) desc"
        +" limit 40;",
        resultSetMapping = "moviesPerGenreResult")
@SqlResultSetMapping(
        name="moviesPerGenreResult",
        classes={
                @ConstructorResult(
                        targetClass = MovieCountPerAttribute.class,
                        columns = {
                                @ColumnResult(name = "g.name",type = String.class),
                                @ColumnResult(name = "count(name)",type = Long.class)
                        }
                )
        }
)
@Entity
@Table(name="GENRE")
public class Genre {

    public Genre() {
        movies = new HashSet<>();
    }

    static String normalize(String genreName) {
        return Normalizer.normalize(genreName, Form.NFD);
    }

    @Id
    private String name;
    
    @ManyToMany(mappedBy="genres",cascade=CascadeType.MERGE)
    private Set<Movie> movies = null;

    public Genre(String _name){name = Genre.normalize(_name); movies=new HashSet<>();} 

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

    private boolean hasMovie(Movie movie){
        return movies.contains(movie);
    }
}
