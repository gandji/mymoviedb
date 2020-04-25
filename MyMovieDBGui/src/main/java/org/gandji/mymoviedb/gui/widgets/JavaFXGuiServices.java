package org.gandji.mymoviedb.gui.widgets;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.data.Movie;
import org.gandji.mymoviedb.gui.MovieGuiService;
import org.gandji.mymoviedb.services.MovieDaoGuiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JavaFXGuiServices {

    @Autowired
    private MovieGuiService movieGuiService;

    public void launchMovieDescriptionPanel(Movie movie) {

        try {

            movieGuiService.launchMovieDescriptionDialog(movie, null, null, false);

            /* dev Parent root = FXMLLoader.load(getClass().getResource("/scenes/movie_description_panel.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("New Window");
            stage.setScene(scene);
            stage.setMaxHeight(Double.MAX_VALUE);
            stage.setMaxWidth(Double.MAX_VALUE);
            stage.show(); */

        } catch (Exception e) {
            log.warn("Failed to create new Window.", e);
            //e.printStackTrace();
        }

    }
}
