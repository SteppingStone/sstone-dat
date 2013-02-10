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
import javax.swing.JSlider;

import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.project.ResourceType;
import org.edc.sstone.record.reader.model.ScreenRecord;
import org.edc.sstone.record.writer.model.ScreenRecordWriter;
import org.edc.sstone.swing.Binder;
import org.edc.sstone.swing.component.FloatSlider;
import org.edc.sstone.swing.event.ComponentEnabledListener;

/**
 * @author Greg Orlowski
 */
public class AudioScreenEditorForm<R extends ScreenRecordWriter<?>>
        extends ScreenEditorForm<R> {

    private static final long serialVersionUID = -828687868658385621L;
    FloatSlider autoAdvanceDelaySlider = null;

    public AudioScreenEditorForm(R recordWriter) {
        super(recordWriter);
    }

    protected void addFormComponents() {
        super.addFormComponents();

        ZipResourceSelectionPanel resourceChooser = new ZipResourceSelectionPanel(recordWriter,
                "resourcePath", ResourceType.AudioTrack);
        addToComponentForm("audioScreenEditorForm.label.audioFile", resourceChooser);

        JCheckBox autoAdvanceToggle = new JCheckBox(null, null);
        autoAdvanceDelaySlider = new FloatSlider(JSlider.HORIZONTAL, 0, 8,
                ScreenRecord.ADVANCE_DELAY_SECOND_INTERVAL);

        ComponentEnabledListener selectToggledListener = new ComponentEnabledListener(autoAdvanceToggle,
                "selected",
                autoAdvanceDelaySlider);

        DATUtil.bind(autoAdvanceToggle, "selected")
                .withChangeListeners(selectToggledListener)
                .toBean(recordWriter, "autoAdvance");
        addToComponentForm("audioScreenEditorForm.label.autoAdvance", autoAdvanceToggle);

        DATUtil.bind(autoAdvanceDelaySlider, "floatValue")
                .toBean(recordWriter, "autoAdvanceDelaySeconds");
        addToComponentForm("audioScreenEditorForm.label.autoAdvanceDelay", autoAdvanceDelaySlider, false);

        // Fire manually to set the initial (enabled/disabled) state of the slider
        selectToggledListener.stateChanged(null);
    }

}
