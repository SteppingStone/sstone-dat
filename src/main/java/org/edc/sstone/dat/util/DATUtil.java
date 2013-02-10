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

import javax.swing.JComponent;

import org.bushe.swing.event.EventBus;
import org.edc.sstone.dat.DesktopAuthoringToolApp;
import org.edc.sstone.dat.event.ProjectChangedEventListener;
import org.edc.sstone.dat.event.ProjectStateChangedEvent;
import org.edc.sstone.swing.Binder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Orlowski
 */
public class DATUtil {

    public static void useMonospaceFont(Component... components) {
        for (Component c : components)
            DesktopAuthoringToolApp.getInstance().getFontManager().useMonospaceFont(c);
    }

    public static void fixFont(Component component, String fontKey) {
        DesktopAuthoringToolApp.getInstance().getFontManager().fixFont(component, fontKey);
    }

    public static void fixFontRecursive(JComponent component, String fontKey) {
        Component[] children = component.getComponents();
        for (Component c : children) {
            if (c instanceof JComponent) {
                fixFontRecursive((JComponent) c, fontKey);
            }
        }
        fixFont(component, fontKey);
    }

    public static Binder bind(JComponent component) {
        return bind(component, null);
    }

    public static Binder bind(JComponent component, String componentProp) {
        return Binder.bind(component, componentProp)
                .withChangeListeners(ProjectChangedEventListener.getInstance());
    }

    public static void markDirty() {
        DesktopAuthoringToolApp.getInstance().getChangeManager().setDirty(true);
        EventBus.publish(ProjectStateChangedEvent.instance);
    }
    
    public static Logger getLogger(Class<?> _class) {
        return LoggerFactory.getLogger(_class);
    }
}
