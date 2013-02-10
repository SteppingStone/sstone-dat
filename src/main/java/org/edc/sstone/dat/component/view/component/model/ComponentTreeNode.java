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
package org.edc.sstone.dat.component.view.component.model;

import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.edc.sstone.record.writer.model.ComponentContainerRecordWriter;
import org.edc.sstone.record.writer.model.ComponentPresentation;
import org.edc.sstone.record.writer.model.IComponentRecordWriter;
import org.edc.sstone.record.writer.model.TitledComponentRecordWriter;

/**
 * @author Greg Orlowski
 */
public class ComponentTreeNode<RW extends IComponentRecordWriter> extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 8237861672309325837L;

    protected final RW recordWriter;

    public ComponentTreeNode(RW recordWriter) {
        this.recordWriter = recordWriter;
        allowsChildren = false;
    }

    public RW getRecordWriter() {
        return recordWriter;
    }
    
    public boolean isScreenRecord() {
        return false;
    }

    @SuppressWarnings("unchecked")
    protected ComponentContainerTreeNode<? extends ComponentContainerRecordWriter> getParentNode() {
        return (ComponentContainerTreeNode<? extends ComponentContainerRecordWriter>) getParent();
    }

    protected int getIndex() {
        return getParent().getIndex(this);
    }

    // We should fire some change event when this happens
    public boolean moveUp() {
        int idx = getIndex();
        if (idx > 0) {
            // We need to put the parent in a local variable b/c
            // once we remove the node from the parent, getParent will return null.
            ComponentContainerTreeNode<?> parent = getParentNode();
            removeFromParent();
            parent.insert(this, idx - 1);
            return true;
        }
        return false;
    }

    public boolean moveDown() {
        int idx = getIndex();
        if (getParent() != null && idx < getParent().getChildCount() - 1) {
            // We need to put the parent in a local variable b/c
            // once we remove the node from the parent, getParent will return null.
            ComponentContainerTreeNode<?> parent = getParentNode();
            removeFromParent();
            parent.insert(this, idx + 1);
            return true;
        }
        return false;
    }

    // TODO: rework tostring
    public String toString() {
        IComponentRecordWriter rw = getRecordWriter();
        String str = rw.getClass().getSimpleName();
        if (rw instanceof TitledComponentRecordWriter<?>) {
            str = ((TitledComponentRecordWriter<?>) rw).getTitle();
        }
        return str;
    }

    /*
     * We do not need to implement removeFromParent; the super implementation of removeFromParent
     * will get the parent, find the index of this node, and call remove(idx) on the parent.
     */
    public void delete() {
        if (getParent() == null) {
            // TODO: handle deleting the root node
        } else {
            // getParentNode().getRecordWriter().getComponentWriters().remove(getIndex());
            removeFromParent();
        }
    }

    public Set<ComponentPresentation> getAllowedChildren() {
        return null;
    }

}
