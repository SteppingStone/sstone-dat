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
package org.edc.sstone.dat.component.view.component.form;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.edc.sstone.beans.IntegerConverter;
import org.edc.sstone.beans.PropertyConverter;
import org.edc.sstone.dat.ResourceConstants;
import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.dat.util.IntegerNumberFormat;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.dat.util.SwingUtil;
import org.edc.sstone.swing.component.ETextField;

/**
 * @author Greg Orlowski
 */
public class SpacingEditorComponent extends JPanel {

    private static final long serialVersionUID = -7514087998383593926L;
    List<ETextField<Integer>> textFields = new ArrayList<ETextField<Integer>>(4);
    PropertyConverter<Integer> propConverter = new IntegerConverter();

    public SpacingEditorComponent(Object bean, String... beanProperties) {
        super();
        GridLayout layout = new GridLayout(2, 4);
        layout.setHgap(10);
        layout.setVgap(6);
        setLayout(layout);

        String[] labels = initLabels();
        for (int i = 0; i < labels.length; i++) {
            addField(bean, labels[i], beanProperties[i], i);
        }
    }

    private String[] initLabels() {
        String[] ret = new String[4];
        int i = 0;
        for (String k : new String[] { "top", "right", "bottom", "left" }) {
            ret[i++] = SAFUtil.getString("margin." + k + ".label.text");
        }
        return ret;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        SwingUtil.enableComponents(enabled, textFields);
    };

    protected void addField(Object bean, String label, String beanProp, int i) {
        add(new JLabel(label));
        ETextField<Integer> tf = new ETextField<Integer>(new IntegerNumberFormat(false, 0, 3), 3);
        tf.setCommitsOnValidEdit(true);
        tf.setValidateWithFormatter(true);

        tf.indicateValidInput(tf.getBackground(), SAFUtil.getColor(ResourceConstants.FORM_ERROR_BGCOLOR));
        DATUtil.bind(tf).withConverter(propConverter).toBean(bean, beanProp);

        textFields.add(i, tf);
        add(tf);
    }
}
