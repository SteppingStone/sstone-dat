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

import javax.swing.JTextField;

import org.edc.sstone.beans.NullStringConverter;
import org.edc.sstone.dat.event.ComponentModelChangeListener;
import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.record.writer.model.TitledComponentRecordWriter;

/**
 * @author Greg Orlowski
 */
public abstract class TitledComponentEditorPanel<R extends TitledComponentRecordWriter<?>> extends
        ComponentForm<R> {

    private static final long serialVersionUID = 8188756152968874030L;

    public TitledComponentEditorPanel(R recordWriter) {
        super(recordWriter);
    }

    public TitledComponentEditorPanel(String tabLabelKey, R recordWriter) {
        super(tabLabelKey, recordWriter);
    }

    protected void addFormComponents() {
        JTextField titleField = new JTextField(getTextFieldWidth(recordWriter.getTitle()));
        DATUtil.bind(titleField)
                .withChangeListeners(new ComponentModelChangeListener())
                .withConverter(new NullStringConverter())
                .toBean(recordWriter, "title");

        addToComponentForm(getTitleLabelKey(), titleField);
    }

    protected String getTitleLabelKey() {
        return "titleComponentForm.title";
    }

    // public static class TitleBinding {
    // protected final String labelKey;
    // protected final String beanProperty;
    //
    // public TitleBinding(String labelKey, String beanProperty) {
    // this.labelKey = labelKey;
    // this.beanProperty = beanProperty;
    // }
    // }

}
