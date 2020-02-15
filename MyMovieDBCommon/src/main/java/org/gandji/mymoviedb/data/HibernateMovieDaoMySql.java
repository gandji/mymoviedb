/*
 * This file is part of MyMovieDB.
 *
 * MyMovieDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyMovieDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>
 */

package org.gandji.mymoviedb.data;

import org.gandji.mymoviedb.data.repositories.mysql.MovieRepositoryMySql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.*;

/**
 * Created by gandji on 14/02/2018.
 */
@Component
@Profile("mysql")
public class HibernateMovieDaoMySql extends HibernateMovieDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MovieRowMapper movieRowMapper;

    @Override
    public List<Movie> findByDirectorKeywords(String kwds) {
        return ((MovieRepositoryMySql) movieRepository).findByDirectorKeywords(kwds);
    }

    @Override
    public List<Movie> findByTitleKeywords(String kwds) {
        return ((MovieRepositoryMySql) movieRepository).findByTitleKeywords(kwds);
    }

    @Override
    public List<Movie> findByAlternateTitleKeywords(String kwds) {
        return ((MovieRepositoryMySql) movieRepository).findByAlternateTitleKeywords(kwds);
    }

    @Override
    public List<Movie> findByGenreName(String genre) {
        return ((MovieRepositoryMySql) movieRepository).findByGenreName(genre);
    }

    @Override
    public List<Movie> findByCommentsKeywords(String kwds) {
        return ((MovieRepositoryMySql) movieRepository).findByCommentsKeywords(kwds);
    }

    @Override
    public List<Movie> findByActorsKeywords(String kwds) {
        return ((MovieRepositoryMySql) movieRepository).findByActorsKeywords(kwds);
    }

    @Override
    public List<Movie> findByActorName(String name) {
        return movieRepository.findByActorName(name);
    }

    private Map<String, String> gatherCriteria(String titleKeywords, String directorKeywords,
                                               String actorsKeywords, String genreKeyword,
                                               String commentsKeywords, String qualiteVideoKeyword) {
        Map<String, String> criterias = new HashMap<>();
        if (null != titleKeywords && !"".equals(titleKeywords)) {
            for (String titleKeyword : titleKeywords.split(" +")) {
                criterias.put("title:" + titleKeyword, " ( ( match title          against (\"" + titleKeyword + "\" in natural language mode) ) " +
                        "or ( match alternate_title against (\"" + titleKeyword + "\" in natural language mode) ) )");
            }
        }

        if (null != directorKeywords && !"".equals(directorKeywords)) {
            criterias.put("director", " (match director against (\"" + directorKeywords + "\" in natural language mode)) ");
        }

        if (null != actorsKeywords && !"".equals(actorsKeywords)) {
            criterias.put("actors", ""); // just to signal we search actor name
            for (String actorKeyword : actorsKeywords.split(" +")) {
                if (actorKeyword.length() > 3) {
                    criterias.put(actorKeyword, " (match a.name against (\"" + actorKeyword + "\" in natural language mode)) ");
                }
            }
        }

        if (null != genreKeyword && !"".equals(genreKeyword) && !"Any".equals(genreKeyword)) {
            criterias.put("genres", " (g.name like \"" + genreKeyword + "\" ) ");
        }

        if (null != commentsKeywords && !"".equals(commentsKeywords)) {
            criterias.put("comments", " (match comments against (\"" + commentsKeywords + "\" in natural language mode)) ");
        }

        if (null != qualiteVideoKeyword && !"".equals(qualiteVideoKeyword) && !"Any".equals(qualiteVideoKeyword)) {
            if (qualiteVideoKeyword.equals("Unknown")) {
                criterias.put("qualiteVideo", " (vf.qualite_video is null ) ");
            } else {
                criterias.put("qualiteVideo", " (vf.qualite_video like \"" + qualiteVideoKeyword + "\" ) ");
            }
        }

        return criterias;
    }

    private Iterable<Movie> assembleQueryStringThenQuery(Map<String, String> criterias, String booleanOp) {
        if (criterias.isEmpty()) {
            return findAllByOrderByCreated(0, 300).getContent();
        }

        String queryString = "SELECT * from movie ";
        if (criterias.containsKey("actors")) {
            queryString = queryString + "left outer join movie_actors as ma on movie.id=ma.movies_id " +
                    "left outer join actor as a on a.id=actors_id ";
        }
        if (criterias.containsKey("genres")) {
            queryString = queryString + "left outer join movie_genres as mg on movie.id=mg.movies_id " +
                    "left outer join genre as g on g.name=genres_name ";
        }
        if (criterias.containsKey("qualiteVideo")) {
            queryString = queryString + "left outer join videofile as vf on movie.id=vf.movie_id ";
        }

        queryString = queryString + "where ";
        boolean first = true;
        for (String criteria : criterias.values()) {

            if (null == criteria || criteria.equals("")) {
                continue;
            }

            if (!first) {
                queryString = queryString + " " + booleanOp + " ";
            }
            queryString = queryString + criteria;
            first = false;
        }

        queryString = queryString + " order by created desc";

        List<Movie> movies = jdbcTemplate.query(queryString, (Object[]) null, movieRowMapper);
        // remove duplicates
        Set<Long> uniqueMovieIds = new HashSet<>();
        List<Movie> uniqueMovies = new ArrayList<>();
        for (Movie movie : movies) {
            if (!uniqueMovieIds.contains(movie.getId())) {
                uniqueMovieIds.add(movie.getId());
                uniqueMovies.add(movie);
            }
        }
        return uniqueMovies;
    }

    @Override
    public Iterable<Movie> searchInternal(String titleKeywords, String directorKeywords,
                                          String actorsKeywords, String genreKeyword,
                                          String commentsKeywords, String qualiteVideoKeyword) {

        Map<String, String> criterias = gatherCriteria(titleKeywords, directorKeywords, actorsKeywords,
                genreKeyword, commentsKeywords, qualiteVideoKeyword);

        return assembleQueryStringThenQuery(criterias, "and");
    }

    @Override
    public Iterable<Movie> searchInternalAll(String keywords) {
        Map<String, String> criterias = gatherCriteria(keywords, keywords, keywords,
                keywords, keywords, null);

        return assembleQueryStringThenQuery(criterias, "or");
    }

    @Override
    @Transactional
    public Movie populateMovie(Movie movie) {
        List<Actor> actors = findActorsForMovie(movie);
        Set<Actor> actorSet = new HashSet<>(actors);
        movie.setActors(actorSet);

        List<VideoFile> videoFiles = findVideoFilesForMovie(movie);
        Set<VideoFile> videoFileSet = new HashSet<>(videoFiles);
        movie.setFiles(videoFileSet);

        List<Genre> genres = findGenresForMovie(movie);
        Set<Genre> genresSet = new HashSet<>(genres);
        movie.setGenres(genresSet);
        return movie;
    }
}
