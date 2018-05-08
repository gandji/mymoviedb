/* 
 * Copyright (C) 2017 gandji <gandji@free.fr>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/**
 * Author:  gandji <gandji@free.fr>
 * Created: 17 févr. 2017
 */

 /*create table ker (_id bigint not null auto_increment, regexp varchar(255), primary key (_id));*/

show create table movie;

select index_name from information_schema.statistics;

/* select actor */
select * from movie left outer join movie_actors as ma on ma.movies_id=movie.id          
                     join actor on actor.id=actors_id
 where match actor.name against ("jones" in natural language mode);

/* big hack from SO */
/*select if (
    exists(
        select distinct index_name from information_schema.statistics 
        where table_schema = 'mymoviedb' 
        and table_name = 'movie' and index_name like 'fulltext_title'
    )
    ,'select ''index fulltext_title exists'' _______;'
    ,'create index fulltext_title on movie(Title)') into @a;
PREPARE stmt1 FROM @a;
EXECUTE stmt1;
DEALLOCATE PREPARE stmt1;*/

/* get this with "show create table movie" above:
CREATE TABLE `movie` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `director` varchar(255) DEFAULT NULL,
  `duree` varchar(255) DEFAULT NULL,
  `infoUrl` varchar(255) DEFAULT NULL,
  `summary` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `year` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FULLTEXT KEY `fulltext_title` (`title`),
  FULLTEXT KEY `fulltext_director` (`director`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8
*/
SELECT * FROM information_schema.statistics WHERE table_schema = 'mymoviedb' AND index_name ='fulltext_title';
select IF(  EXISTS(SELECT index_name FROM information_schema.statistics WHERE table_schema = 'mymoviedb' AND index_name ='fultext_title')
   ,1

,   0
);
/*create table ker (id bigint not null auto_increment, rege varchar(25), primary key (id));*/

select * from movie left outer join movie_genres as mg on mg.movies_id=movie.id 
                  where genres_name like 'Adventure';

select * from movie where match (title, alternateTitle) against ("Retour" in natural language mode);

select * from videofile where hashCode is NULL ;