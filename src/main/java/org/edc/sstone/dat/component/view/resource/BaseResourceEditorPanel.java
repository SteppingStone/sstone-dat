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

import static org.edc.sstone.dat.util.SAFUtil.button;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.edc.sstone.dat.DesktopAuthoringToolApp;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.project.Project;
import org.edc.sstone.project.ResourceType;

/**
 * @author Greg Orlowski
 */
public abstract class BaseResourceEditorPanel extends JPanel {

    private static final long serialVersionUID = 2897169243709782765L;

    protected final ResourceType resourceType;

    protected JButton addResourcesButton;
    protected JButton removeResourcesButton;

    final JToolBar resourceToolBar;

    /*
     * I am explicitly declaring action listeners rather than using SAF annotations b/c BSAF was not
     * processing actions defined in annotated super class methods.
     */
    protected final ActionListener addResourcesAction = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            addResources();
        }
    };

    protected final ActionListener removeResourcesAction = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            removeResources();
        }
    };

    public BaseResourceEditorPanel(final ResourceType resourceType) {
        this.resourceType = resourceType;
        SAFUtil.setDefaultName(this);
        setLayout(new BorderLayout());

        resourceToolBar = new JToolBar();
        resourceToolBar.setLayout(new BoxLayout(resourceToolBar, BoxLayout.X_AXIS));
        resourceToolBar.setBorder(BorderFactory.createEtchedBorder());

        addResourcesButton = button("resourceEditorPanel.addResourcesButton", addResourcesAction);
        removeResourcesButton = button("resourceEditorPanel.removeResourcesButton", removeResourcesAction);
        removeResourcesButton.setEnabled(false);

        resourceToolBar.add(addResourcesButton);
        resourceToolBar.add(removeResourcesButton);

        add(resourceToolBar, BorderLayout.SOUTH);
        SAFUtil.injectComponents(this);
    }

    protected void setMainComponent(JComponent component) {
        add(component, BorderLayout.CENTER);
    }

    protected abstract void removeResources();

    protected abstract void addResources();

    protected Project getProject() {
        return DesktopAuthoringToolApp.getInstance().getProject();
    }

}
