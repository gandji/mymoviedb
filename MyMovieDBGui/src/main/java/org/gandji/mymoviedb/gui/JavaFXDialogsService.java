package org.gandji.mymoviedb.gui;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Profile("javafx")
public class JavaFXDialogsService implements DialogsService {

    final static Logger logger = LoggerFactory.getLogger(JavaFXDialogsService.class);

    @Override
    public void showMessageDialog(Object frame, String message, String title, MessageType messageType) {
        logger.info(String.format("[%s] GOT MESSAGE JAVAFX: %s", title, message));
        // create a alert
        Alert a = new Alert(messageType.getAlertType());
        a.setHeaderText(message);
        a.show();
    }

    @Override
    public void externalOpenUrl(String url) throws IOException {
        final Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    Desktop.getDesktop().browse(URI.create(url));
                } catch (IOException e) {
                    System.err.println(e.toString());
                }
                return null;
            }
        };

        final Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void externalOpenFile(Path fileToPlay) throws IOException {

        if (!Files.isReadable(fileToPlay)) {
            throw new IOException(String.format("Cannot find file %s",fileToPlay));
        }

        final Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    File fd = new File(fileToPlay.getParent().resolve(fileToPlay.getFileName()).toString());
                    Desktop.getDesktop().open(fd);
                } catch (IOException e) {
                    System.err.println(e.toString());
                }
                return null;
            }
        };

        final Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
