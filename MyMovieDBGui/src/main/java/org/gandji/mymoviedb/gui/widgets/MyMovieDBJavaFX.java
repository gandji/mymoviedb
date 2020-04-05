package org.gandji.mymoviedb.gui.widgets;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.gandji.mymoviedb.MyMovieDBGUI;
import org.gandji.mymoviedb.gui.StageReadyEvent;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

/**
 * Created by gandji on 08/12/2019.
 *
 * see: https://spring.io/blog/2019/01/16/spring-tips-javafx
 */
@Slf4j
public class MyMovieDBJavaFX extends Application {
    @Override
    /**
     * This function does not run on the user interface thread
     */
    public void init() throws Exception {

        ApplicationContextInitializer<GenericApplicationContext> initializer =
                new ApplicationContextInitializer<GenericApplicationContext>() {
                    @Override
                    public void initialize(GenericApplicationContext ac) {
                        ac.registerBean(Application.class, () -> MyMovieDBJavaFX.this);
                        ac.registerBean(Parameters.class, () -> getParameters());
                        ac.registerBean(HostServices.class, () -> getHostServices());
                    }
                };

        ConfigurableEnvironment environment = new StandardEnvironment();

        // tell spring we are wiring things for javafx
        environment.setActiveProfiles("javafx");

        SpringApplicationBuilder sab = new SpringApplicationBuilder()
                .environment(environment)
                .sources(MyMovieDBGUI.class)
                .initializers(initializer);
        this.context = sab.run(getParameters().getRaw().toArray(new String[0]));

    }

    @Override
    public void stop() throws Exception {
        this.context.close();
        Platform.exit();
    }

    public ConfigurableApplicationContext context;

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.context.publishEvent(new StageReadyEvent(primaryStage));

    }
}
