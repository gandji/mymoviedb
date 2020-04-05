package org.gandji.mymoviedb.gui;

import javafx.scene.control.Alert;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;

public interface DialogsService {

    enum MessageType {
        INFO(JOptionPane.INFORMATION_MESSAGE, Alert.AlertType.INFORMATION),
        WARN(JOptionPane.WARNING_MESSAGE, Alert.AlertType.WARNING),
        ERROR(JOptionPane.ERROR_MESSAGE, Alert.AlertType.ERROR);

        final int jOptionMessageType;
        final Alert.AlertType alertType;

        MessageType(int jOptionMessageType, Alert.AlertType alertType) {
            this.jOptionMessageType = jOptionMessageType;
            this.alertType = alertType;
        }

        public int getjOptionMessageType() {
            return jOptionMessageType;
        }

        public Alert.AlertType getAlertType() {
            return alertType;
        }
    }

    void showMessageDialog(Object frame,
                           String message,String title,
                           MessageType messageType);

    void externalOpenUrl(String url) throws IOException;
    void externalOpenFile(Path fileToPlay) throws IOException;
}
