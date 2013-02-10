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

import javax.swing.tree.MutableTreeNode;

import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.record.writer.model.ComponentContainerRecordWriter;
import org.edc.sstone.record.writer.model.ComponentPresentation;
import org.edc.sstone.record.writer.model.IComponentRecordWriter;
import org.edc.sstone.record.writer.model.ScreenRecordWriter;

/**
 * @author Greg Orlowski
 */
public class ComponentContainerTreeNode<CCRW extends ComponentContainerRecordWriter> extends ComponentTreeNode<CCRW> {

    private static final long serialVersionUID = -5253071081391830098L;

    public ComponentContainerTreeNode(CCRW componentContainerRecordWriter) {
        super(componentContainerRecordWriter);
        allowsChildren = true;
    }

    protected static IComponentRecordWriter recordWriterForNode(MutableTreeNode newChild) {
        ComponentTreeNode<?> componentNode = (ComponentTreeNode<?>) newChild;
        IComponentRecordWriter rw = componentNode.recordWriter;
        return rw;
    }

    @Override
    public boolean isScreenRecord() {
        return ComponentPresentation.isScreen(recordWriter.getPresentation());
    }

    @Override
    public void insert(MutableTreeNode newChild, int childIndex) {
        super.insert(newChild, childIndex);
        getRecordWriter().insertComponent(recordWriterForNode(newChild), childIndex);
        DATUtil.markDirty();
    }

    public void addNodeWithoutModelUpdate(ComponentTreeNode<?> newChild) {
        super.insert(newChild, getChildCount());
    }

    @Override
    public void remove(int childIndex) {
        super.remove(childIndex);
        getRecordWriter().getComponentWriters().remove(childIndex);
        DATUtil.markDirty();
    }

    public Set<ComponentPresentation> getAllowedChildren() {
        return recordWriter.getPossibleChildTypes();
    }

}
