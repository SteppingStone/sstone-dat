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

import static org.edc.sstone.dat.util.SAFUtil.setDefaultName;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.tree.TreePath;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.edc.sstone.dat.DesktopAuthoringToolApp;
import org.edc.sstone.dat.component.view.component.event.ComponentNodeSelectionEvent;
import org.edc.sstone.dat.component.view.component.model.ComponentTreeNode;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.microemu.MicroEmulatorFrame;
import org.edc.sstone.record.writer.model.TextAreaComponentRecordWriter;
import org.jdesktop.application.Action;

/**
 * @author Greg Orlowski
 */
public class ComponentTreeToolbar extends JToolBar {

    private static final long serialVersionUID = 4582507769546445726L;
    private final ComponentTree componentTree;

    private JButton deleteComponentButton;
    private JButton moveComponentUpButton;
    private JButton moveComponentDownButton;
    private JButton editComponentButton;
    private JButton runEmulatorButton;

    public ComponentTreeToolbar(ComponentTree componentTree) {
        super();
        this.componentTree = componentTree;
        setDefaultName(this);

        setRollover(true);
        setFloatable(false);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorderPainted(false);
        setAlignmentX(LEFT_ALIGNMENT);

        initButtons();

        AnnotationProcessor.process(this);
    }

    private void initButtons() {
        deleteComponentButton = button("deleteComponentButton");
        moveComponentUpButton = button("moveComponentUpButton");
        moveComponentDownButton = button("moveComponentDownButton");
        editComponentButton = button("editComponentButton");
        runEmulatorButton = button("runEmulatorButton");

        for (JButton b : new JButton[] { deleteComponentButton, moveComponentUpButton, moveComponentDownButton,
                editComponentButton, runEmulatorButton }) {
            b.setEnabled(false);
            add(b);
        }
    }

    // protected JButton button(String name, String action) {
    // JButton ret = button(name);
    // ret.setAction(SAFUtil.action(this, action));
    // return ret;
    // }

    protected JButton button(String name) {
        JButton ret = SAFUtil.button(name);
        ret.setAction(SAFUtil.action(this, name.substring(0, name.indexOf("Button"))));
        return ret;
    }

    /*
     * TODO: move the guts of moveComponentUp/Down to ComponentTree. I could also create a generic
     * move method that takes a Direction enum to make it more DRY
     */
    @Action
    public void moveComponentUp() {
        ComponentTreeNode<?> selectedNode = componentTree.getSelectedNode();
        if (selectedNode != null) {
            List<TreePath> expandedSiblings = componentTree.getExpandedSiblings(selectedNode);
            TreePath treePath = new TreePath(selectedNode.getPath());
            if (selectedNode.moveUp()) {
                componentTree.updateBranchStructure();
                componentTree.setSelectionPath(treePath);
                componentTree.expandNodes(expandedSiblings);
            }
        }
        updateButtonState();
    }

    @Action
    public void moveComponentDown() {
        ComponentTreeNode<?> selectedNode = componentTree.getSelectedNode();
        if (selectedNode != null) {
            List<TreePath> expandedSiblings = componentTree.getExpandedSiblings(selectedNode);
            TreePath treePath = new TreePath(selectedNode.getPath());
            if (selectedNode.moveDown()) {
                componentTree.updateBranchStructure();
                componentTree.setSelectionPath(treePath);
                componentTree.expandNodes(expandedSiblings);
            }
        }
        updateButtonState();
    }

    @Action
    public void deleteComponent() {
        componentTree.deleteSelectedNode();
        updateButtonState();
    }

    @Action
    public void editComponent() {
        ComponentTreeNode<?> selectedNode = componentTree.getSelectedNode();
        if (selectedNode.getRecordWriter() instanceof TextAreaComponentRecordWriter<?>) {
            componentTree.editNode(selectedNode);
        }
    }

    @Action
    public void runEmulator() {
        //MicroEmulatorFrame emulatorFrame = new MicroEmulatorFrame();
        MicroEmulatorFrame emulatorFrame = DesktopAuthoringToolApp.getInstance().getEmulatorFrame();
        
        emulatorFrame.launch(componentTree.getNavigationCoordinate());
    }

    private void updateButtonState() {
        ComponentTreeNode<?> selectedNode = componentTree.getSelectedNode();
        boolean upEnabled = false;
        boolean downEnabled = false;
        boolean editEnabled = false;
        boolean deleteEnabled = false;
        if (selectedNode != null) {
            editEnabled = true;
            deleteEnabled = true;
            boolean hasParent = selectedNode.getParent() != null;

            if (hasParent && selectedNode.getSiblingCount() > 0) {
                upEnabled = selectedNode.getPreviousSibling() != null;
                downEnabled = selectedNode.getNextSibling() != null;
            }
        }
        deleteComponentButton.setEnabled(deleteEnabled);
        editComponentButton.setEnabled(editEnabled);
        moveComponentUpButton.setEnabled(upEnabled);
        moveComponentDownButton.setEnabled(downEnabled);
    }

    @EventSubscriber(eventClass = ComponentNodeSelectionEvent.class)
    public void onEvent(ComponentNodeSelectionEvent event) {
        runEmulatorButton.setEnabled(event.isSelectedNodeScreenRecord());
        updateButtonState();
    }
}