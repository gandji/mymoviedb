package org.gandji.mymoviedb.tools;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.MyMovieDBTestsConfiguration;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * Created by gandji on 15/04/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyMovieDBTestsConfiguration.class)
@Slf4j
public class TmdbTests {

    @Test
    public void test1() {
        String response="n/a";
        try {
            Connection connection = Jsoup.connect("http://www.themoviedatabase.org" + "?language=fr")
                    .header("Accept", "application/json");
            response = connection.data("movie/12493-tengoku-to-jigoku","").get().text();
        } catch (IOException ex) {
            log.error("cannot find url ", ex);
        }
        log.info("RESPONSE: "+response);
    }
}
