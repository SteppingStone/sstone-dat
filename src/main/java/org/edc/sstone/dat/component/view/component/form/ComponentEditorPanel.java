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
package org.edc.sstone.dat.component.view.component.form;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.record.writer.model.ComponentRecordWriter;
import org.edc.sstone.record.writer.model.ScreenRecordWriter;

/**
 * @author Greg Orlowski
 */
public class ComponentEditorPanel extends JTabbedPane {

    private static final long serialVersionUID = 3412951225378478005L;

    // protected JPanel style
    // protected final R recordWriter;

    // private final List<ComponentForm<R>> componentFormPanels = new ArrayList<ComponentForm<R>>();

    public ComponentEditorPanel(LabeledComponent... tabs) {
        super();
        addTabs(tabs);
    }

    protected void addTabs(LabeledComponent... lcs) {
        for (LabeledComponent lc : lcs) {
            if (lc != null) {
                addTab(lc.getLabel(), new JScrollPane(lc.getComponent()));
            }
        }
    }

    protected void addTabs() {
        /*
         * TODO: ScreenSeriesRecordWriter does not have a style, which prevents me from putting
         * getStyle() in IComponentRecordWriter... consider cleaning up the hierarchy or adding
         * canBeStyled() and having getStyle return null in SSRW
         */
        // if (recordWriter instanceof ComponentRecordWriter<?>) {
        // addComponentStyleTab((ComponentRecordWriter<?>) recordWriter);
        // }

    }

    protected void addComponentStyleTab(ComponentRecordWriter<?> rw) {
        StyleEditorForm<ComponentRecordWriter<?>> styleTab = new StyleEditorForm<ComponentRecordWriter<?>>(rw);
        addTab(getComponentStyleTabLabel(rw), new JScrollPane(styleTab));
    }

    protected String getComponentStyleTabLabel(ComponentRecordWriter<?> rw) {
        String key = (rw instanceof ScreenRecordWriter)
                ? "componentEditorPanel.screenStyleTab"
                : "componentEditorPanel.styleTab";
        return SAFUtil.getString(key);
    }

}
