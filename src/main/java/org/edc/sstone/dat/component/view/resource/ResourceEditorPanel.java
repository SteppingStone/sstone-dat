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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.project.ResourceType;
import org.edc.sstone.swing.component.EFileChooser;
import org.edc.sstone.util.StringUtil;

/**
 * @author Greg Orlowski
 */
public class ResourceEditorPanel extends BaseResourceEditorPanel {

    private static final long serialVersionUID = -5366426914025287884L;

    final JList resourceList;

    protected ResourceEditorPanel(final ResourceType resourceType) {
        super(resourceType);

        resourceList = new JList(listModel());
        resourceList.setBorder(SAFUtil.getBorder("resourceEditorPanelList.border"));
        resourceList.setCellRenderer(new ResourceItemRenderer());
        resourceList.setLayoutOrientation(JList.VERTICAL_WRAP);
        resourceList.addListSelectionListener(new ResourceListSelectionListener());

        DATUtil.fixFont(resourceList, "List.font");

        setMainComponent(new JScrollPane(resourceList));

        SAFUtil.injectComponents(this);
    }

    protected void removeResources() {
        String[] selectedFilenames = StringUtil.toStringArray(resourceList.getSelectedValues());

        if (selectedFilenames.length > 0) {
            try {
                getProject().removeResources(resourceType, selectedFilenames);
                resourceList.setModel(listModel());
                resourceList.updateUI();
                DATUtil.markDirty();
            } catch (IOException e) {
                // TODO: better exception handling
                throw new RuntimeException(e);
            }
        }
    }

    protected void addResources() {
        JFileChooser resourceChooser = new EFileChooser();
        resourceChooser.setMultiSelectionEnabled(true);

        int returnVal = resourceChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                if (resourceChooser.getSelectedFiles().length > 0) {
                    getProject().addResources(resourceType, resourceChooser.getSelectedFiles());
                    resourceList.setModel(listModel());
                    resourceList.updateUI();
                    DATUtil.markDirty();
                }
            } catch (IOException e) {
                // TODO: better exception handling
                throw new RuntimeException(e);
            }
        }
    }

    private ListModel listModel() {
        DefaultListModel ret = new DefaultListModel();
        for (Resource resource : getResources()) {
            ret.addElement(resource);
        }
        return ret;
    }

    private List<Resource> getResources() {
        return resources(getProject().listResources(resourceType));
    }

    private List<Resource> resources(List<String> names) {
        List<Resource> ret = new ArrayList<Resource>();
        for (String filename : names) {
            ret.add(new Resource(filename));
        }
        return ret;
    }

    private static class ResourceItemRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 4681660387567980140L;

        ResourceItemRenderer() {
            super();
            setBorder(noFocusBorder);
            DATUtil.fixFont(this, "List.font");
        }
    }

    private class ResourceListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            int[] indices = ResourceEditorPanel.this.resourceList.getSelectedIndices();
            boolean enabled = (indices != null && indices.length > 0);
            ResourceEditorPanel.this.removeResourcesButton.setEnabled(enabled);
        }
    }

}
