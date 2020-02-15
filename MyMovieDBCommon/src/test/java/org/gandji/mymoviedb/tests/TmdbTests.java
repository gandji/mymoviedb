package org.gandji.mymoviedb.tests;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.data.*;
import org.gandji.mymoviedb.data.repositories.GenreRepository;
import org.gandji.mymoviedb.scrapy.MovieInfoSearchService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gandji on 15/04/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyMovieDBTestsConfiguration.class)
@Slf4j
public class TmdbTests extends BaseTests {

    @Value("${mymoviedb.tmdb.apikey}")
    String tmdbApiKey;

    @Autowired
    private MovieInfoSearchService movieInfoSearchService;

    @Test
    public void test1() {
        URL url = null;
        URL url2 = null;
        URL url3 = null;
        try {
            url = new URL("https://www.themoviedb.org/movie/12493");
            url2 = new URL("https://www.themoviedb.org/movie/12493-rubbish-3");
            url3 = new URL("https://www.themoviedb.org/movie/12493?lang=fr");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        MovieInfoSearchService.TmdbDescriptor tmdbDescriptor = movieInfoSearchService.getTmbdIdFromUrl(url.toString());
        MovieInfoSearchService.TmdbDescriptor tmdbDescriptor2 = movieInfoSearchService.getTmbdIdFromUrl(url2.toString());
        MovieInfoSearchService.TmdbDescriptor tmdbDescriptor3 = movieInfoSearchService.getTmbdIdFromUrl(url3.toString());

        Assert.assertEquals((Integer)12493, tmdbDescriptor.id);
        Assert.assertEquals((Integer)12493, tmdbDescriptor2.id);
        Assert.assertEquals((Integer)12493, tmdbDescriptor3.id);

        TheMovieDbApi tmdbApi = null;
        try {
            tmdbApi = new TheMovieDbApi(tmdbApiKey);

                log.info("Extracted Id : "+tmdbDescriptor.id);
                MovieInfo movieInfo = tmdbApi.getMovieInfo(tmdbDescriptor.id, "fr");
                log.info(movieInfo.toString());
            Assert.assertEquals("Entre le ciel et l'enfer",movieInfo.getTitle());

        } catch (MovieDbException e) {
            e.printStackTrace();
        }

    }
}
