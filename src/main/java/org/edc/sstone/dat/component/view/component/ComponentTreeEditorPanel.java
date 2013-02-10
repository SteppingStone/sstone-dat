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

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Greg Orlowski
 */
public class ComponentTreeEditorPanel extends JPanel {

    private static final long serialVersionUID = 3277059092716555501L;

    private ComponentTreeToolbar toolbar;
    private JScrollPane componentTreePanel;
    private final ComponentTree componentTree;

    ComponentTreeEditorPanel() {
        setDefaultName(this);
        componentTree = new ComponentTree();
        setLayout(initLayout());
        initContents();
    }

    private LayoutManager initLayout() {
        return new BorderLayout();
    }

    private void initContents() {
        componentTreePanel = new JScrollPane(componentTree);
        componentTreePanel.setName("componentTreePanel");

        toolbar = new ComponentTreeToolbar(componentTree);

        add(componentTreePanel, BorderLayout.CENTER);
        add(toolbar, BorderLayout.PAGE_END);
    }

}
