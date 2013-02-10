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

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.bushe.swing.event.EventBus;
import org.edc.sstone.dat.component.view.component.event.ComponentNodeSelectionEvent;
import org.edc.sstone.dat.component.view.component.model.ComponentTreeNode;

/**
 * @author Greg Orlowski
 */
public class ComponentTreeNodeSelectionListener implements TreeSelectionListener {

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        TreePath treePath = e.getPath();

        Object nodeObj = treePath.getLastPathComponent();
        if (nodeObj instanceof ComponentTreeNode<?>) {
            ComponentTreeNode<?> node = ((ComponentTreeNode<?>) nodeObj);
            EventBus.publish(new ComponentNodeSelectionEvent(node.isScreenRecord(),
                    node.getAllowedChildren()));
        }

    }

}
