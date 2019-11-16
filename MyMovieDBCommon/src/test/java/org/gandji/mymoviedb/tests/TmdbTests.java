package org.gandji.mymoviedb.tests;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
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
public class TmdbTests {

    Logger logger = LoggerFactory.getLogger(TmdbTests.class);

    @Value("${mymoviedb.tmdb.apikey}")
    String tmdbApiKey;

    @Autowired
    HibernateMovieDao hibernateMovieDao;

    @Autowired
    HibernateActorDao hibernateActorDao;

    @Autowired
    HibernateVideoFileDao hibernateVideoFileDao;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieInfoSearchService movieInfoSearchService;

    protected void fillDbForTests() {
        {
            Movie movie = new Movie();
            movie.setTitle("Men in Black 3 (2012)");
            movie.setYear("(2012)");
            movie.setInfoUrlAsString("http://www.imdb.com/title/tt1409024");
            movie.setDirector("Barry Sonnenfeld");
            movie.addActorByName("Will Smith");
            movie.addActorByName("Tommy Lee Jones");
            movie.addActorByName("Josh Brolin");
            movie.setSummary("Agent J travels in time to M.I.B.'s early days in 1969 to stop an alien from assassinating his friend Agent K and changing history.");
            movie.setDuree("1h 46min");
            movie.addGenreByName("Action");
            movie.addGenreByName("Adventure");
            movie.addGenreByName("Comedy");
            logger.info("Saving movie "+movie.getTitle());
            hibernateMovieDao.updateOrCreateMovie(movie);
        }
        {
            Movie movie = new Movie();
            movie.setTitle("Mononoke-hime (1997)");
            movie.setYear("(1997)");
            movie.setInfoUrlAsString("http://www.imdb.com/title/tt0119698");
            movie.setDirector("Hayao Miyazaki");
            movie.addActorByName("Yôji Matsuda");
            movie.addActorByName("Yuriko Ishida");
            movie.addActorByName("Yûko Tanaka");
            movie.setSummary("On a journey to find the cure for a Tatarigami's curse, Ashitaka finds himself in the middle of a war between the forest gods and Tatara, a mining colony. In this quest he also meets San, the Mononoke Hime.");
            movie.setDuree("2h 14min");
            movie.addGenreByName("Animation");
            movie.addGenreByName("Adventure");
            movie.addGenreByName("Fantasy");
            logger.info("Saving movie "+movie.getTitle());
            hibernateMovieDao.updateOrCreateMovie(movie);
        }
        {
            Movie movie = new Movie();
            movie.setTitle("Back to the Future (1985)");
            movie.setYear("(1985)");
            movie.setInfoUrlAsString("http://www.imdb.com/title/tt0088763");
            movie.setDirector("Robert Zemeckis");
            movie.addActorByName("Christopher Lloyd");
            movie.addActorByName("Michael J. Fox");
            movie.addActorByName("Lea Thompson");
            movie.setSummary("Marty McFly, a 17-year-old high school student, is accidentally sent 30 years into the past in a time-traveling DeLorean invented by his close friend, the maverick scientist Doc Brown.");
            movie.setDuree("1h 56min");
            movie.addGenreByName("Animation");
            movie.addGenreByName("Adventure");
            movie.addGenreByName("Fantasy");
            VideoFile vf = new VideoFile();
            vf.setDirectory("C:\\");
            vf.setFileName("Back.to.the.Future.1985.720p.BrRip.x264.YIFY.mp4");
            vf.setMovie(movie);
            logger.info("Saving movie "+movie.getTitle());
            hibernateMovieDao.updateOrCreateMovie(movie,vf);
        }
        {
            Movie movie = new Movie();
            movie.setTitle("The Fugitive (1993)");
            movie.setYear("(1993)");
            movie.setInfoUrlAsString("http://www.imdb.com/title/tt0106977");
            movie.setDirector("Andrew Davis");
            movie.addActorByName("Harrison Ford");
            movie.addActorByName("Tommy Lee Jones");
            movie.addActorByName("Sela Ward");
            movie.setSummary("Dr. Richard Kimble, unjustly accused of murdering his wife, must find the real killer while being the target of a nationwide manhunt lead by a seasoned US Marshall.");
            movie.setDuree("2h 10min");
            movie.addGenreByName("Action");
            movie.addGenreByName("Crime");
            movie.addGenreByName("Drama");
            logger.info("Saving movie "+movie.getTitle());
            hibernateMovieDao.updateOrCreateMovie(movie);
        }

        Assert.assertEquals(4, hibernateMovieDao.count());
        Assert.assertEquals(11, hibernateActorDao.count());
        Assert.assertEquals(1, hibernateVideoFileDao.count());
        Assert.assertEquals(7, genreRepository.count());

    }

    @Before
    public void before(){
        emptyDbForTests();
        Assert.assertEquals(0, hibernateMovieDao.count());
        Assert.assertEquals(0, hibernateActorDao.count());
        Assert.assertEquals(0, hibernateVideoFileDao.count());
        Assert.assertEquals(0, genreRepository.count());
        fillDbForTests();
    }

    @After
    public void after() {
    }

    private void emptyDbForTests() {
        List<Movie> moviesToDelete = new ArrayList<>();
        long count = hibernateMovieDao.count();
        logger.info("Deleting "+count+" movies");
        for (Movie movie : hibernateMovieDao.findAll()) {
            hibernateMovieDao.deleteMovie(movie);
        }
        for (Actor actor : hibernateActorDao.findAll()) {
            hibernateActorDao.delete(actor);
        }
        for (Genre genre : genreRepository.findAll()) {
            genreRepository.delete(genre);
        }
    }

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

                logger.info("Extracted Id : "+tmdbDescriptor.id);
                MovieInfo movieInfo = tmdbApi.getMovieInfo(tmdbDescriptor.id, "fr");
                logger.info(movieInfo.toString());
            Assert.assertEquals("Entre le ciel et l'enfer",movieInfo.getTitle());

        } catch (MovieDbException e) {
            e.printStackTrace();
        }

    }
}
