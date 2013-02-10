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
package org.edc.sstone.dat.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.edc.sstone.dat.DesktopAuthoringToolApp;
import org.edc.sstone.dat.component.view.component.ComponentViewPanel;
import org.edc.sstone.dat.component.view.config.ProjectConfigurationPanel;
import org.edc.sstone.dat.component.view.resource.ResourceViewPanel;
import org.edc.sstone.dat.event.ApplicationModeChangeEvent;
import org.edc.sstone.dat.event.SetProjectEvent;
import org.edc.sstone.project.Project;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.SingleFrameApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Orlowski
 */
public class MainWindow extends FrameView {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

    private JPanel wrapper = new JPanel();
    private ComponentViewPanel componentViewPanel;
    private ResourceViewPanel resourceViewPanel;

    private Map<ApplicationMode, JComponent> modeComponentMap = new HashMap<ApplicationMode, JComponent>();

    public MainWindow(SingleFrameApplication app) {
        super(app);

        setMenuBar(new MainMenuBar());
        setToolBar(new MainToolbar());

        componentViewPanel = new ComponentViewPanel();
        resourceViewPanel = new ResourceViewPanel();

        modeComponentMap.put(ApplicationMode.Components, componentViewPanel);
        modeComponentMap.put(ApplicationMode.Resources, resourceViewPanel);

        wrapper.setLayout(new BorderLayout());
        wrapper.add(componentViewPanel, BorderLayout.CENTER);
        setComponent(wrapper);

        AnnotationProcessor.process(this);
    }

    protected Project getProject() {
        return DesktopAuthoringToolApp.getInstance().getProject();
    }

    @EventSubscriber(eventClass = SetProjectEvent.class)
    public void onEvent(SetProjectEvent event) {
        Component activeComponent = getActiveComponent();
        if ((activeComponent instanceof ProjectConfigurationPanel)
                && ((ProjectConfigurationPanel) activeComponent).getProject() != event.project) {
            setActiveComponent(new ProjectConfigurationPanel(event.project));
        }
    }

    @EventSubscriber(eventClass = ApplicationModeChangeEvent.class)
    public void onEvent(ApplicationModeChangeEvent event) {
        JComponent component = null;
        // We cannot use the same instance of ProjectConfigurationPanel
        // in a map b/c it should reference a project, and that state
        // can change if you, e.g., open a new project file. So we
        // need to instantiate a new one every time.
        if (event.mode == ApplicationMode.Configuration) {
            component = new ProjectConfigurationPanel(getProject());
        } else {
            component = modeComponentMap.get(event.mode);
        }

        if (component != null && component != getActiveComponent()) {
            setActiveComponent(component);
            LoggerFactory.getLogger(MainWindow.class).debug("switched app mode: " + event.mode);
        }
    }

    protected void setActiveComponent(JComponent component) {
        wrapper.remove(0);
        wrapper.add(component);
        wrapper.updateUI();
    }

    protected Component getActiveComponent() {
        return wrapper.getComponents()[0];
    }
}
