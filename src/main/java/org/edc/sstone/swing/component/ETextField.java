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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.Format;
import java.text.ParseException;
import java.util.regex.Pattern;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;

import org.edc.sstone.dat.util.ValidatingFormat;
import org.edc.sstone.swing.event.DocumentChangeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Orlowski
 */
public class ETextField<T> extends JTextField {

    private static final Logger logger = LoggerFactory.getLogger(ETextField.class);

    private static final long serialVersionUID = -3602051159825900572L;
    private final Format format;
    private Pattern constraintPattern = null;
    private boolean commitOnValidEdit = false;
    private boolean validateWithFormatter = false;

    private Color invalidFormatColor;
    private Color validFormatColor;
    private EditListener editListener;

    private boolean documentListenersEnabled = true;

    protected T value;

    public ETextField(Format format) {
        this(format, null, 0);
    }

    public ETextField(Format format, int maxWidth) {
        this(format, new EDocument(maxWidth), maxWidth);
    }

    protected ETextField(Format format, Document doc, int maxWidth) {
        super(doc, null, maxWidth);
        this.format = format;
        addFocusListener(new FocusHandler());

        this.editListener = new EditListener(this);
        getDocument().addDocumentListener(editListener);
    }

    public ETextField<T> setConstraintPattern(String constraintPattern) {
        this.constraintPattern = Pattern.compile(constraintPattern);
        return this;
    }

    public ETextField<T> setCommitsOnValidEdit(boolean commit) {
        this.commitOnValidEdit = commit;
        return this;
    }

    protected boolean isTextValid() {
        if (validateWithFormatter && (format instanceof ValidatingFormat)) {
            return ((ValidatingFormat) format).isValid(getText());
        }
        if (constraintPattern == null) {
            return true;
        }
        return constraintPattern.matcher(getText()).matches();
    }

    @SuppressWarnings("unchecked")
    protected void updateValueFromText() {

        Object valueObj = null;
        String text = null;
        try {
            text = getText();
            valueObj = format.parseObject(text);
            // logger.debug("updateValueFromText... text: " + text + "; valueObj: " + valueObj);

            if (valueObj != null) {
                setValue((T) valueObj, false);
            }
        } catch (ParseException e) {
            logger.debug("ParseException: " + e.getMessage());
        }
    }

    protected void setValue(T value, boolean updateText) {
        Object oldValue = this.value;
        Object newValue = value;
        if (updateText) {
            try {
                documentListenersEnabled = false;
                setText(format.format(value));
            } finally {
                documentListenersEnabled = true;
            }
        }
        this.value = value;
        firePropertyChange("value", oldValue, newValue);
    }

    public void setValue(T value) {
        setValue(value, true);
    }

    public T getValue() {
        return this.value;
    }

    protected class FocusHandler extends FocusAdapter {
        @Override
        public void focusLost(FocusEvent e) {
            if (!isTextValid()) {
                setText(ETextField.this.format.format(getValue()));
            }
        }
    }

    public void indicateValidInput(Color validInputBackground, Color invalidInputBackground) {
        this.invalidFormatColor = invalidInputBackground;
        this.validFormatColor = validInputBackground;

    }

    protected static class EditListener extends DocumentChangeAdapter {

        private final ETextField<?> textField;

        EditListener(ETextField<?> textField) {
            this.textField = textField;
        }

        @Override
        public void documentUpdated(DocumentEvent e) {
            if (textField.documentListenersEnabled) {
                if (!textField.isTextValid()) {
                    if (textField.invalidFormatColor != null) {
                        textField.setBackground(textField.invalidFormatColor);
                    }
                } else {
                    if (textField.validFormatColor != null) {
                        textField.setBackground(textField.validFormatColor);
                    }
                    if (textField.commitOnValidEdit) {
                        textField.updateValueFromText();
                    }
                }
            }
        }
    }

    public void setValidateWithFormatter(boolean validateWithFormatter) {
        this.validateWithFormatter = validateWithFormatter;
    }
}
