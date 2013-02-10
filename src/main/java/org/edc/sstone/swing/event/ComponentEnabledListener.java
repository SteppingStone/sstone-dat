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
package org.edc.sstone.swing.event;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.edc.sstone.util.BeanUtil;

/**
 * @author Greg Orlowski
 */
public class ComponentEnabledListener implements ChangeListener {

    private final Object componentBean;
    private final String sourceBeanProperty;
    private final JComponent target;

    public ComponentEnabledListener(Object componentBean, String sourceBeanProperty, JComponent target) {
        this.componentBean = componentBean;
        this.sourceBeanProperty = sourceBeanProperty;
        this.target = target;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object propVal = BeanUtil.getProperty(componentBean, sourceBeanProperty);
        if (propVal instanceof Boolean) {
            boolean enabled = ((Boolean) propVal).booleanValue();
            target.setEnabled(enabled);
        }
    }

}
