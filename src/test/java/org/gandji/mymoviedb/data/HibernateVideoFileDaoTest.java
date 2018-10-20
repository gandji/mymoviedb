package org.gandji.mymoviedb.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gandji.mymoviedb.MyMovieDBTestsConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gandji on 20/05/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyMovieDBTestsConfiguration.class)
public class HibernateVideoFileDaoTest {
    private static final Logger logger = Logger.getLogger(HibernateVideoFileDaoTest.class.getName());

    String directoryName = "C:/";

    @Autowired
    HibernateMovieDao hibernateMovieDao;
    @Test
    public void importJson() throws Exception {
        System.out.println("Importing DB");

        List<Movie> movies = readCollection("mymoviedb_movie",new Movie());
        //List<VideoFile> files = (List<VideoFile>) readCollection("mymoviedb_videofile",new VideoFile());

        for (Movie movie : movies) {
            System.out.println("Movie Object "+movie.getId()+"\n" + movie.getTitle());
            //hibernateMovieDao.save(movie);
        }

        /*for (VideoFile vf : files) {
            System.out.println("File id="+vf.getId()+"\n"+vf);
        }*/
    }

    private <T> List<T> readCollection(String name, T dummy) {

        logger.info("Loading collection "+name+" with parameter type "+dummy.getClass());
        //read json file data to String
        byte[] jsonData = new byte[0];
        try {
            jsonData = Files.readAllBytes(Paths.get(directoryName+ "/"+name+".json"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not load collection "+name);
            e.printStackTrace();
        }

        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //convert json string to object
        List<T> items = null;
        try {
            if (dummy instanceof  Movie) {
                items = objectMapper.readValue(jsonData, new TypeReference<List<Movie>>() {
                });
            }
            if (dummy instanceof  VideoFile) {
                items = objectMapper.readValue(jsonData, new TypeReference<List<VideoFile>>() {
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }


}