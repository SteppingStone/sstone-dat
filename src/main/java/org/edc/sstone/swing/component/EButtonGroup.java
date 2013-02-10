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
package org.edc.sstone.swing.component;

import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;

/**
 * @author Greg Orlowski
 */
public class EButtonGroup<T> extends ButtonGroup implements DataModel<T> {

    private static final long serialVersionUID = -6290566898274518824L;

    @Override
    @SuppressWarnings("unchecked")
    public T getValue() {
        ButtonModel bm = getSelection();
        if (bm != null && bm instanceof DataModel) {
            return ((DataModel<T>) bm).getValue();
        }
        return null;
    }

    public void setEnabled(boolean enabled) {
        for (Enumeration<AbstractButton> e = getElements(); e.hasMoreElements();) {
            e.nextElement().setEnabled(enabled);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(T value) {
        boolean wasSet = false;
        for (Enumeration<AbstractButton> e = getElements(); e.hasMoreElements();) {
            AbstractButton b = e.nextElement();
            if (b instanceof DataModel) {
                T buttonValue = ((DataModel<T>) b).getValue();
                if (buttonValue == value || (buttonValue != null && buttonValue.equals(value))) {
                    if (b.getModel() instanceof DefaultButtonModel) {
                        setSelected(((DefaultButtonModel) b.getModel()), true);
                        wasSet = true;
                        break;
                    }
                }
            }
        }

        if (!wasSet) {
            clearSelection();
        }
    }

}
