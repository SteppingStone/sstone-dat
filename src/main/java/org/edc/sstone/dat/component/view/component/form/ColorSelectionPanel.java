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

import static org.edc.sstone.dat.util.DATUtil.useMonospaceFont;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JTextField;

import org.edc.sstone.dat.ResourceConstants;
import org.edc.sstone.dat.util.ColorNumberFormat;
import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.dat.util.SwingUtil;
import org.edc.sstone.record.writer.model.RecordWriter;
import org.edc.sstone.swing.ComponentChangeListener;
import org.edc.sstone.swing.component.ETextField;
import org.edc.sstone.util.BeanUtil;
import org.edc.sstone.util.StringUtil;
import org.jdesktop.application.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Orlowski
 */
public class ColorSelectionPanel extends Box {

    private static final long serialVersionUID = -1625675428971881608L;

    private static final Logger logger = LoggerFactory.getLogger(ColorSelectionPanel.class);

    protected JButton openColorChooserButton;
    protected ETextField<Integer> colorField;
    protected JTextField colorBox;

    protected final Object bean;
    protected final String beanProperty;

    public ColorSelectionPanel(Object bean, String beanProperty) {
        super(BoxLayout.X_AXIS);

        this.bean = bean;
        this.beanProperty = beanProperty;

        initComponents();
        initLayout();
        packComponents();

        SAFUtil.injectComponents(this);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        SwingUtil.enableComponents(enabled,
                openColorChooserButton,
                colorField,
                colorBox);
    };

    protected String getEmptyColorBoxString() {
        return StringUtil.repeat(" ", 8);
    }

    protected void initColorField() {
        ETextField<Integer> colorField = new ETextField<Integer>(ColorNumberFormat.getColorFormatInstance(), 6);

        // colorField.setColumns(6);
        colorField.setColumns(7);

        Object colorIntObj = BeanUtil.getProperty(bean, beanProperty);
        if (colorIntObj instanceof Integer) {
            colorField.setValue((Integer) colorIntObj);
        }

        colorField.setValidateWithFormatter(true);
        colorField.indicateValidInput(colorField.getBackground(),
                SAFUtil.getColor(ResourceConstants.FORM_ERROR_BGCOLOR));
        colorField.setCommitsOnValidEdit(true);

        useMonospaceFont(colorField);

        ComponentChangeListener changeListener = new ComponentChangeListener(bean, beanProperty);
        changeListener.setAllowNull(false);
        colorField.addPropertyChangeListener("value", changeListener);

        this.colorField = colorField;
    }

    protected void initLayout() {
    }

    protected void initComponents() {
        // TODO: remove unnecessary color chooser tabs
        initColorField();

        openColorChooserButton = new JButton();
        openColorChooserButton.setName("openColorChooser.button");
        openColorChooserButton.setAction(SAFUtil.action(this, "openColorChooser"));

        // colorBox = new JTextPane();
        String initialString = getEmptyColorBoxString();
        colorBox = new JTextField(initialString);
        useMonospaceFont(colorBox);

        Dimension d = new Dimension(getFontMetrics(colorBox.getFont()).charsWidth(
                initialString.toCharArray(), 0, initialString.length()), 0);
        colorBox.setMinimumSize(d);
        colorBox.setPreferredSize(d);

        colorBox.setEditable(false);
        colorBox.setHorizontalAlignment(JTextField.CENTER);

        colorBox.setText(getEmptyColorBoxString());
        colorBox.setBackground(getColor(colorField.getValue()));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        colorBox.setVisible(!RecordWriter.isNull(colorField.getValue()));

        colorField.addPropertyChangeListener("value", new ColorFieldPropertyChangeListener());
    }

    private class ColorFieldPropertyChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            int colorFieldValue = colorField.getValue();
            boolean visible = !RecordWriter.isNull(colorFieldValue);
            colorBox.setVisible(visible);
            /*
             * call revalidate() to workaround this JDK bug:
             * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4403550
             */
            if (visible) {
                colorBox.setBackground(getColor(colorFieldValue));
                ColorSelectionPanel.this.revalidate();
            }
            DATUtil.markDirty();
        }

    }

    protected Color getColor(int value) {
        return RecordWriter.isNull(value) ? Color.WHITE : new Color(value);
    }

    @Action
    public void openColorChooser() {
        Color c = JColorChooser.showDialog(this, "Color Chooser", getColor(colorField.getValue()));
        if (c != null) {
            int colorValue = c.getRGB() & 0xFFFFFF; // we need to remove the alpha channel
            colorField.setValue(Integer.valueOf(colorValue));
        }
    }

    protected void packComponents() {
        add(colorField);
        add(Box.createHorizontalStrut(colorField.getFont().getSize()));
        add(openColorChooserButton);
        add(Box.createHorizontalStrut(colorField.getFont().getSize()));
        add(colorBox);
    }

}
