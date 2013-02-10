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

import org.edc.sstone.record.writer.model.ScreenRecordWriter;

/**
 * @author Greg Orlowski
 */
public class ScreenEditorForm<R extends ScreenRecordWriter<?>> extends TitledComponentEditorPanel<R> {

    private static final long serialVersionUID = -828687868658385621L;

    public ScreenEditorForm(R recordWriter) {
        super(recordWriter);
    }

    protected String getTitleLabelKey() {
        return "screenEditorForm.title";
    }

}
