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

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.edc.sstone.dat.event.ComponentModelChangeListener;
import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.record.writer.model.TextAreaComponentRecordWriter;
import org.edc.sstone.swing.Binder;

/**
 * @author Greg Orlowski
 */
public class TextAreaFormPanel<R extends TextAreaComponentRecordWriter<?>> extends ComponentForm<R> {

    private static final long serialVersionUID = 8127144523527296632L;

    public TextAreaFormPanel(R recordWriter) {
        super(recordWriter);
    }

    protected void addFormComponents() {
        JTextArea textInput = new JTextArea(getTextFieldWidth(recordWriter.getText()), 40);
        JScrollPane textInputScrollPane = new JScrollPane(textInput);
        DATUtil.bind(textInput)
                .withChangeListeners(new ComponentModelChangeListener())
                .toBean(recordWriter, "text");

        addToComponentForm("textAreaForm.label.text", textInputScrollPane);

        if (recordWriter.isReader()) {
            JCheckBox audioEnabledToggle = new JCheckBox(null, null);
            DATUtil.bind(audioEnabledToggle, "selected")
                    .toBean(recordWriter, "audioEnabled");
            addToComponentForm("textAreaForm.label.audioEnabled", audioEnabledToggle);

            if (supportReadNonLetters()) {
                JCheckBox readNonLettersToggle = new JCheckBox(null, null);
                DATUtil.bind(readNonLettersToggle, "selected")
                        .toBean(recordWriter, "readNonLetters");
                addToComponentForm("textAreaForm.label.readNonLetters", readNonLettersToggle);
            }
        }
    }
    
    protected boolean supportReadNonLetters() {
        return true;
    }

}
