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
package org.gandji.mymoviedb.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import javax.swing.JComponent;

import org.gandji.mymoviedb.gui.widgets.NewLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author gandji <gandji@free.fr>
 */
@Component
@Deprecated
public class ProgressPane extends JComponent {

    protected int alphaLevel = 200;
    protected float shield = 0.70f;
    protected RenderingHints hints = null;
    protected Font font;
    
    @Autowired
    NewLayout mainFrame;

    public ProgressPane() {
//        this.hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        this.hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        
         Font fonts[] = 
      GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
         
         for (Font it : fonts) {
             if (it.getName().contains("Lucida")){
                 this.font = it;
             }
         }
//     String fonts[] = 
//      GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFontsNames();
//   
//    for ( int i = 0; i < fonts.length; i++ )
//    {
//      System.out.println(fonts[i]);
//    }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        //g2.setRenderingHints(hints);
            
            g2.setColor(new Color(255, 255, 255, (int) (alphaLevel * shield)));
            g2.fillRect(0, 0, mainFrame.getWidth(), mainFrame.getHeight());
            
        // Draw Text
       g2.setColor(new Color(0, 0, 0,255));
        g2.setFont(font.deriveFont(18.f));
        g2.drawString("Please wait, processing file.......", (int)(mainFrame.getWidth()*0.35), 
                (int)(mainFrame.getHeight()*0.35));
    }
}
