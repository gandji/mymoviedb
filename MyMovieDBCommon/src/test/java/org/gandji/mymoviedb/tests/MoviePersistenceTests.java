package org.gandji.mymoviedb.tests;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.data.Actor;
import org.gandji.mymoviedb.data.Movie;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gandji on 22/12/2019.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyMovieDBTestsConfiguration.class)
@Slf4j
public class MoviePersistenceTests extends BaseTests {

    @Test
    public void testActorUnicity() {
        Assert.assertEquals(11, hibernateActorDao.count());
    }

    @Test
    public void testMergeMovie() throws MalformedURLException {
        Actor harrissonFord;
        String name = "Harrison Ford";
        Iterable<Actor> actorsWithName = hibernateActorDao.findByName(name);
        Iterator<Actor> actorsIterator = actorsWithName.iterator();
        Assert.assertNotNull("Should have Harrisson Ford in base", actorsIterator.hasNext());
        harrissonFord = actorsIterator.next();

        // load the fugitive
        Movie movie = hibernateMovieDao.findByInfoUrl(new URL("http://www.imdb.com/title/tt0106977")).iterator().next();
        movie = hibernateMovieDao.populateMovie(movie);
        log.info(String.format("Loaded movie %d : %s",movie.getId(),movie.getTitle()));

        hibernateMovieDao.addActor(movie, harrissonFord);

        Movie movieReloaded = hibernateMovieDao.findOne(movie.getId());
        List<Actor> actors = hibernateMovieDao.findActorsForMovie(movieReloaded);
        Assert.assertEquals(3,actors.size());
        Assert.assertEquals(11, hibernateActorDao.count());

        // load mononoke
        movie = hibernateMovieDao.findByInfoUrl(new URL("http://www.imdb.com/title/tt0119698")).iterator().next();
        movie = hibernateMovieDao.populateMovie(movie);
        log.info(String.format("Loaded movie %d : %s",movie.getId(),movie.getTitle()));

        hibernateMovieDao.addActor(movie, harrissonFord);

        movieReloaded = hibernateMovieDao.findOne(movie.getId());
        actors = hibernateMovieDao.findActorsForMovie(movieReloaded);
        Assert.assertEquals(4,actors.size());
        Assert.assertEquals(11, hibernateActorDao.count());
    }

    @Test
    public void testUpdateAddFileOrCreateMovie() {
        Assert.assertNotNull("To be implemented",null);
    }
}
