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
package org.edc.sstone.dat.component.view.component;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.edc.sstone.dat.component.view.component.event.OpenComponentEditorEvent;
import org.edc.sstone.dat.component.view.component.form.ComponentEditorPanel;
import org.edc.sstone.dat.component.view.component.form.ComponentFormFactory;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.microemu.EmulatorConfigurationPanel;

/**
 * @author Greg Orlowski
 */
public class ComponentViewPanel extends JSplitPane {

    private static final long serialVersionUID = -6607979686782019589L;

    private ComponentTreeEditorPanel componentTreePanel;
    private JSplitPane horizontalSplitPane;
    private ComponentFormFactory componentFormFactory = new ComponentFormFactory();

    public ComponentViewPanel() {
        super();

        SAFUtil.setDefaultName(this);

        setLayout(new BorderLayout());
        AnnotationProcessor.process(this);

        initContents();
    }

    @EventSubscriber(eventClass = OpenComponentEditorEvent.class)
    public void onEvent(OpenComponentEditorEvent event) {
        setComponentEditor(componentFormFactory.getComponentEditorPanel(event.getComponentWriter()));
    }

    protected void setComponentEditor(ComponentEditorPanel editorPanel) {
        horizontalSplitPane.setRightComponent(editorPanel);
    }

    private void initContents() {
        componentTreePanel = new ComponentTreeEditorPanel();
        horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        horizontalSplitPane.setLeftComponent(componentTreePanel);

        // Put an empty JPanel in the right pane as a placeholder
        horizontalSplitPane.setRightComponent(new JPanel());

        add(horizontalSplitPane, BorderLayout.CENTER);

        JTabbedPane bottomPanel = new JTabbedPane();

        bottomPanel.addTab(SAFUtil.getString("componentPaletteTab.label.text"),
                new ComponentPalette());
        bottomPanel.addTab(SAFUtil.getString("emulatorConfigurationTab.label.text"),
                new EmulatorConfigurationPanel());

        // This works... set the preferred size to 2 or 3 rows of icons + enough
        // room for the tabs... actually, do not set preferred. Fill the container,
        // set a minimum, and then set a max
        bottomPanel.setMinimumSize(new Dimension(10000, 24 * 4));
        // bottomPanel.setPreferredSize(new Dimension(10000, 24 * 4));
        bottomPanel.setMaximumSize(new Dimension(10000, 24 * 8));

        // setBottomComponent(bottomPanel);
        add(bottomPanel, BorderLayout.SOUTH);

    }

}
