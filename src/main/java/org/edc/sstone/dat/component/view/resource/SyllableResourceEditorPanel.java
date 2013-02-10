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
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.project.Project;
import org.edc.sstone.project.ResourceType;
import org.edc.sstone.swing.component.EFileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Orlowski
 */
public class SyllableResourceEditorPanel extends BaseResourceEditorPanel {

    SyllableResourceTree syllableResourceTree;
    Logger logger = LoggerFactory.getLogger(SyllableResourceEditorPanel.class);

    protected SyllableResourceEditorPanel() {
        super(ResourceType.SyllableAudio);
        addResourcesButton.setEnabled(false);

        syllableResourceTree = new SyllableResourceTree(buildSyllableTreeRootNode());

        syllableResourceTree.setBorder(SAFUtil.getBorder("syllableResourceTree.border"));
        syllableResourceTree.addTreeSelectionListener(new SyllableTreeNodeSelectionListener());

        setMainComponent(new JScrollPane(syllableResourceTree));
    }

    protected SyllableTreeNode buildSyllableTreeRootNode() {
        Project project = getProject();
        SyllableTreeNode rootNode = new SyllableTreeNode("syllables", false);

        Set<String> words = new TreeSet<String>();
        for (String word : project.listResources(ResourceType.WordAudio)) {
            int idx = word.indexOf('.');
            words.add((idx == -1) ? word : word.substring(0, idx));
        }

        List<String> syllableResourcePaths = project.listResources(ResourceType.SyllableAudio);
        for (String p : syllableResourcePaths) {
            words.add(p.contains("/") ? p.substring(0, p.indexOf('/')) : p);
        }

        for (String word : words) {
            rootNode.add(new SyllableTreeNode(word, false));
        }

        for (String syllableResourcePath : syllableResourcePaths) {
            ensureParent(rootNode, syllableResourcePath).add(SyllableTreeNode.forFile(syllableResourcePath));
        }
        // syllableResourceTree = new SyllableResourceTree(rootNode);
        return rootNode;
    }

    @SuppressWarnings("unchecked")
    private SyllableTreeNode ensureParent(SyllableTreeNode rootNode, String path) {
        SyllableTreeNode ret = null;
        String parentPath = new File(path).getParent();
        for (Enumeration<SyllableTreeNode> e = rootNode.children(); e.hasMoreElements();) {
            SyllableTreeNode node = e.nextElement();
            if (parentPath.equals(node.pathName)) {
                ret = node;
                break;
            }
        }
        if (ret == null) {
            ret = new SyllableTreeNode(parentPath, false);
            rootNode.add(ret);
        }
        return ret;
    }

    private static final long serialVersionUID = 4816548498750711252L;

    private static <T> List<T> reversedList(Collection<T> elements) {
        List<T> ret = new ArrayList<T>(elements);
        Collections.reverse(ret);
        return ret;
    }

    @Override
    protected void removeResources() {
        SortedSet<SyllableTreeNode> nodesForRemoval = syllableResourceTree.getSelectionForRemoval();
        if (nodesForRemoval.size() > 0) {
            List<SyllableTreeNode> depthFirst = reversedList(nodesForRemoval);
            String[] paths = new String[depthFirst.size()];
            int i = 0;
            for (SyllableTreeNode node : depthFirst) {
                paths[i++] = node.getZipPath();
            }

            try {
                getProject().removeResources(resourceType, paths);

                Set<String> expandedDirs = syllableResourceTree.expandedDirs();
                syllableResourceTree.setRootNode(buildSyllableTreeRootNode());
                syllableResourceTree.expandDirectoryNodes(expandedDirs);

                DATUtil.markDirty();
            } catch (IOException e) {
                // TODO: better exception handling
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void addResources() {
        SyllableTreeNode dirNode = syllableResourceTree.getSelectedNode();
        if (dirNode != null && dirNode.isDirectory()) {
            JFileChooser resourceChooser = new EFileChooser();
            resourceChooser.setMultiSelectionEnabled(true);

            int returnVal = resourceChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    if (resourceChooser.getSelectedFiles().length > 0) {
                        Set<String> expandedDirs = syllableResourceTree.expandedDirs();

                        getProject().addResources(resourceType, dirNode.pathName, resourceChooser.getSelectedFiles());
                        syllableResourceTree.setRootNode(buildSyllableTreeRootNode());
                        syllableResourceTree.expandDirectoryNodes(expandedDirs);

                        DATUtil.markDirty();
                    }
                } catch (IOException e) {
                    // TODO: better exception handling
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class SyllableResourceTree extends JTree {

        private static final long serialVersionUID = -7449658137688478550L;

        SyllableResourceTree(SyllableTreeNode rootNode) {
            super(new DefaultTreeModel(rootNode));
            setCellRenderer(new SyllableNodeCellRenderer());
            getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        }

        @SuppressWarnings("unchecked")
        void expandDirectoryNodes(Set<String> expandedDirs) {
            SyllableTreeNode rootNode = rootNode();
            for (Enumeration<SyllableTreeNode> e = rootNode.children(); e.hasMoreElements();) {
                SyllableTreeNode dirNode = e.nextElement();
                if (!dirNode.isLeaf() && expandedDirs.contains(dirNode.pathName)) {
                    expandPath(new TreePath(new Object[] { rootNode, dirNode }));
                }
            }
        }

        private void setRootNode(SyllableTreeNode rootNode) {
            setModel(new DefaultTreeModel(rootNode));
        }

        @SuppressWarnings("unchecked")
        SortedSet<SyllableTreeNode> getSelectionForRemoval() {
            TreePath[] paths = getSelectionPaths();
            SortedSet<SyllableTreeNode> ret = new TreeSet<SyllableTreeNode>();
            if (paths != null) {
                for (TreePath path : paths) {
                    SyllableTreeNode node = (SyllableTreeNode) path.getLastPathComponent();
                    if (node.isDirectory()) {
                        for (Enumeration<SyllableTreeNode> e = node.children(); e.hasMoreElements();)
                            ret.add(e.nextElement());
                    }
                    if (node.isFile()) {
                        ret.add(node);
                    }
                }
            }

            List<SyllableTreeNode> addedParentNodes = new ArrayList<SyllableTreeNode>();
            for (SyllableTreeNode node : ret) {
                if (node.isFile() && !ret.contains(node.getParent())) {
                    SyllableTreeNode parent = (SyllableTreeNode) node.getParent();
                    boolean shouldIncludeParent = true;
                    for (Enumeration<SyllableTreeNode> e = parent.children(); e.hasMoreElements();) {
                        if (!ret.contains(e.nextElement())) {
                            shouldIncludeParent = false;
                            break;
                        }
                    }
                    if (shouldIncludeParent) {
                        addedParentNodes.add(parent);
                    }
                }
            }
            ret.addAll(addedParentNodes);
            return ret;
        }

        @Override
        public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {

            // TODO: i18n resource tree nodes. Add an i18n getLabel() method to ResourceTreeNode
            // that
            // translates the ResourceType/label str into an internationalized string
            if (value != null && (value instanceof SyllableTreeNode)) {
                return ((SyllableTreeNode) value).pathName;
            }
            return "--";
        }

        TreePath getRootPath() {
            return getPathForRow(0);
        }

        private SyllableTreeNode rootNode() {
            Object ret = getModel().getRoot();
            return (ret instanceof SyllableTreeNode) ? ((SyllableTreeNode) ret) : null;
        }

        Set<String> expandedDirs() {
            Enumeration<TreePath> expanded = getExpandedDescendants(getRootPath());
            Set<String> ret = new HashSet<String>();
            if (expanded != null) {
                while (expanded.hasMoreElements()) {
                    TreePath path = expanded.nextElement();
                    if (path != null) {
                        ret.add(((SyllableTreeNode) path.getLastPathComponent()).pathName);
                    }
                }
            }
            return ret;
        }

        SyllableTreeNode getSelectedNode() {
            return (SyllableTreeNode) getSelectionPath().getLastPathComponent();
        }

    }

    static class SyllableTreeNode extends DefaultMutableTreeNode implements Comparable<SyllableTreeNode> {

        private static final long serialVersionUID = 5035607661839524608L;

        private final boolean file;
        private final String pathName;
        transient boolean remove = false;

        SyllableTreeNode(String pathName, boolean isFile) {
            this.pathName = pathName;
            this.file = isFile;
        }

        String getZipPath() {
            if (isFile())
                return ((SyllableTreeNode) getParent()).getZipPath() + pathName;
            if (isDirectory())
                return pathName + '/';
            return "";
        }

        boolean isRemove() {
            return remove;
        }

        boolean isFile() {
            return file;
        }

        static SyllableTreeNode forFile(String fileAbsPath) {
            return new SyllableTreeNode(new File(fileAbsPath).getName(), true);
        }

        boolean isDirectory() {
            return !file && !isRoot();
        }

        String getFilePath() {
            StringBuilder sb = new StringBuilder(pathName);
            TreeNode parent = this;
            while ((parent = parent.getParent()) != null) {
                SyllableTreeNode node = (SyllableTreeNode) parent;
                if (!node.isRoot()) {
                    sb.insert(0, '/').insert(0, node.pathName);
                }
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((getZipPath() == null) ? 0 : getZipPath().hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            SyllableTreeNode other = (SyllableTreeNode) obj;
            return getZipPath().equals(other.getZipPath());
        }

        @Override
        public int compareTo(SyllableTreeNode o) {
            return getZipPath().compareTo(o.getZipPath());
        }

    }

    static class SyllableNodeCellRenderer extends DefaultTreeCellRenderer {

        private static final long serialVersionUID = 7644839025110608884L;
        private Icon folderIcon = SAFUtil.getIcon("folder.icon");
        private Icon syllableClipFileIcon = SAFUtil.getIcon("mimeAudio.icon");

        SyllableNodeCellRenderer() {
            super();
            setBorder(new EmptyBorder(2, 1, 2, 1)); // TODO: move to LAF?
            fixFont();
        }

        /*
         * TODO: this is copy/paste from ComponentNodeCellRenderer. Refactor to some common super
         * class.
         */
        protected void fixFont() {
            Font font = getFont();
            if (font == null) {
                setFont(UIManager.getFont("Tree.font"));
            }
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            SyllableTreeNode node = (SyllableTreeNode) value;
            setIcon((node.isRoot() || node.isDirectory())
                    ? folderIcon
                    : syllableClipFileIcon);
            return this;
        }
    }

    class SyllableTreeNodeSelectionListener implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            /*
             * Confusingly, e.getPaths()
             * "Returns the paths that have been added or removed from the selection.", NOT
             * necessarily the array of all currently-selected paths. Therefore, get a reference to
             * the tree to get the current selection paths
             */
            TreePath[] treePaths = SyllableResourceEditorPanel.this.syllableResourceTree.getSelectionPaths();
            boolean addButtonEnabled = false;
            boolean removeButtonEnabled = false;
            if (treePaths != null) {
                if (treePaths.length == 1) {
                    Object nodeObj = treePaths[0].getLastPathComponent();
                    if (nodeObj instanceof SyllableTreeNode) {
                        SyllableTreeNode node = ((SyllableTreeNode) nodeObj);
                        addButtonEnabled = node.isDirectory();
                        removeButtonEnabled = canRemove(node);
                    }
                } else if (treePaths.length > 1) {
                    for (TreePath tp : treePaths) {
                        Object nodeObj = tp.getLastPathComponent();
                        if ((nodeObj instanceof SyllableTreeNode) && canRemove(((SyllableTreeNode) nodeObj))) {
                            removeButtonEnabled = true;
                        } else {
                            removeButtonEnabled = false;
                            break;
                        }
                    }
                }
            }
            SyllableResourceEditorPanel.this.addResourcesButton.setEnabled(addButtonEnabled);
            SyllableResourceEditorPanel.this.removeResourcesButton.setEnabled(removeButtonEnabled);
        }

        private boolean canRemove(SyllableTreeNode node) {
            return node.isFile() || (node.isDirectory() && !node.isLeaf());
        }
    }

}
