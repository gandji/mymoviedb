package org.gandji.mymoviedb.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.gandji.mymoviedb.data.HibernateActorDao;
import org.gandji.mymoviedb.data.HibernateMovieDao;
import org.gandji.mymoviedb.services.MyMovieDBJSCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Created by gandji on 12/12/2019.
 */
@Component
@Slf4j
public class StageListener implements ApplicationListener<StageReadyEvent> {

    // this is a spring bean so we have @Value available

    @Autowired
    HibernateMovieDao hibernateMovieDao;

    @Autowired
    HibernateActorDao hibernateActorDao;

    @Autowired
    SpringTemplateEngine pageTemplateResolver;

    @Autowired
    private MyMovieDBJSCommands myMovieDBJSCommands;

    @Autowired
    private JavascriptConsoleLogger javascriptConsoleLogger;

    private WebEngine webEngine;

    private Button maxiMiniButton;
    private ClassPathResource maxiMiniIconResource;

    @Override
    public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
        Stage  stage = stageReadyEvent.getStage();
        WebView browser = new WebView();
        webEngine = browser.getEngine();

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) ->
        {
            log.info("Load worker state changed from " + oldState + " to new state = " + newState);
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("java", javascriptConsoleLogger);
                webEngine.executeScript("console.log = function(message){java.log(message);};" +
                        "console.error = function(message) {java.error(message);};");
                log.info("Pushing callbacks in JS");
                window.setMember("mmdb", myMovieDBJSCommands);
            }

        });


        webEngine.loadContent(myMovieDBJSCommands.initialPage());

        ToolBar toolBar = new ToolBar();
        Button resetButton;
        try {
            Resource homeIconResource = new ClassPathResource("icons/baseline_home_black_18dp.png");
            javafx.scene.image.Image img = new Image(homeIconResource.getInputStream());
            resetButton = new Button("",new ImageView(img));
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            resetButton = new Button("Home");
        }
        resetButton.setOnAction(event -> {
            webEngine.loadContent(myMovieDBJSCommands.initialPage());
        });
        toolBar.getItems().add(resetButton);

        try {
            if (stage.isMaximized()) {
                maxiMiniIconResource = new ClassPathResource("icons/icons8-minimize-window-18.png");
            } else {
                maxiMiniIconResource = new ClassPathResource("icons/icons8-maximize-window-18.png");
            }
            javafx.scene.image.Image img = new Image(maxiMiniIconResource.getInputStream());
            maxiMiniButton = new Button("",new ImageView(img));
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            if (stage.isMaximized()) {
                maxiMiniButton = new Button("Minimize");
            }else {
                maxiMiniButton = new Button("Maximize");
            }
        }
        maxiMiniButton.setOnAction(event -> {
            stage.setMaximized(!stage.isMaximized());
            try {
                if (stage.isMaximized()) {
                    maxiMiniIconResource = new ClassPathResource("icons/icons8-minimize-window-18.png");
                } else {
                    maxiMiniIconResource = new ClassPathResource("icons/icons8-maximize-window-18.png");
                }
                javafx.scene.image.Image img = new Image(maxiMiniIconResource.getInputStream());
                maxiMiniButton.setText("");
                maxiMiniButton.setGraphic(new ImageView(img));
            } catch (NullPointerException | IOException e) {
            }
        });
        toolBar.getItems().add(maxiMiniButton);

        Button fullscreenButton;
        try {
            Resource fullscreenIconResource = new ClassPathResource("icons/icons8-full-screen-18.png");
            javafx.scene.image.Image img = new Image(fullscreenIconResource.getInputStream());
            fullscreenButton = new Button("",new ImageView(img));
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
            fullscreenButton = new Button("Fullscreen");
        }
        fullscreenButton.setOnAction(event -> stage.setFullScreen(!stage.isFullScreen()));
        toolBar.getItems().add(fullscreenButton);

        StackPane root = new StackPane();
        root.setPadding(new Insets(5,5,5,5));
        root.getChildren().addAll(browser, toolBar);
        StackPane.setAlignment(toolBar, Pos.BOTTOM_CENTER);

        webEngine.documentProperty().addListener(new ChangeListener<Document>() {
            @Override public void changed(ObservableValue<? extends Document> prop, Document oldDoc, Document newDoc) {
                log.info("Some document property changed");
            }

        });

        Scene scene = new Scene(root);

        stage.setTitle("MyMovieDB");
        stage.setScene(scene);
        stage.setWidth(1100);
        stage.setHeight(800);

        stage.show();
    }
}
