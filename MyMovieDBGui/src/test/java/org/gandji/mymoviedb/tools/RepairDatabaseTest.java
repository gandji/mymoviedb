package org.gandji.mymoviedb.tools;

import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.MyMovieDBTestsConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by gandji on 28/05/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyMovieDBTestsConfiguration.class)
//@SpringBootTest(classes = MyMovieDBTestsConfiguration.class)
@Slf4j
public class RepairDatabaseTest {

    @Autowired
    RepairDatabase repairDatabase;

    @Test
    public void doRepair() {
        repairDatabase.doRepair();
    }

    @Test
    public void loggingTest() {
        log.error("ERROR LEVEL");
        log.warn("WARNING LEVEL");
        log.info("INFO LEVEL");
        log.debug("DEBUG LEVEL");
        log.trace("TRACE LEVEL");
    }
}