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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.edc.sstone.dat.DesktopAuthoringToolApp;
import org.edc.sstone.dat.component.view.component.model.ComponentTreeNode;
import org.edc.sstone.dat.util.SwingUtil;
import org.edc.sstone.record.writer.model.ComponentPresentation;
import org.edc.sstone.record.writer.model.IComponentRecordWriter;

/**
 * @author Greg Orlowski
 */
public class ComponentNodeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = -7795652392719894532L;

    // TODO: move borders to resource properties file
    private static final int Y_BORDER = 2;
    private static final int X_BORDER = 1;

    public ComponentNodeCellRenderer() {
        super();
        setBorder(new EmptyBorder(Y_BORDER, X_BORDER, Y_BORDER, X_BORDER));
        fixFont();
    }

    /**
     * Not all LAFs seem to use Tree.font to render the text in the nodes.
     */
    protected void fixFont() {
        Font font = getFont();
        if (font == null) {
            setFont(UIManager.getFont("Tree.font"));
        }
    }

    protected Icon getComponentIcon(ComponentTreeNode<IComponentRecordWriter> node) {
        IComponentRecordWriter rw = node.getRecordWriter();
        Icon ret = null;
        if (rw instanceof IComponentRecordWriter) {
            ComponentPresentation iconKey = ((IComponentRecordWriter) rw).getPresentation();
            DesktopAuthoringToolApp app = DesktopAuthoringToolApp.getInstance();
            ret = app.getSmallComponentIcon(iconKey);
        }
        return ret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        Icon icon = getComponentIcon((ComponentTreeNode<IComponentRecordWriter>) value);
        if (icon != null) {
            setIcon(icon);
        }
        return this;
    }

}
