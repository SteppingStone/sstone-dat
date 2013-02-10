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
import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.record.writer.model.MenuItemRecordWriter;

/**
 * @author Greg Orlowski
 */
public class MenuItemComponentEditorPanel<R extends MenuItemRecordWriter<?>> extends TitledComponentEditorPanel<R> {

    private static final long serialVersionUID = 5538814596488872649L;

    public MenuItemComponentEditorPanel(R recordWriter) {
        super("componentForm.tabLabel.MenuItemComponent", recordWriter);
    }
    
    protected String getTitleLabelKey() {
        return "menuItemComponentForm.menuField";
    }
    
    protected void addFormComponents() {
        super.addFormComponents();
        
        JTextField branchTitleField = new JTextField(getTextFieldWidth(recordWriter.getTitle()));
        DATUtil.bind(branchTitleField)
                .withConverter(new NullStringConverter())
                .toBean(recordWriter, "branchTitle");
        
        addToComponentForm("menuItemComponentForm.branchTitleField", branchTitleField);
    }

}
