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
package org.edc.sstone.dat.service;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.UIManager;

import org.edc.sstone.util.IOUtil;
import org.jdesktop.application.Resource;

/**
 * @author Greg Orlowski
 */
public class FontManager {

    @Resource(key = "font.ttf.proportional")
    protected String proportionalFontPath;

    @Resource(key = "font.ttf.monospace")
    protected String monospaceFontPath;

    private Font proportionalFont;
    private Font monospaceFont;

    public FontManager() {
    }

    protected void loadFonts() {
        proportionalFont = loadFont(proportionalFontPath);
        monospaceFont = loadFont(monospaceFontPath);
    }

    protected Font loadFont(String resourcePath) {
        Font ret = null;
        InputStream in = getClass().getResourceAsStream(resourcePath);
        try {
            ret = Font.createFont(Font.TRUETYPE_FONT, in);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeGracefully(in);
        }
        return ret;
    }

    public void useMonospaceFont(Component component) {
        component.setFont(deriveMonospaceFont(component));
    }

    public void fixFont(Component component, String fontKey) {
        Font font = component.getFont();
        component.setFont(font == null
                ? UIManager.getFont(fontKey)
                : deriveFont(font));
    }

    /**
     * A convenience method to get a monospace font in the same size + style as the component&#39;s
     * current font.
     * 
     * @param component
     *            the component component
     * @return a monospace font
     */
    protected Font deriveMonospaceFont(Component component) {
        return deriveMonospaceFont(component.getFont());
    }

    protected Font deriveMonospaceFont(Font sourceFont) {
        float size = (float) sourceFont.getSize();
        if (monospaceFont == null) {
            loadFonts();
            if (monospaceFont == null)
                return sourceFont;
        }
        return monospaceFont.deriveFont(size);
    }

    /**
     * @param sourceFont
     *            a font to use as a template
     * @return a custom font object with size and style attributes similar to the sourceFont
     */
    public Font deriveFont(Font sourceFont) {
        if (proportionalFont == null) {
            loadFonts();
            if (proportionalFont == null)
                return sourceFont;
        }
        return proportionalFont.deriveFont(sourceFont.getSize2D());
    }

    public Font getProportionalFont(int size) {
        return proportionalFont.deriveFont((float) size);
    }

    /*
     * I think we can/should just use LafManager to cast a wider net and change the font in
     * UIDefaults. Remove this if it indeed proves to be superfluous.
     */
    // public void useProportionalFont(JComponent... jcomponents) {
    // for (JComponent jcomponent : jcomponents) {
    // Font componentFont = jcomponent.getFont();
    // if (componentFont != null) {
    // jcomponent.setFont(deriveFont(componentFont));
    // }
    // }
    // }

}
