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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.record.writer.model.IComponentRecordWriter;

/**
 * @author Greg Orlowski
 */
public abstract class ComponentForm<R extends IComponentRecordWriter>
        extends JPanel implements LabeledComponent {

    private static final long serialVersionUID = 5613723441122509988L;
    protected final R recordWriter;
    private int componentFormRowIdx = 0;

    private List<ComponentRow> componentRows = new ArrayList<ComponentRow>();
    protected String label;

    protected ComponentForm(R recordWriter) {
        this(null, recordWriter);
    }

    protected ComponentForm(String labelKey, R recordWriter) {
        super();
        this.recordWriter = recordWriter;
        if (labelKey != null) {
            this.label = SAFUtil.getString(labelKey);
        } else {
            String l = SAFUtil.getString("componentForm.tabLabel."
                    + recordWriter.getPresentation().toString());
            this.label = l == null ? SAFUtil.getString("componentForm.tabLabel.component") : l;
        }
        initModel();

        initLayout();

        addFormComponents();

        layoutFormComponents();
    }

    protected abstract void addFormComponents();

    protected void addToComponentForm(String labelName, Component component, boolean fillX) {
        componentRows.add(new ComponentRow(labelName, component, fillX));
    }

    protected void addToComponentForm(String labelName, Component component) {
        addToComponentForm(labelName, component, true);
    }

    private void layoutFormComponents() {
        for (Iterator<ComponentRow> e = componentRows.iterator(); e.hasNext();) {
            ComponentRow row = e.next();
            boolean lastRow = !e.hasNext();

            JLabel label = new JLabel();
            label.setText(null);
            label.setName(row.labelName);
            SAFUtil.injectComponents(label);

            GridBagConstraints componentConstraints = componentConstraints();
            GridBagConstraints labelConstraints = labelConstraints();
            if (!row.fillX) {
                componentConstraints.fill = GridBagConstraints.NONE;
            }
            if (lastRow) {
                labelConstraints.weighty = componentConstraints.weighty = 1.0;
            }

            add(label, labelConstraints);
            add(row.component, componentConstraints);
            componentFormRowIdx++;
        }
    }

    protected void initLayout() {
        /*
         * Using GridBagLayout for the component form because I think it is easier for descendant
         * classes to add + align components to a form with existing components using GBL than using
         * SpringLayout or GroupLayout. The latter 2 work better when all components that will
         * appear in the layout are added together.
         */
        setLayout(new GridBagLayout());
    }

    protected void initModel() {
    }

    protected GridBagConstraints labelConstraints() {
        GridBagConstraints gbc = gbcCommon();
        int margin = 12;
        // TODO: get the margin from the LAF settings
        gbc.insets = new Insets(margin, margin, 0, margin);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.weightx = 0.0001;
        return gbc;
    }

    protected GridBagConstraints gbcCommon() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridy = componentFormRowIdx;
        // gbc.weightx = gbc.weighty = 1.0;
        gbc.weighty = 0.0001;
        return gbc;
    }

    protected GridBagConstraints componentConstraints() {
        GridBagConstraints gbc = gbcCommon();
        int margin = 12;
        gbc.insets = new Insets(margin, 0, 0, margin * 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        return gbc;
    }

    private static class ComponentRow {
        private final String labelName;
        private final Component component;
        private final boolean fillX;

        private ComponentRow(String labelName, Component component, boolean fillX) {
            this.labelName = labelName;
            this.component = component;
            this.fillX = fillX;
        }
    }

    protected int getTextFieldWidth(String text) {
        if (text == null)
            return 30;

        int ret = text.length() / 40;
        if (ret < 6)
            return 6;
        else if (ret > 20)
            return 20;
        return ret;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
