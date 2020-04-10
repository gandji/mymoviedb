package org.gandji.mymoviedb.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

@Component
@Profile("swing")
public class SwingDialogsService implements DialogsService {

    @Override
    public void showMessageDialog(Object frame, String message, String title, MessageType messageType) {
        JOptionPane.showMessageDialog((java.awt.Component) frame, message, title,messageType.getjOptionMessageType());
    }

    @Override
    public void externalOpenUrl(String url) throws IOException {
        Desktop dt = Desktop.getDesktop();
        dt.browse(URI.create(url));
    }

    @Override
    public void externalOpenFile(Path fileToPlay) throws IOException {
        Desktop dt = Desktop.getDesktop();
        File fd = new File(fileToPlay.getParent().resolve(fileToPlay.getFileName()).toString());
        dt.open(fd);
    }
}
