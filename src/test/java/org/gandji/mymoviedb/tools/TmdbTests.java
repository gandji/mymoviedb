package org.gandji.mymoviedb.tools;

import org.gandji.mymoviedb.MyMovieDBTestsConfiguration;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * Created by gandji on 15/04/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyMovieDBTestsConfiguration.class)
public class TmdbTests {

    private static final Logger LOG = LoggerFactory.getLogger(TmdbTests.class);

    @Test
    public void test1() {
        String response="n/a";
        try {
            Connection connection = Jsoup.connect("http://www.themoviedatabase.org" + "?language=fr")
                    .header("Accept", "application/json");
            response = connection.data("movie/12493-tengoku-to-jigoku","").get().text();
        } catch (IOException ex) {
            LOG.error("cannot find url ", ex);
        }
        LOG.info("RESPONSE: "+response);
    }
}
