package org.gandji.mymoviedb.tests;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.data.*;
import org.gandji.mymoviedb.data.repositories.GenreRepository;
import org.gandji.mymoviedb.services.MovieDaoServices;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gandji on 22/12/2019.
 */
@Slf4j
public class BaseTests {

    @Autowired
    HibernateMovieDao hibernateMovieDao;

    @Autowired
    HibernateActorDao hibernateActorDao;

    @Autowired
    HibernateVideoFileDao hibernateVideoFileDao;

    @Autowired
    MovieDaoServices movieDaoServices;

    @Autowired
    GenreRepository genreRepository;

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
            log.info("Saving movie "+movie.getTitle());
            movieDaoServices.checkActorsAndSaveMovie(movie);
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
            log.info("Saving movie "+movie.getTitle());
            movieDaoServices.checkActorsAndSaveMovie(movie);
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
            log.info("Saving movie "+movie.getTitle());
            movie = movieDaoServices.checkActorsAndSaveMovie(movie);
            movieDaoServices.addFileToMovie(movie,vf);
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
            log.info("Saving movie "+movie.getTitle());
            movieDaoServices.checkActorsAndSaveMovie(movie);
        }

        Assert.assertEquals(4, hibernateMovieDao.count());
        Assert.assertEquals(11, hibernateActorDao.count());
        Assert.assertEquals(1, hibernateVideoFileDao.count());
        Assert.assertEquals(7, genreRepository.count());

        List<Movie> tommyLeeJonesMovies = hibernateMovieDao.findByActorName("Tommy Lee Jones");
        Assert.assertEquals(2,tommyLeeJonesMovies.size());

        List<Movie> backToTheFutures = null;
        try {
            backToTheFutures = hibernateMovieDao.findByInfoUrl(new URL("http://www.imdb.com/title/tt0088763"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Assert.fail("Could not read URL for back to the future");
        }
        Assert.assertEquals(1,backToTheFutures.size());
        Movie backToTheFuture = backToTheFutures.get(0);
        backToTheFuture = hibernateMovieDao.populateMovie(backToTheFuture);
        Assert.assertEquals(1,backToTheFuture.getFiles().size());
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
        log.info("Deleting "+count+" movies");
        for (Movie movie : hibernateMovieDao.findAll()) {
            log.info(String.format("Deleting movie %d: %s",movie.getId(),movie.getTitle()));
            hibernateMovieDao.deleteMovie(movie);
        }
        for (Actor actor : hibernateActorDao.findAll()) {
            log.info(String.format("Deleting actor %d: %s",actor.getId(),actor.getName()));
            hibernateActorDao.delete(actor);
        }
        for (VideoFile videoFile : hibernateVideoFileDao.findAll()) {
            log.info(String.format("Deleting file %d: %s",videoFile.getId(),videoFile.getFileName()));
            hibernateVideoFileDao.deleteFile(videoFile);
        }
        for (Genre genre : genreRepository.findAll()) {
            log.info(String.format("Deleting genre %s",genre.getName()));
            genreRepository.delete(genre);
        }
    }

}
