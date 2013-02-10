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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.edc.sstone.dat.DesktopAuthoringToolApp;
import org.edc.sstone.dat.component.view.component.event.AddComponentNodeEvent;
import org.edc.sstone.dat.component.view.component.event.ComponentNodeSelectionEvent;
import org.edc.sstone.dat.component.view.component.event.OpenComponentEditorEvent;
import org.edc.sstone.dat.component.view.component.model.ComponentContainerTreeNode;
import org.edc.sstone.dat.component.view.component.model.ComponentTreeNode;
import org.edc.sstone.dat.event.ComponentModelChangeListener.ComponentModelChangeEvent;
import org.edc.sstone.dat.event.SetProjectEvent;
import org.edc.sstone.project.Project;
import org.edc.sstone.record.writer.RecordWriterFactory;
import org.edc.sstone.record.writer.model.ComponentContainerRecordWriter;
import org.edc.sstone.record.writer.model.ComponentPresentation;
import org.edc.sstone.record.writer.model.IComponentRecordWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Orlowski
 */
public class ComponentTree extends JTree {

    // TODO: define MAX_LABEL_LENGTH in a resource
    static final int MAX_LABEL_LENGTH = 26;
    private static final long serialVersionUID = -1505652686793587631L;
    protected RecordWriterFactory recordWriterFactory = new RecordWriterFactory();

    private static final Logger logger = LoggerFactory.getLogger(ComponentTree.class);

    private ComponentTreeNode<?> nodeForEdit = null;

    public ComponentTree() {
        super((TreeModel) null); // disambiguate the constructor
        setLargeModel(true);
        setRootVisible(true);
        setShowsRootHandles(true);
        setToggleClickCount(0);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        AnnotationProcessor.process(this);
        DesktopAuthoringToolApp.getInstance().getLafManager().configure(this);

        setCellRenderer(new ComponentNodeCellRenderer());

        addTreeSelectionListener(new ComponentTreeNodeSelectionListener());
        addMouseListener(new NodeDoubleClickListener());
    }

    @EventSubscriber(eventClass = SetProjectEvent.class)
    public void onEvent(SetProjectEvent event) {
        setProject(event.project);
    }

    @EventSubscriber(eventClass = ComponentModelChangeEvent.class)
    public void onEvent(ComponentModelChangeEvent event) {
        if (nodeForEdit != null && nodeForEdit.getRecordWriter() == event.getRecordWriter()) {
            ((DefaultTreeModel) getModel()).nodeChanged(nodeForEdit);
            // logger.debug("node edited: " + nodeForEdit.getClass().getName());
        }
    }

    @EventSubscriber(eventClass = AddComponentNodeEvent.class)
    public void onEvent(AddComponentNodeEvent event) {
        ComponentTreeNode<?> node = getSelectedNode();

        if (node != null && node.getAllowsChildren() && (node instanceof ComponentContainerTreeNode)) {
            ComponentContainerTreeNode<?> parentNode = (ComponentContainerTreeNode<?>) node;
            IComponentRecordWriter newComponent = parentNode.getRecordWriter()
                    .newChild(recordWriterFactory, event.getComponentType());

            List<TreePath> expandedChildren = getExpandedChildren(parentNode);
            parentNode.add(buildTreeNode(newComponent));
            ((DefaultTreeModel) getModel()).nodeStructureChanged(parentNode);
            expandNodes(expandedChildren);
        } else if (node == null && getRootDefaultNode() == null) {
            Project project = DesktopAuthoringToolApp.getInstance().getProject();
            project.setIndex(recordWriterFactory.newComponentWriter(event.getComponentType()));
            setProject(project); // this creates a new model + rebuilds builds the tree
        }
    }

    protected void setProject(Project project) {
        if (project != null) {
            setModel(new DefaultTreeModel(buildComponentContainerNode(project.getIndex())));
        }
    }

    public ComponentTreeNode<?> getSelectedNode() {
        Object ret = getLastSelectedPathComponent();
        return ret != null ? (ComponentTreeNode<?>) ret : null;
    }

    /**
     * @return the offset to the selected node as an array of integers representing the offset of
     *         the parent node at every level of the tree. The length of the array will be equal to
     *         the depth of the node. an empty array indicates the root node.
     */
    public int[] getNavigationCoordinate() {
        TreeNode currNode = getSelectedNode();
        List<Integer> retList = new LinkedList<Integer>();
        while (currNode.getParent() != null) {
            TreeNode parent = currNode.getParent();
            int idx = parent.getIndex(currNode);
            retList.add(0, Integer.valueOf(idx));
            currNode = parent;
        }
        int[] ret = new int[retList.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = retList.get(i).intValue();
        return ret;
    }

    protected DefaultMutableTreeNode getRootDefaultNode() {
        Object root = getModel().getRoot();
        return root == null ? null : (DefaultMutableTreeNode) root;
    }

    protected ComponentContainerTreeNode<ComponentContainerRecordWriter>
            buildComponentContainerNode(ComponentContainerRecordWriter componentContainerNode) {

        ComponentContainerTreeNode<ComponentContainerRecordWriter> containerNode =
                new ComponentContainerTreeNode<ComponentContainerRecordWriter>(componentContainerNode);

        for (IComponentRecordWriter crw : componentContainerNode.getComponentWriters()) {
            containerNode.addNodeWithoutModelUpdate(buildTreeNode(crw));
        }
        return containerNode;
    }

    protected ComponentTreeNode<? extends IComponentRecordWriter> buildTreeNode(IComponentRecordWriter crw) {
        if (crw instanceof ComponentContainerRecordWriter) {
            return buildComponentContainerNode((ComponentContainerRecordWriter) crw);
        }
        return new ComponentTreeNode<IComponentRecordWriter>(crw);
    }

    public void updateBranchStructure() {
        ((DefaultTreeModel) getModel()).nodeStructureChanged(getSelectedNode().getParent());
    }

    protected void expandNodes(List<TreePath> nodes) {
        for (TreePath tp : nodes)
            expandPath(tp);
    }

    protected List<TreePath> getExpandedChildren(DefaultMutableTreeNode node) {
        List<TreePath> ret = new ArrayList<TreePath>();
        if (node != null) {
            TreePath path = new TreePath(((DefaultMutableTreeNode) node).getPath());
            Enumeration<TreePath> expandedChildren = getExpandedDescendants(path);
            if (expandedChildren != null) {
                while (expandedChildren.hasMoreElements()) {
                    ret.add(expandedChildren.nextElement());
                }
            }
        }
        return ret;
    }

    protected List<TreePath> getExpandedSiblings(DefaultMutableTreeNode node) {
        if (node == null || node.getParent() == null) {
            return new ArrayList<TreePath>();
        }
        TreePath parentPath = new TreePath(((DefaultMutableTreeNode) node.getParent()).getPath());
        List<TreePath> ret = new ArrayList<TreePath>();
        Enumeration<TreePath> expandedSiblingNodes = getExpandedDescendants(parentPath);
        while (expandedSiblingNodes.hasMoreElements()) {
            TreePath tp = expandedSiblingNodes.nextElement();
            if (tp.getLastPathComponent() != node)
                ret.add(tp);
        }
        return ret;
    }

    public void deleteSelectedNode() {
        if (getSelectedNode() != null) {
            TreeNode parent = getSelectedNode().getParent();
            // this is the root node
            if (parent == null) {
                deleteRootNode();
            } else {
                List<TreePath> expandedSiblingNodes = getExpandedSiblings(getSelectedNode());
                ComponentTreeNode<?> node = getSelectedNode();
                if (node == nodeForEdit) {
                    nodeForEdit = null;
                }
                node.delete();
                ((DefaultTreeModel) getModel()).nodeStructureChanged(parent);
                expandNodes(expandedSiblingNodes);
            }
        }
    }

    protected void deleteRootNode() {
        this.nodeForEdit = null;
        ((DefaultTreeModel) getModel()).setRoot(null);
        DesktopAuthoringToolApp.getInstance().getProject().setIndex(null);
        EventBus.publish(new ComponentNodeSelectionEvent(false,
                EnumSet.of(ComponentPresentation.MenuScreen, ComponentPresentation.ScreenSeries)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public String convertValueToText(Object value, boolean selected,
            boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        if (value != null && (value instanceof ComponentTreeNode<?>)) {
            ComponentTreeNode<? extends IComponentRecordWriter> node = (ComponentTreeNode<? extends IComponentRecordWriter>) value;
            IComponentRecordWriter rw = node.getRecordWriter();
            if (rw instanceof IComponentRecordWriter) {
                String label = ((IComponentRecordWriter) rw).getLabel(MAX_LABEL_LENGTH);
                return label;
            }
            return "NODE"; // this should be unreachable
        } else {
            return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
        }
    }

    protected class NodeDoubleClickListener extends MouseAdapter implements MouseListener {
        public void mousePressed(MouseEvent e) {
            if (e.getClickCount() == 2) {
                ComponentTreeNode<?> node = ComponentTree.this.getSelectedNode();
                if (node != null) {
                    editNode(node);
                }
            }
        }
    }

    protected void editNode(ComponentTreeNode<?> node) {
        EventBus.publish(new OpenComponentEditorEvent(node.getRecordWriter()));
        this.nodeForEdit = node;
    }

}
