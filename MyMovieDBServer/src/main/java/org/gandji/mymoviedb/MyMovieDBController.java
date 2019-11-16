package org.gandji.mymoviedb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by gandji on 12/05/2019.
 */
@Controller
@Slf4j
public class MyMovieDBController {

    @GetMapping("/mymoviedb")
    public String greeting(Model model) {
        log.info("Main MymovieDB page requested");

        return "mymoviedb_start_page";
    }

}
