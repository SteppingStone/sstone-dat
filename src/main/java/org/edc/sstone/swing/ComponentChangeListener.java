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
package org.edc.sstone.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.edc.sstone.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Orlowski
 */
public class ComponentChangeListener extends BasePropertyChangeListener
        implements PropertyChangeListener, ChangeListener, ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(ComponentChangeListener.class);
    private String sourceComponentPropertyName = null;

    // public ComponentChangeListener(Object boundBean) {
    // this(boundBean, null);
    // }

    public ComponentChangeListener(Object boundBean, String beanPropertyName) {
        this(boundBean, beanPropertyName, null);
    }

    public ComponentChangeListener(Object boundBean, String beanPropertyName, String sourceComponentPropertyName) {
        super(boundBean, beanPropertyName);
        this.sourceComponentPropertyName = sourceComponentPropertyName;
    }

    // public ComponentChangeListener(Object sourceObject, Object boundBean, String
    // beanPropertyName) {
    // super(boundBean, beanPropertyName);
    // this.sourceObject = sourceObject;
    // }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (sourceObject == null) {
            sourceObject = evt.getSource();
        }

        logger.debug("Prop change: " + sourceObject.getClass().getSimpleName() +
                " " + sourceObject.hashCode());
        Object newValue = evt.getNewValue();
        if (!areEqual(evt.getOldValue(), newValue)) {
            updateBean(newValue);
        }
    }

    protected void handleEvent(Object source) {
        if (sourceObject == null) {
            sourceObject = source;
        }

        Object newValue = BeanUtil.getProperty(sourceObject, sourceComponentPropertyName);
        if (logger.isDebugEnabled()) {
            String msg = String.format("[EVENT] component: [%s]; component prop: [%s]; "
                    + "beanClass: [%s]; bean prop: [%s]; "
                    + "value: [%s]",
                    sourceObject.getClass().getSimpleName(), sourceComponentPropertyName,
                    boundBean.getClass().getSimpleName(), beanPropertyName, newValue.toString());
            logger.debug(msg);
        }
        updateBean(newValue);
    }

    @Override
    public void stateChanged(ChangeEvent evt) {
        handleEvent(evt.getSource());
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        handleEvent(evt.getSource());
    }
}
