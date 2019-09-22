package org.gandji.mymoviedb.gui.widgets;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.gandji.mymoviedb.ApplicationContextUtils;

import javax.swing.*;

/**
 * Created by gandji on 13/02/2018.
 */
public class LogbackLogAppender extends AppenderBase<ILoggingEvent> {

    private LogDisplay logDisplay;
    private PatternLayout layout;

    public void setLayout(PatternLayout patternLayout) { this.layout = patternLayout;}

    @Override
    protected void append(ILoggingEvent event) {
        JTextArea textArea = null;
        if (null == logDisplay) {
            if (null!= ApplicationContextUtils.getApplicationContext()) {
                logDisplay = ApplicationContextUtils.getApplicationContext().getBean(LogDisplay.class);
            }
        }
        if (null!=logDisplay) {
            textArea = logDisplay.getLogTextArea();
        }
        if (null != textArea) {
            if (null == layout) {
                textArea.append(">> "+event.getLevel() + ": ");
                textArea.append(event.getMessage());
                textArea.append("\n");
            } else {
                textArea.append(layout.doLayout(event));
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        }
    }

}
