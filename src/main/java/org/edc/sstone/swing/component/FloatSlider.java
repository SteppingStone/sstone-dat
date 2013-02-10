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

import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * @author Greg Orlowski
 */
public class FloatSlider extends JSlider {

    private static final long serialVersionUID = -2032325340060382859L;

    protected final int integerDivisions;

    public FloatSlider(int orientation, int min, int max, int integerDivisions) {
        super(orientation);
        setMinimum(min * integerDivisions);
        setMaximum(max * integerDivisions);

        this.integerDivisions = integerDivisions;
        setMajorTickSpacing(integerDivisions);
        setMinorTickSpacing(1);
        setPaintLabels(true);
        setPaintTicks(true);
        setSnapToTicks(true);

        // only print whole numbers
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        for (int i = min; i <= max; i++) {
            labelTable.put(new Integer(i * integerDivisions), new JLabel(Integer.toString(i)));
        }
        this.setLabelTable(labelTable);
    }

    public float getFloatValue() {
        float value = (float) getValue();
        float integerValue = (float) integerDivisions;
        return value / integerValue;
    }

    public void setFloatValue(float value) {
        int integerValue = (int) (value * integerDivisions);
        setValue(integerValue);
    }
}
