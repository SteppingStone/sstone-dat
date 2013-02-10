/*
 * Copyright (c) 2012 EDC
 * 
 * This file is part of Stepping Stone.
 * 
 * Stepping Stone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Stepping Stone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Stepping Stone.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */
package org.edc.sstone.dat.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Collection;

/**
 * @author Greg Orlowski
 */
public class SwingUtil {

    public static String wrapHtmlTag(String s, String tag) {
        StringBuilder sb = new StringBuilder("<html><");
        sb.append(tag).append(">");
        sb.append(s);
        sb.append("</").append(tag).append("></html>");
        return sb.toString();
    }

    public static void enableComponents(boolean enabled, Component... components) {
        for (Component c : components) {
            c.setEnabled(enabled);
        }
    }

    public static void enableComponents(boolean enabled, Collection<? extends Component> components) {
        for (Component c : components) {
            c.setEnabled(enabled);
        }
    }

    public static void setMaxSizePercent(final Component c, float widthPercent, float heightPercent) {
        final Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
        float fWidth = ((float) screenDimensions.width) * widthPercent;
        float fHeight = ((float) screenDimensions.height) * heightPercent;
        c.setMaximumSize(new Dimension((int) fWidth, (int) fHeight));
    }

    public static void center(final Component c) {
        final Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenDimensions.width - c.getWidth()) / 2;
        int y = (screenDimensions.height - c.getHeight()) / 2;
        c.setLocation(x, y);
    }

    /*
     * TODO: remove lint. I played around with this to fix rendering issues in our ComponentTree,
     * but it turned out that there was a cleaner way to ensure proper sizing with this hack.
     * 
     * FontMetrics is probably better to use than creating a jlabel anyway, but if I need code like
     * this then likely I'm doing something wrong in either the code or the UI design (keep it
     * simple)
     */
    // public static Dimension getFontDimension(String str) {
    // JLabel label = new JLabel(str);
    // return label.getPreferredSize();
    // }
    //
    // public static Dimension getFontDimension(int charlen) {
    // StringBuilder sb = new StringBuilder(charlen);
    // for (int i = 0; i < charlen; i++)
    // sb.append("W");
    // return getFontDimension(sb.toString());
    // }
}
