package org.gandji.mymoviedb.tools;

import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Created by gandji on 16/05/2018.
 */
public class MyMovieDBStartupFailureAnalyzer implements FailureAnalyzer {

    protected static final String action = "Please set the credentials in the batch file or command line:\n"
            +" -Dspring.datasource.url=<your database url>\n"
            +"       probably something like jdbc:mysql://localhost:3306/mymoviedb\n"
            +" -Dspring.datasource.username=<your database user name>\n"
            +" -Dspring.datasource.password=<your database password>\n";

    public static final String description = "Cannot connect to database";
    @Override
    public FailureAnalysis analyze(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause()!=null) {
            current = current.getCause();
            if (current instanceof SQLException) {
                FailureWindow failureWindow = new FailureWindow(description+" : "+current.getMessage(), action, current);
                failureWindow.init();
                failureWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                failureWindow.setVisible(true);
                return new FailureAnalysis(description+" : "+current.getMessage(), action, current);
            }
        }

        FailureWindow failureWindow = new FailureWindow("Unknown error : ", "Please contact the MyMovieDB team", throwable);
        failureWindow.init();
        failureWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        failureWindow.setVisible(true);
        return new FailureAnalysis("Unknown error : "+throwable.getMessage(), "Please contact the MyMovieDB team", throwable);
    }

    class FailureWindow extends JFrame {
        private final String description;
        private final String action;
        private final Throwable cause;

        public FailureWindow(String description, String action, Throwable current) throws HeadlessException {
            setTitle("MyMovieDB startup error");
            this.description = description;
            this.action = action;
            this.cause = current;
        }

        public void init() {
            JTextPane info = new JTextPane();
            info.setText("MyMovieDB failed to start.\n\n"+this.description+" : "+cause.getMessage()+"\n\n" +action);
            JScrollPane panel = new JScrollPane(info);
            setContentPane(panel);
            pack();
        }
    }

}
