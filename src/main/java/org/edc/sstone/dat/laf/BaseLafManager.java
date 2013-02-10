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
package org.edc.sstone.dat.laf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.edc.sstone.dat.DesktopAuthoringToolApp;
import org.edc.sstone.dat.service.FontManager;
import org.edc.sstone.dat.util.DATUtil;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Orlowski
 */
public class BaseLafManager implements LafManager {

    private static final Logger logger = LoggerFactory.getLogger(LafManagerFactory.class);

    protected ResourceMap getResourceMap() {
        return Application.getInstance().getContext().getResourceManager()
                .getResourceMap(getClass(), BaseLafManager.class);
    }

    public void configureLaf() {
        ResourceMap rm = getResourceMap();

        // rm.getString(key, args)
        // Application.getInstance().getContext().getResourceMap(cls)

        // This should get all the keys in the BaseLafManager and in subclasses but
        // remove all keys present in the Application resource map(s)
        Set<String> lafManagerKeys = new HashSet<String>(rm.keySet());
        lafManagerKeys.removeAll(Application.getInstance().getContext().getResourceMap().keySet());

        UIDefaults uid = UIManager.getDefaults();
        for (String key : lafManagerKeys) {
            Object value = uid.get(key);
            if (value != null) {
                if (value instanceof Insets) {
                    Insets insets = (Insets) rm.getObject(key, Insets.class);
                    logger.debug("Setting UIDefaults Inset key: " + key + " to: " + insets);
                    uid.put(key, insets);
                } else if (value instanceof Color) {
                    logger.debug("Setting UIDefaults Color key: " + key);
                    uid.put(key, rm.getColor(key));
                } else if (value instanceof String) {
                    logger.debug("Setting UIDefaults String key: " + key);
                    uid.put(key, rm.getString(key));
                } else if (value instanceof Integer) {
                    logger.debug("Setting UIDefaults Integer key: " + key);
                    uid.put(key, rm.getInteger(key));
                } else if (value instanceof Boolean) {
                    logger.debug("Setting UIDefaults Boolean key: " + key);
                    uid.put(key, rm.getBoolean(key));
                }
                /*
                 * TODO: configure font here... I would have to get the key, get the font size, then
                 * derive the proper-sized sans font.
                 */
            }
        }

        fixFonts();
        configureListBorder();
    }

    protected void configureDefault(final String key, final Class<?> _class) {
        ResourceMap rm = getResourceMap();
        Object val = rm.getObject(key, _class);
        UIManager.getDefaults().put(key, val);
    }

    /*
     * TODO: this does not really have an
     */
    protected void configureListBorder() {
        configureDefault("List.cellNoFocusBorder", EmptyBorder.class);
        configureDefault("List.focusSelectedCellHighlightBorder", EmptyBorder.class);
    }

    protected void fixFonts() {
        for (String fontKey : defaultFontSizes().keySet()) {
            replaceFont(fontKey);
        }
        // UIDefaults uid = UIManager.getDefaults();
        // for (Object keyObj : uid.keySet()) {
        // String key = keyObj.toString();
        // if (key.toLowerCase().endsWith("font")) {
        // Object val = uid.get(key);
        // if (val instanceof Font) {
        // replaceFont(key);
        // }
        // }
        // }
    }

    protected Map<String, Integer> defaultFontSizes() {
        Map<String, Integer> ret = new HashMap<String, Integer>();
        ret.put("TextArea.font", 13);
        ret.put("TextField.font", 11);
        ret.put("FormattedTextField.font", 11);
        ret.put("TextPane.font", 11);
        ret.put("List.font", 12);
        ret.put("Table.font", 12);
        ret.put("FileChooser.listFont", 12);
        ret.put("Panel.font", 13);
        ret.put("Label.font", 11);
        ret.put("Tree.font", 11);
        return ret;
    }

    protected void replaceFont(String key) {
        UIDefaults uid = UIManager.getDefaults();
        FontManager fm = DesktopAuthoringToolApp.getInstance().getFontManager();

        Font font = uid.getFont(key);
        font = (font != null) ? font = fm.deriveFont(font) : fm.getProportionalFont(defaultFontSizes().get(key));
        uid.put(key, font);
    }

    @Override
    public void configure(JTree treeComponent) {
        // do nothing
    }

    @Override
    public void fixFont(JFileChooser fileChooser) {
        /*
         * TODO: I may want to do this selectively in only certain LAFs
         */
        DATUtil.fixFontRecursive(fileChooser, "Label.font");
    }

}
