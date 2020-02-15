package org.gandji.mymoviedb.gui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by gandji on 13/12/2019.
 */
@Component
@Slf4j
public class JavascriptConsoleLogger {

    public void log(String message) {
        log.info("[js] "+message);
    }
    public void error(String message) {
        log.error("[js] "+message);
    }
}
