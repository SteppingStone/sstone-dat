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

import java.awt.Color;
import java.awt.Insets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * @author Greg Orlowski
 */
public class ESpinner extends JSpinner {

    private static final long serialVersionUID = -780225873021342844L;

    public ESpinner(String name, List<?> choices, Object defaultValue) {
        super(listModel(choices, defaultValue));
        setName(name);
        configure();
    }

    public ESpinner(List<?> choices, Object defaultValue) {
        this(null, choices, defaultValue);
    }

    static SpinnerListModel listModel(List<?> choices, Object defaultValue) {
        Collections.reverse(choices);
        SpinnerListModel spinnerModel = new SpinnerListModel(choices);
        spinnerModel.setValue(defaultValue);
        return spinnerModel;
    }

    protected void configure() {

        JTextField tf = ((JSpinner.DefaultEditor) getEditor()).getTextField();

        SpinnerModel model = getModel();
        Object o = null;
        int maxLen = 0;
        if (model instanceof SpinnerListModel) {
            List<?> l = ((SpinnerListModel) model).getList();
            for (Iterator<?> e = l.iterator(); e.hasNext();) {
                o = e.next();
                if (o != null)
                    maxLen = Math.max(o.toString().length(), maxLen);
            }
        }
        // else {
        // while ((o = model.getNextValue()) != null)
        // maxLen = Math.max(o.toString().length(), maxLen);
        // }

        // FontMetrics fm = tf.getFontMetrics(tf.getFont());
        // int width = fm.stringWidth(FontFace.PROPORTIONAL.toString());

        tf.setEditable(false);
        tf.setBackground(Color.WHITE);
        tf.setColumns(maxLen);

        tf.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void setPadding(Insets insets) {
        JTextField tf = ((JSpinner.DefaultEditor) getEditor()).getTextField();
        tf.setBorder(new EmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
    }

}
