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

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.edc.sstone.beans.PropertyConverter;
import org.edc.sstone.util.BeanUtil;

/**
 * @author Greg Orlowski
 */
public abstract class BasePropertyChangeListener {

    protected volatile Object sourceObject;
    protected final String beanPropertyName;
    final protected Object boundBean;
    protected boolean allowNull = false;
    protected List<ChangeListener> modelUpdateListeners;

    protected PropertyConverter<?> propConverter;

    // cache the setter method
    protected volatile Method beanSetterMethod;

    protected boolean cacheSetterMethod = false;

    protected void fireModelUpdateListeners() {
        if (modelUpdateListeners != null && !modelUpdateListeners.isEmpty()) {
            ChangeEvent event = new ChangeEvent(boundBean);
            for (ChangeListener l : modelUpdateListeners) {
                l.stateChanged(event);
            }
        }
    }

    // protected BasePropertyChangeListener(Object boundBean) {
    // this.boundBean = boundBean;
    // }

    protected BasePropertyChangeListener(Object boundBean, String beanPropertyName) {
        this.beanPropertyName = beanPropertyName;
        this.boundBean = boundBean;

        if (!beanPropertyName.contains(".") && !beanPropertyName.endsWith("]")) {
            cacheSetterMethod = true;
        }
    }

    protected boolean areEqual(Object o1, Object o2) {
        if (o1 == o2)
            return true;
        if (o1 != null)
            return o1.equals(o2);
        return o2.equals(o1);
    }

    /**
     * Convert an object from a source type to a destination type
     * 
     * @param input
     * @return
     */
    protected Object convert(Object input) {
        if (propConverter != null) {
            Object converted = propConverter.convertToBeanProperty(input);
            return converted;
        }
        return input;
    }

    protected void updateBean(Object newValue) {
        if (allowNull || newValue != null) {
            Object convertedValue = convert(newValue);
            try {
                if (cacheSetterMethod) {
                    getBeanSetterMethod(convertedValue.getClass()).invoke(boundBean, convertedValue);
                } else {
                    String beanProp = getBeanPropertyName();
                    BeanUtil.setProperty(boundBean, beanProp, convertedValue);
                }
                fireModelUpdateListeners();
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @param eventSourceObject
     *            The object that triggered the event
     * @param parameterClass
     *            the class of the first (and only) setter parameter. If the setter takes a
     *            primitive, this should be the boxed class of the primitive.
     * @return the setter method
     */
    protected Method determineSetterMethod(Class<?> parameterClass) {
        String beanPropName = getBeanPropertyName();
        Method ret = null;
        try {
            ret = BeanUtil.determineSetterMethod(boundBean, beanPropName, parameterClass);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        if (ret == null) {
            throw new IllegalArgumentException("Bean has no writable property named: " + beanPropName);
        }
        return ret;
    }

    protected String getBeanPropertyName() {
        return beanPropertyName;

        // if (beanPropertyName != null) {
        // return beanPropertyName;
        // }
        // if (sourceComponent != null && sourceComponent instanceof Component) {
        // String[] componentNameParts = ((Component) sourceComponent).getName().split(".");
        // this.beanPropertyName = componentNameParts[componentNameParts.length - 1];
        // return beanPropertyName;
        // }
        // throw new
        // IllegalArgumentException("If propertyName is null then the sourceComponent.name must not be null");
    }

    protected Method getBeanSetterMethod(Class<?> parameterClass) {
        if (beanSetterMethod == null) {
            synchronized (this) {
                if (beanSetterMethod == null)
                    beanSetterMethod = determineSetterMethod(parameterClass);
            }
        }
        return beanSetterMethod;
    }

    /**
     * @return the object that generated the event
     */
    protected Object getSourceObject() {
        return sourceObject;
    }

    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }

    public void setPropertyConverter(PropertyConverter<?> propConverter) {
        this.propConverter = propConverter;
    }

    public void setModelUpdateListeners(List<ChangeListener> modelUpdateListeners) {
        this.modelUpdateListeners = modelUpdateListeners;
    }

}
