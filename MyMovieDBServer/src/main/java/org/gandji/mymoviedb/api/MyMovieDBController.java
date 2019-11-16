package org.gandji.mymoviedb.api;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.data.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gandji on 12/05/2019.
 */
@Controller
@Slf4j
public class MyMovieDBController {

    @Autowired
    private HibernateMovieDao hibernateMovieDao;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    @Autowired
    MovieResourceAssembler movieResourceAssembler;

    @GetMapping("/mymoviedb")
    public String greeting(Model model) {
        log.info("Main MymovieDB page requested");

        List<MovieResource> movies = hibernateMovieDao.findAllByOrderByCreated(0,15)
                .stream()
                .map(movie -> movieResourceAssembler.toResource(movie)).collect(Collectors.toList());

        model.addAttribute("movies",
               movies
                );

        return "mymoviedb_start_page_materialize";
    }

    @GetMapping("mymoviedb/{movieId}")
    public String oneMovie(@PathVariable Long movieId, Model model) {
        if (null!=movieId) {
            log.info("Individual movie controller mapping: id = "+movieId);
            Movie movie = hibernateMovieDao.findOne(movieId);
            MovieResource movieResource = movieResourceAssembler.toResource(movie);
            model.addAttribute("movie", movieResource);
        } else {
            log.info("Individual movie controller mapping: id = null");
        }
        return "movies::oneMovie";
    }

    @GetMapping("mymoviedb/search")
    public String searchAll(@RequestParam("q") String q, Model model) {

        List<MovieResource> movies;

        if (null==q || "".equals(q)) {
            movies = hibernateMovieDao.findAllByOrderByCreated(15,10)
                    .stream()
                    .map(movie -> movieResourceAssembler.toResource(movie)).collect(Collectors.toList());

        } else {
            movies = new ArrayList<>();
            StringBuffer kwds = new StringBuffer();
            for (String kw : q.split(";")) {
                kwds.append(kw + " ");
            }
            log.info("Searching for " + kwds.toString());
        }
        model.addAttribute("movies",
                movies
        );
        return "mymoviedb_start_page_materialize::moviesCarousel";
    }
}
