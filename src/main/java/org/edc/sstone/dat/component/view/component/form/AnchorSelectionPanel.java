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

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.edc.sstone.Constants;
import org.edc.sstone.dat.ResourceConstants;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.record.writer.model.Anchor;
import org.edc.sstone.record.writer.model.RecordWriter;
import org.edc.sstone.swing.component.DataModel;
import org.edc.sstone.swing.component.EButtonGroup;
import org.edc.sstone.swing.component.EToggleButton;

/**
 * @author Greg Orlowski
 */
public class AnchorSelectionPanel extends JPanel implements DataModel<Byte> {

    private static final long serialVersionUID = 147109163909053399L;

    protected byte anchor;
    Color validationOkColor;
    Color validationInvalidColor;

    EButtonGroup<Anchor> hAnchorGroup = new EButtonGroup<Anchor>();
    EButtonGroup<Anchor> vAnchorGroup = new EButtonGroup<Anchor>();

    public AnchorSelectionPanel(Anchor[] horizontalAnchors, Anchor[] verticalAnchors) {
        super();

        GridLayout layout = new GridLayout(2, 3);
        layout.setHgap(10);
        layout.setVgap(6);
        setLayout(layout);

        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        initButtons(horizontalAnchors, verticalAnchors);

        validationOkColor = getBackground();
        validationInvalidColor = SAFUtil.getColor(ResourceConstants.FORM_ERROR_BGCOLOR);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        hAnchorGroup.setEnabled(enabled);
        vAnchorGroup.setEnabled(enabled);
    };

    protected void indicateValidation() {
        setBackground(isValueValid() ? validationOkColor : validationInvalidColor);
        revalidate();
    }

    protected Map<Anchor, Icon> getIconMapping() {
        Map<Anchor, Icon> ret = new HashMap<Anchor, Icon>();
        ret.put(Anchor.LEFT, icon(ResourceConstants.ANCHOR_LEFT_ICON));
        ret.put(Anchor.RIGHT, icon(ResourceConstants.ANCHOR_RIGHT_ICON));
        ret.put(Anchor.HCENTER, icon(ResourceConstants.ANCHOR_HCENTER_ICON));

        ret.put(Anchor.TOP, icon(ResourceConstants.ANCHOR_TOP_ICON));
        ret.put(Anchor.BOTTOM, icon(ResourceConstants.ANCHOR_BOTTOM_ICON));
        ret.put(Anchor.BASELINE, icon(ResourceConstants.ANCHOR_BASELINE_ICON));
        ret.put(Anchor.VCENTER, icon(ResourceConstants.ANCHOR_VCENTER_ICON));

        return ret;
    }

    protected Icon icon(String key) {
        return SAFUtil.getIcon(key);
    }

    protected List<EToggleButton<Anchor>> buttonList(Map<Anchor, Icon> iconMapping, Anchor... anchors) {
        List<EToggleButton<Anchor>> ret = new ArrayList<EToggleButton<Anchor>>();
        for (Anchor anchor : anchors) {
            String name = "anchor.button." + anchor;
            EToggleButton<Anchor> btn = new EToggleButton<Anchor>(iconMapping.get(anchor), name, true);
            btn.setValue(anchor);
            btn.addChangeListener(new ButtonActionListener(this));
            SAFUtil.injectComponents(btn);
            ret.add(btn);
        }
        return ret;
    }

    protected void initButtons(Anchor[] horizontalAnchors, Anchor[] verticalAnchors) {
        List<EToggleButton<Anchor>> hButtons = buttonList(getIconMapping(), horizontalAnchors);
        List<EToggleButton<Anchor>> vButtons = buttonList(getIconMapping(), verticalAnchors);

        addButtons(hAnchorGroup, hButtons);
        addButtons(vAnchorGroup, vButtons);

        addComponents(hButtons);
        addComponents(vButtons);
    }

    protected boolean isValueValid() {
        return (hAnchorGroup.getValue() == null && vAnchorGroup.getValue() == null)
                || (hAnchorGroup.getValue() != null && vAnchorGroup.getValue() != null);
    }

    // private void addUnselectActionListener(ButtonGroup group) {
    // UnselectButtonAction listener = new UnselectButtonAction(group);
    // for (Enumeration<AbstractButton> e = group.getElements(); e.hasMoreElements();)
    // e.nextElement().addChangeListener(listener);
    // }

    static class ButtonActionListener implements ChangeListener {

        private final AnchorSelectionPanel parent;

        private ButtonActionListener(AnchorSelectionPanel parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (parent.isValueValid()) {
                if (parent.hAnchorGroup.getValue() == null && parent.vAnchorGroup.getValue() == null) {
                    parent.setValue(Constants.NUMBER_NOT_SET, false);
                } else {
                    parent.setValue((byte) (parent.hAnchorGroup.getValue().intValue()
                            | parent.vAnchorGroup.getValue().intValue()), false);
                }
            }
            parent.indicateValidation();
        }
    }

    protected void addComponents(List<? extends Component> components) {
        for (Component c : components)
            add(c);
    }

    protected void addButtons(EButtonGroup<Anchor> group, List<EToggleButton<Anchor>> buttons) {
        for (AbstractButton b : buttons)
            group.add(b);
    }

    public Byte getValue() {
        return anchor;
    }

    protected void setValue(Byte value, boolean updateUIModel) {
        Byte oldValue = getValue();
        Byte newValue = value;

        if (oldValue.byteValue() != newValue.byteValue()) {
            if (updateUIModel) {
                updateUIModelFromValue(value);
            }
            this.anchor = value;
            firePropertyChange("value", oldValue, newValue);
        }
    }

    private void updateUIModelFromValue(Byte value) {
        if (RecordWriter.isNull(value)) {
            hAnchorGroup.clearSelection();
            vAnchorGroup.clearSelection();
        } else {
            hAnchorGroup.setValue(Anchor.horizontalAnchor(value));
            vAnchorGroup.setValue(Anchor.verticalAnchor(value));
        }
    }

    @Override
    public void setValue(Byte value) {
        setValue(value, true);
    }

}
