/*
 * Copyright (C) 2017 gandji <gandji@free.fr>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.gandji.mymoviedb.gui.widgets;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;

/**
 *
 * @author gandji <gandji@free.fr>
 */
public class JavaUtilLogAppender extends Handler {

    private JTextArea textArea = null;

    public JavaUtilLogAppender(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void publish(LogRecord logRecord) {
        if (null != textArea) {
            textArea.append(logRecord.getLevel() + ": ");
            //textArea.append(logRecord.getSourceClassName() + ":");
            //textArea.append(logRecord.getSourceMethodName() + ":");
            //textArea.append("   <" + logRecord.getMessage() + ">");
            textArea.append(logRecord.getMessage());
            textArea.append("\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    @Override
    public void close() {}

    @Override
    public void flush() {}

}
