package org.gandji.mymoviedb.api;

import org.gandji.mymoviedb.errors.MovieNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by gandji on 12/10/2019.
 */
@ControllerAdvice
public class MovieNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(MovieNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String movieNotFoundHandle(MovieNotFoundException e) {
        return e.getMessage();
    }
}
