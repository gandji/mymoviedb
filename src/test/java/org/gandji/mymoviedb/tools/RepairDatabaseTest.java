package org.gandji.mymoviedb.tools;

import org.gandji.mymoviedb.MyMovieDBTestsConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by gandji on 28/05/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyMovieDBTestsConfiguration.class)
//@SpringBootTest(classes = MyMovieDBTestsConfiguration.class)
public class RepairDatabaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(RepairDatabaseTest.class);

    @Autowired
    RepairDatabase repairDatabase;

    @Test
    public void doRepair() {
        repairDatabase.doRepair();
    }

    @Test
    public void loggingTest() {
        LOG.error("ERROR LEVEL");
        LOG.warn("WARNING LEVEL");
        LOG.info("INFO LEVEL");
        LOG.debug("DEBUG LEVEL");
        LOG.trace("TRACE LEVEL");
    }
}