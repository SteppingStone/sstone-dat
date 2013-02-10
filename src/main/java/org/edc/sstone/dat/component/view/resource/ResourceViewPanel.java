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
package org.edc.sstone.dat.component.view.resource;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.project.ResourceType;

/**
 * @author Greg Orlowski
 */
public class ResourceViewPanel extends JSplitPane {

    private static final long serialVersionUID = -5029143290748223165L;

    private ResourceTypeSelectionPanel resourceTypeSelectionPanel;

    public ResourceViewPanel() {
        super(JSplitPane.HORIZONTAL_SPLIT);
        SAFUtil.setDefaultName(this);

        AnnotationProcessor.process(this);
        initContents();

    }

    @EventSubscriber(eventClass = OpenResourceEditorEvent.class)
    public void onEvent(OpenResourceEditorEvent event) {
        setRightComponent((event.resourceType == ResourceType.SyllableAudio)
                ? new SyllableResourceEditorPanel()
                : new ResourceEditorPanel(event.resourceType));
    }

    private void initContents() {
        resourceTypeSelectionPanel = new ResourceTypeSelectionPanel();
        setLeftComponent(new JScrollPane(resourceTypeSelectionPanel));

        getLeftComponent().setMinimumSize(SAFUtil.getDimension("resourceTypeSelectionPanel.minimumSize"));
        setRightComponent(new JPanel());
    }

}
