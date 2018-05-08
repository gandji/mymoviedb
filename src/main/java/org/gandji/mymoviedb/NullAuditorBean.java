package org.gandji.mymoviedb;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
 * Created by gandji on 02/04/2018.
 */
@Component
public class NullAuditorBean implements AuditorAware<String> {
    @Override
    public String getCurrentAuditor() {
        return "MyMovieDB";
    }
}
