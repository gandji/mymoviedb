package org.gandji.mymoviedb;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gandji on 21/09/2019.
 */
@RestController
@Slf4j
public class MovieRestController {

    @Autowired
    private HibernateMovieDao hibernateMovieDao;

    @GetMapping("/mymoviedb/movies")
    List<Movie> all() {
       Page<Movie> li = hibernateMovieDao.findAllByOrderByCreated(0,10);
        List<Movie> list = new ArrayList<>();
        li.forEach(m->{
            log.info("Movie "+m.getTitle());
        });
        li.forEach(list::add);
        return list;
    }

    @GetMapping("/mymoviedb/movies/{id}")
    Integer oneMovie(@PathVariable Long id) {
        return id.intValue();
    }
}
