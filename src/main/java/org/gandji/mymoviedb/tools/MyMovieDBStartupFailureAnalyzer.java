package org.gandji.mymoviedb.tools;

import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.jdbc.datasource.embedded.OutputStreamFactory;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.sql.SQLException;

/**
 * Created by gandji on 16/05/2018.
 */
public class MyMovieDBStartupFailureAnalyzer implements FailureAnalyzer {

    public static final String connectionFailedDescription = "Cannot connect to database";
    @Override
    public FailureAnalysis analyze(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause()!=null) {
            current = current.getCause();
            if (current instanceof SQLException) {
                FailureAnalysis failureAnalysis = analyzeSQLException(throwable,current);
                if (failureAnalysis!=null) {
                    return failureAnalysis;
                }
            }
        }

        FailureWindow failureWindow = new FailureWindow("MyMovieDB startup failed for an obscure reason", "Please contact the MyMovieDB team", throwable);
        failureWindow.init();
        failureWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        failureWindow.setVisible(true);
        return new FailureAnalysis("Unknown error : "+throwable.getMessage(), "Please contact the MyMovieDB team", throwable);
    }

    private FailureAnalysis analyzeSQLException(Throwable throwable, Throwable current) {
        if (current.getMessage().startsWith("Communications link failure")) {
            String action = "Please check that you have a running MySQL server";
            FailureWindow failureWindow = new FailureWindow(connectionFailedDescription, action, current);
            failureWindow.init();
            failureWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            failureWindow.setVisible(true);
            return new FailureAnalysis(connectionFailedDescription +" : "+current.getMessage(), action, current);
        }

        String action = "Please set the credentials in the batch file or command line:\n"
                +" -Dspring.datasource.url=<your database url>\n"
                +"       probably something like jdbc:mysql://localhost:3306/mymoviedb\n"
                +" -Dspring.datasource.username=<your database user name>\n"
                +" -Dspring.datasource.password=<your database password>\n";

        FailureWindow failureWindow = new FailureWindow(connectionFailedDescription, action, current);
        failureWindow.init();
        failureWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        failureWindow.setVisible(true);
        return new FailureAnalysis(connectionFailedDescription +" : "+current.getMessage(), action, current);
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
            StyledDocument doc = info.getStyledDocument();
            addStylesToDocument(doc);
            try {
                doc.insertString(0, "MyMovieDB failed to start.\n\n"
                                +this.description+" : \n",
                                 doc.getStyle("regular"));
                doc.insertString(doc.getLength(),"\n   "+action+"\n\n",doc.getStyle("regular"));
                doc.insertString(doc.getLength(),"Message:\n",doc.getStyle("bold"));
                doc.insertString(doc.getLength(),cause.getMessage(),doc.getStyle("code"));
                doc.insertString(doc.getLength(),"\n\nStacktrace:\n",doc.getStyle("bold"));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                cause.printStackTrace(ps);
                String stack = baos.toString();
                doc.insertString(doc.getLength(),stack,doc.getStyle("code"));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            JScrollPane panel = new JScrollPane(info);
            setContentPane(panel);
            pack();
        }

        protected void addStylesToDocument(StyledDocument doc) {
            //Initialize some styles.
            Style def = StyleContext.getDefaultStyleContext().
                    getStyle(StyleContext.DEFAULT_STYLE);

            Style myDefault = doc.addStyle("regular", def);
            StyleConstants.setFontSize(myDefault,18);

            Style s = doc.addStyle("bold", myDefault);
            StyleConstants.setBold(s, true);

            s = doc.addStyle("code", myDefault);
            StyleConstants.setLeftIndent(s, 25.0f);
            StyleConstants.setFirstLineIndent(s, 25.0f);
            StyleConstants.setFontFamily(s, "Monospaced");
            StyleConstants.setFontSize(s, 15);

            s = doc.addStyle("large", myDefault);
            StyleConstants.setFontSize(s, 22);
        }
        }

}
