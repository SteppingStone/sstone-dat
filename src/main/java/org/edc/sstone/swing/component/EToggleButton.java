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

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JToggleButton;

/**
 * @author Greg Orlowski
 */
public class EToggleButton<T> extends JToggleButton implements DataModel<T> {

    private static final long serialVersionUID = 4418680979689722214L;

    public EToggleButton(boolean canUntoggleInGroup) {
        this(null, null, canUntoggleInGroup);
    }

    public EToggleButton(Icon icon, String name, boolean canUntoggleInGroup) {
        super(icon);
        setName(name);
        setModel(new EToggleButtonModel<T>(canUntoggleInGroup));
    }

    public EToggleButton(Icon icon) {
        this(icon, null, false);
    }

    public EToggleButton(String name, Icon icon) {
        this(icon, name, false);
    }
    
    public EToggleButton(String name) {
        this(null, name, false);
    }

    @SuppressWarnings("unchecked")
    public void setValue(T modelObject) {
        ((EToggleButtonModel<T>) getModel()).setValue(modelObject);
    }

    @SuppressWarnings("unchecked")
    public T getValue() {
        return ((EToggleButtonModel<T>) getModel()).getValue();
    }

    public static class EToggleButtonModel<T> extends JToggleButton.ToggleButtonModel implements DataModel<T> {

        private static final long serialVersionUID = 2383112177730956132L;

        protected boolean canUntoggleInGroup = false;
        protected T modelObject = null;

        public EToggleButtonModel(boolean canUntoggleInGroup) {
            super();
            this.canUntoggleInGroup = canUntoggleInGroup;
        }

        @Override
        public void setSelected(boolean b) {
            super.setSelected(b);
            if (!b && isSelected()) {
                ButtonGroup group = getGroup();
                if (group != null)
                    group.clearSelection();
            }
        }

        public T getValue() {
            return modelObject;
        }

        public void setValue(T modelObject) {
            this.modelObject = modelObject;
        }
    }

}
