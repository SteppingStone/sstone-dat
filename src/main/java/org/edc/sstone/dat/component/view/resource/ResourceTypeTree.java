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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.bushe.swing.event.EventBus;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.project.ResourceType;

/**
 * @author Greg Orlowski
 */
public class ResourceTypeTree extends JTree {

    private static final long serialVersionUID = 2431163584278442949L;

    public ResourceTypeTree() {
        super();

        setRootVisible(true);
        setShowsRootHandles(true);
        setToggleClickCount(0);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setCellRenderer(new ResourceNodeCellRenderer());

        buildTree();

        addMouseListener(new NodeClickListener());
    }

    private void openResourceEditor(ResourceType resourceType) {
        EventBus.publish(new OpenResourceEditorEvent(resourceType));
    }

    private class NodeClickListener extends MouseAdapter implements MouseListener {

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getClickCount() >= 1) {
                ResourceTreeNode node = ResourceTypeTree.this.getSelectedNode();
                if (node != null && node.isLeaf()) {
                    openResourceEditor(node.getResourceType());
                }
            }
        }

    }

    protected ResourceTreeNode getSelectedNode() {
        Object path = getLastSelectedPathComponent();
        return (path instanceof ResourceTreeNode) ? (ResourceTreeNode) path : null;
    }

    private void buildTree() {
        // TODO: i18n resource tree nodes
        ResourceTreeNode rootNode = new ResourceTreeNode("Resources");
        for (ResourceType rt : resourceTypes()) {
            ensureParentNode(rootNode, rt).add(new ResourceTreeNode(rt));
        }
        setModel(new DefaultTreeModel(rootNode));
    }

    @SuppressWarnings("unchecked")
    private ResourceTreeNode ensureParentNode(ResourceTreeNode branchNode, String path) {
        ResourceTreeNode node = null;
        for (Enumeration<ResourceTreeNode> e = branchNode.children(); e.hasMoreElements();) {
            node = e.nextElement();
            if (path.equals(node.getBasename()))
                return node;
        }
        node = new ResourceTreeNode(path);
        branchNode.add(node);
        return node;
    }

    private ResourceTreeNode ensureParentNode(ResourceTreeNode rootNode, ResourceType rt) {
        ResourceTreeNode ret = rootNode;
        for (String path : rt.getParentPaths()) {
            ret = ensureParentNode(ret, path);
        }
        return ret;
    }

    @Override
    public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {

        // TODO: i18n resource tree nodes. Add an i18n getLabel() method to ResourceTreeNode that
        // translates the ResourceType/label str into an internationalized string
        if (value != null && (value instanceof ResourceTreeNode)) {
            return ((ResourceTreeNode) value).getBasename();
        }
        return "-Resources-";
    }

    /**
     * This is just here so the ordering in the tree is orthogonal to the enum ordering
     */
    private ResourceType[] resourceTypes() {
        return new ResourceType[] {
                ResourceType.PanelImage,

                ResourceType.LetterAudio,
                ResourceType.SyllableAudio,
                ResourceType.WordAudio,
                ResourceType.AudioTrack
        };
    }

    static class ResourceTreeNode extends DefaultMutableTreeNode {

        private static final long serialVersionUID = -6919770732707148830L;

        ResourceTreeNode(String label) {
            super(label);
        }

        ResourceTreeNode(ResourceType rt) {
            super(rt);
        }

        ResourceType getResourceType() {
            return (userObject instanceof ResourceType) ? (ResourceType) userObject : null;
        }

        String getBasename() {
            ResourceType rt = getResourceType();
            return (rt != null) ? rt.getBasename() : (String) userObject;
        }
    }

    static class ResourceNodeCellRenderer extends DefaultTreeCellRenderer {

        private static final long serialVersionUID = 7644839025110608884L;
        private Icon icon = SAFUtil.getIcon("folder.icon");

        ResourceNodeCellRenderer() {
            super();
            setBorder(new EmptyBorder(2, 1, 2, 1)); // TODO: move to LAF?
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            setIcon(icon);
            return this;
        }
    }

}
