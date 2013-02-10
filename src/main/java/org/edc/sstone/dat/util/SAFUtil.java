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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

/**
 * @author Greg Orlowski
 */
public class SAFUtil {

    // public static void normalizeResourceFolder(ApplicationContext appContext) {
    // appContext.getResourceManager().setResourceFolder(null);
    // }

    public static ApplicationContext getContext() {
        return Application.getInstance().getContext();
    }

    public static <C extends JComponent> C newComponent(Class<C> c, String name) {
        C ret;
        try {
            ret = (C) c.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ret.setName(name);
        return (C) ret;
    }

    public static <C extends JComponent> C nameComponent(C c, String name) {
        c.setName(name);
        return c;
    }

    public static JButton button(String name) {
        JButton ret = nameComponent(new JButton(), name);
        ret.setBorderPainted(false);
        // ret.setSelected(false);
        ret.setFocusable(false);
        return ret;
    }

    public static JButton button(String name, ActionListener actionListener) {
        JButton ret = button(name);
        ret.addActionListener(actionListener);
        return ret;
    }

    /**
     * A convenience method to initialize a button with an action
     * 
     * @param name
     * @param action
     * @return
     */
    public static JButton button(String name, Action action) {
        JButton ret = button(name);
        ret.setAction(action);
        return ret;
    }

    public static JLabel label(String name) {
        JLabel ret = new JLabel();
        ret.setName(name);
        return ret;
    }

    public static void setDefaultName(Component component) {
        String className = component.getClass().getSimpleName();
        char firstLetter = Character.toLowerCase(className.charAt(0));
        component.setName(firstLetter + className.substring(1));
    }

    public static Action action(String name) {
        return getContext().getActionMap().get(name);
    }

    public static Action action(Object actionObject, String name) {
        return getContext().getActionMap(actionObject).get(name);
    }

    public static void injectComponents(Component... components) {
        for (Component c : components)
            Application.getInstance().getContext().getResourceMap().injectComponents(c);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> buildResourceMap(String prefix, Class<V> resourceClass, K[] keys) {
        Map<K, V> ret = new HashMap<K, V>();
        for (K key : keys) {
            String resourceName = prefix + '.' + key.toString();
            Object resource = getContext().getResourceMap().getObject(resourceName, resourceClass);
            if (resource != null) {
                ret.put(key, (V) resource);
            }
        }
        return ret;
    }

    public static Color getColor(String key) {
        return getResourceMap().getColor(key);
    }

    public static Dimension getDimension(String key) {
        return (Dimension) getResourceMap().getObject(key, Dimension.class);
    }

    private static ResourceMap getResourceMap() {
        return Application.getInstance().getContext().getResourceMap();
    }

    public static String getString(String key, Object... args) {
        ResourceMap m = getResourceMap();
        return m.containsKey(key)
                ? m.getString(key, args)
                : null;
    }

    public static Border getBorder(String key) {
        ResourceMap m = getResourceMap();
        return m.containsKey(key) ? (Border) m.getObject(key, EmptyBorder.class) : null;
    }

    public static Icon getIcon(String key) {
        return getResourceMap().getIcon(key);
    }

}
