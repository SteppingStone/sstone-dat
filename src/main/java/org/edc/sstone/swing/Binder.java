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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import org.edc.sstone.beans.PropertyConverter;
import org.edc.sstone.swing.component.ETextField;
import org.edc.sstone.util.BeanUtil;

/**
 * @author Greg Orlowski
 */
public class Binder {

    private JComponent component;
    private String componentProp;
    private boolean convertOnComponentSet = false;
    private List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

    @SuppressWarnings("rawtypes")
    private PropertyConverter propertyConverter;

    public static Binder bind(JComponent component) {
        return new Binder(component, null);
    }

    protected String inferComponentPropertyName() {
        if (component instanceof JTextComponent && !(component instanceof ETextField)) {
            return "text";
        }
        return "value";
    }

    public static Binder bind(JComponent component, String componentProp) {
        return new Binder(component, componentProp);
    }

    public Binder withChangeListeners(ChangeListener... listeners) {
        if (listeners.length > 0) {
            changeListeners.addAll(Arrays.asList(listeners));
        }
        return this;
    }

    public Binder withConverter(PropertyConverter<?> converter) {
        return withConverter(converter, false);
    }

    public Binder withConverter(PropertyConverter<?> converter, boolean convertOnComponentSet) {
        this.propertyConverter = converter;
        this.convertOnComponentSet = convertOnComponentSet;
        return this;
    }

    protected Binder(JComponent component, String componentProp) {
        this.component = component;
        this.componentProp = componentProp == null ? inferComponentPropertyName() : componentProp;
    }

    protected void setBeanProperty(Object bean, String beanProperty) {
        Object beanPropertyValue = convertPropertyToComponentValue(
                BeanUtil.getProperty(bean, beanProperty));
        BeanUtil.setProperty(component, componentProp, beanPropertyValue);
    }

    @SuppressWarnings("unchecked")
    protected Object convertPropertyToComponentValue(Object beanPropertyValue) {
        if (convertOnComponentSet && propertyConverter != null) {
            beanPropertyValue = propertyConverter.convertFromBeanProperty(beanPropertyValue);
        }
        return beanPropertyValue;
    }

    /*
     * TODO: refactor -- I could overload this rather than using instanceof. It may also be
     * desirable to provide alternative public toBean methods that explicitly specify a listener
     * event type
     */

    public void toBean(Object bean, String beanProperty) {
        setBeanProperty(bean, beanProperty);
        if (component instanceof JSpinner) {
            ((JSpinner) component).addChangeListener(componentChangeListener(bean, beanProperty));
        } else if (component instanceof AbstractButton) {
            ((AbstractButton) component).addActionListener(componentChangeListener(bean, beanProperty));
        } else if (component instanceof JSlider) {
            ((JSlider) component).addChangeListener(componentChangeListener(bean, beanProperty));
        } else if (component instanceof JTextComponent && !(component instanceof ETextField)) {
            JTextComponent textComponent = ((JTextComponent) component);
            BeanDocumentListener l = new BeanDocumentListener(textComponent, bean, beanProperty);
            l.setModelUpdateListeners(changeListeners);
            textComponent.getDocument().addDocumentListener(l);
        } else {
            component.addPropertyChangeListener(componentProp, componentChangeListener(bean, beanProperty));
        }
    }

    protected ComponentChangeListener componentChangeListener(Object bean, String beanProperty) {
        ComponentChangeListener listener = new ComponentChangeListener(bean, beanProperty, componentProp);
        listener.setModelUpdateListeners(changeListeners);
        listener.setPropertyConverter(this.propertyConverter);
        return listener;
    }

}
