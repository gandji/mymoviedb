package org.gandji.mymoviedb.gui;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

/**
 * Created by gandji on 12/12/2019.
 */
public class StageReadyEvent extends ApplicationEvent {

    public Stage getStage() {
        return Stage.class.cast(getSource());
    }
    public StageReadyEvent(Stage source) {
        super( source);
    }
}
