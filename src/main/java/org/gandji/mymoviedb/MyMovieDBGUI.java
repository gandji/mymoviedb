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
package org.gandji.mymoviedb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gandji <gandji@free.fr>
 * 
 */
@Import(MyMovieDBConfiguration.class)
@SpringBootApplication// replacement for Configuration, ComponentScan and EnableAutoConfiguration
public class MyMovieDBGUI {

    private static final Logger LOG = Logger.getLogger(MyMovieDBGUI.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         /* set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                LOG.info("Available look end feel: "+info.getName());
            }
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            Font orig = javax.swing.UIManager.getFont("defaultFont");
            if (orig != null) {
                LOG.info("ORIGINAL FONT : "+orig.toString());
            }

            /* cannot use injection here */
            MyMovieDBPreferences preferences = new MyMovieDBPreferences();
            int fontSize = preferences.getFontSize();
            Font f = new javax.swing.plaf.FontUIResource("Lucida",Font.PLAIN,fontSize);
            javax.swing.UIManager.getLookAndFeelDefaults().put("defaultFont", f);

            /*
              java.util.Enumeration keys = javax.swing.UIManager.getLookAndFeelDefaults().keys();
              while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = javax.swing.UIManager.get(key);
                if (value instanceof javax.swing.plaf.FontUIResource)
                    javax.swing.UIManager.put(key, f);
                LOG.info("Set font for : "+key.toString());
            }*/

        } catch (ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        // launch spring boot application not as web service but using the runner!
        SpringApplication.run(MyMovieDBGUI.class, args);
        
    }

}
