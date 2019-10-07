package org.gandji.mymoviedb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by gandji on 02/04/2018.
 */
@Component
public class NullAuditorBean implements AuditorAware<String> {

    @Autowired
    String myMovieDBVersion;

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("MyMovieDB-"+myMovieDBVersion);
    }
}
