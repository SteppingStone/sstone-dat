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

import org.edc.sstone.project.ResourceType;
import org.edc.sstone.record.writer.model.ResourceComponentRecordWriter;

/**
 * @author Greg Orlowski
 */
public class ResourceComponentForm<R extends ResourceComponentRecordWriter<?>> extends ComponentForm<R> {

    private static final long serialVersionUID = 6373066891051726803L;

    private ZipResourceSelectionPanel resourceChooser;

    public ResourceComponentForm(R recordWriter, final ResourceType resourceType) {
        super(recordWriter);
        /*
         * NOTE: we set the resourceType of the chooser after the super ctor b/c it already gets
         * added to the form (which means its ctor is called) in this class' super ctor. It is an
         * ugly workaround necessary because of my stubborn insistence that all component
         * initialization is done for these record editor forms in the ctors for convenience.
         */
        resourceChooser.resourceType = resourceType;
    }

    @Override
    protected void addFormComponents() {
        resourceChooser = new ZipResourceSelectionPanel(recordWriter, "file", null, true);
        addToComponentForm("resourceComponentForm.label.file", resourceChooser);
    }

}
