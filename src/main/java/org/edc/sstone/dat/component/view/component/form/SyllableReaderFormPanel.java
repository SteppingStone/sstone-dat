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

import org.edc.sstone.dat.ResourceConstants;
import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.dat.util.RegexFormat;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.record.writer.model.TextAreaComponentRecordWriter;
import org.edc.sstone.swing.Binder;
import org.edc.sstone.swing.component.ETextField;

/**
 * @author Greg Orlowski
 */
public class SyllableReaderFormPanel<R extends TextAreaComponentRecordWriter<?>> extends TextAreaFormPanel<R> {

    private static final long serialVersionUID = -3322322194736172019L;

    public SyllableReaderFormPanel(R recordWriter) {
        super(recordWriter);
    }

    @Override
    protected void addFormComponents() {
        super.addFormComponents();

        ETextField<String> tf = new ETextField<String>(new RegexFormat("^[-|:_]{1}$"));
        tf.setCommitsOnValidEdit(true);
        tf.setValidateWithFormatter(true);
        tf.indicateValidInput(tf.getBackground(), SAFUtil.getColor(ResourceConstants.FORM_ERROR_BGCOLOR));
        tf.setColumns(1);

        DATUtil.bind(tf).toBean(recordWriter, "syllableSeparator");
        addToComponentForm("textAreaForm.label.syllableSep", tf, false);
    }

    @Override
    protected boolean supportReadNonLetters() {
        return false;
    }

}
