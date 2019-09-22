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
/**
 * Author:  gandji <gandji@free.fr>
 * Created: 21 janv. 2017
 */

SELECT * FROM movie LIMIT 100;
/*select title, director from movie where MATCH Director AGAINST ("miyazaki'" in natural language mode);*/
create fulltext index fulltext_director on movie (Director);
select title, director from movie where MATCH Director AGAINST ("sonnenfeld'" in natural language mode);
create fulltext index fulltext_title on movie (Title);
select title, director from movie where MATCH Title AGAINST ("mononoke+hime" in natural language mode);
/*See also fulltext index built in:*/
/*CREATE TABLE articles (
      id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
      title VARCHAR(200),
      body TEXT,
      FULLTEXT (title,body)
    ) ENGINE=InnoDB;*/
/* find all files for all movies, put all files--directory in one cell */
SELECT
  movie.*,
  GROUP_CONCAT(fileName,"---",directory) AS `Files`
FROM movie
  LEFT JOIN videofile
    ON videofile.movie_id = movie.id
GROUP BY movie.id;
/* list all movies and their files, one line per movie-file pair */
SELECT m.id, m.title, vf.fileName
FROM movie as m
LEFT JOIN videofile as vf ON m.id = vf.movie_id;
/* list all movies and genre */
select movie.title, genre.name from movie
inner join movie_genres  mg on mg.movies_id=movie.id
inner join genre on mg.genres_name=genre.name;

/* list all movies of given genre */
select movie.title, genre.name from movie
inner join movie_genres  mg on mg.movies_id=movie.id
inner join genre on mg.genres_name=genre.name
where genre.name="Adventure";