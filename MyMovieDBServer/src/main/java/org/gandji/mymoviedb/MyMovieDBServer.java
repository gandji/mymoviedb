package org.gandji.mymoviedb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

/**
 * Created by gandji on 21/09/2019.
 *
 *  TODO script creation is not working
 *  TODO monitor (and kill) running server?
 *  TODO rename Web to Server
 */
@Import({MyMovieDBServerConfiguration.class})
@SpringBootApplication
public class MyMovieDBServer extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MyMovieDBServer.class,args);
    }
}
