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

import static org.edc.sstone.dat.util.SAFUtil.button;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.bushe.swing.event.EventBus;
import org.edc.sstone.dat.DesktopAuthoringToolApp;
import org.edc.sstone.dat.event.ApplicationModeChangeEvent;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.project.Project;
import org.jdesktop.application.Action;

/**
 * @author Greg Orlowski
 */
public class MainToolbar extends JToolBar {

    private static final long serialVersionUID = -911891350618377212L;

    JButton componentViewSelectionButton;
    JButton resourceViewSelectionButton;
    JButton configurationViewSelectionButton;

    public MainToolbar() {
        super();
        setName("mainToolbar");
        setRollover(true);
        setFloatable(false);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        // setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setBorder(BorderFactory.createEtchedBorder());

        initButtons();
    }

    @Action
    public void componentView() {
        switchMode(ApplicationMode.Components);
    }

    @Action
    public void resourceView() {
        switchMode(ApplicationMode.Resources);
    }

    @Action
    public void configurationView() {
        switchMode(ApplicationMode.Configuration);
    }

    /**
     * @return true if a {@link Project} has been initialized, otherwise false.
     */
    private boolean isProject() {
        return DesktopAuthoringToolApp.getInstance().getProject() != null;
    }

    protected void switchMode(ApplicationMode mode) {
        if (isProject()) {
            EventBus.publish(new ApplicationModeChangeEvent(mode));
        }
    }

    public void initButtons() {
        componentViewSelectionButton = button("componentViewSelectionButton", SAFUtil.action(this, "componentView"));
        resourceViewSelectionButton = button("resourceViewSelectionButton", SAFUtil.action(this, "resourceView"));
        configurationViewSelectionButton = button("configurationViewSelectionButton",
                SAFUtil.action(this, "configurationView"));

        // we need to getActionMap(obj) to use actions defined in a given object.
        // if we call getActionMap() with no arguments, it gets the application action map
        // resourceViewSelectionButton.setAction(getContext().getActionMap(this).get("selectResourceView"));

        // getContext().getResourceMap().injectComponents(resourceModeButton);

        add(componentViewSelectionButton);
        add(resourceViewSelectionButton);
        add(configurationViewSelectionButton);
    }

}
