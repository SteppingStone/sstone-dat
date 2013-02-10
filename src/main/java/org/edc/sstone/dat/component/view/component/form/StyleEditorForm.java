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

import static org.edc.sstone.dat.util.SAFUtil.label;

import java.awt.Color;
import java.awt.Component;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.edc.sstone.beans.FloatConverter;
import org.edc.sstone.beans.IntegerConverter;
import org.edc.sstone.dat.ResourceConstants;
import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.dat.util.FloatNumberFormat;
import org.edc.sstone.dat.util.IntegerNumberFormat;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.record.reader.model.StyleRecord;
import org.edc.sstone.record.writer.model.Anchor;
import org.edc.sstone.record.writer.model.ComponentRecordWriter;
import org.edc.sstone.record.writer.model.StyleRecordWriter;
import org.edc.sstone.swing.component.ETextField;
import org.jdesktop.application.Action;

// <S extends StyleRecordWriter<?>>
/**
 * @author Greg Orlowski
 */
public class StyleEditorForm<RW extends ComponentRecordWriter<?>> extends JPanel implements LabeledComponent {

    private static final long serialVersionUID = -6771563048598419611L;

    protected RW component;
    protected StyleRecordWriter<StyleRecord> styleRecordWriter;

    protected JCheckBox enableCustomStyleToggle;

    protected FontSelectionPanel fontPanel;

    protected ColorSelectionPanel colorSelector;
    protected ColorSelectionPanel backgroundColorSelector;
    protected ColorSelectionPanel highlightColorSelector;

    protected ETextField<Float> lineHeightField;
    protected SpacingEditorComponent marginField;
    protected ETextField<Integer> paddingField;

    protected ETextField<Float> animationStartDelayField;
    protected ETextField<Float> animationPeriodField;

    protected AnchorSelectionPanel componentAnchorSelector;
    protected AnchorSelectionPanel textAlignSelector;

    private Color errorBackgroundColor;
    private String label;

    public StyleEditorForm(RW componentRecordWriter) {
        this(null, componentRecordWriter);
    }

    public StyleEditorForm(String labelKey, RW componentRecordWriter) {
        this.component = componentRecordWriter;
        this.styleRecordWriter = isComponentStyleNull()
                ? new StyleRecordWriter<StyleRecord>()
                : getStyleFromComponent();

        this.label = SAFUtil.getString(labelKey == null ? defaultLabelKey() : labelKey);

        errorBackgroundColor = SAFUtil.getColor(ResourceConstants.FORM_ERROR_BGCOLOR);
        initLayout();
        initStyleForm();
        enableFormComponents(!isComponentStyleNull());
        layoutComponents();
        SAFUtil.injectComponents(this);
    }

    protected StyleRecordWriter<StyleRecord> getStyleFromComponent() {
        return component.getStyle();
    }

    protected void setStyleForComponent(StyleRecordWriter<StyleRecord> style) {
        component.setStyle(style);
        DATUtil.markDirty();
    }

    private void enableFormComponents(boolean enabled) {
        for (Component c : new Component[] {
                fontPanel,
                colorSelector,
                backgroundColorSelector,
                highlightColorSelector,
                lineHeightField,
                marginField,
                paddingField,
                animationStartDelayField,
                animationPeriodField,
                componentAnchorSelector,
                textAlignSelector
        }) {
            c.setEnabled(enabled);
        }
    }

    protected boolean isComponentStyleNull() {
        return getStyleFromComponent() == null || getStyleFromComponent().isNull();
    }

    protected void layoutComponents() {
        GroupLayout layout = (GroupLayout) getLayout();

        JLabel enableStyleLabel = label("styleForm.enableCustomStyle.label");

        // replace with name
        JLabel fontSelectionLabel = label("styleForm.font.label");
        JLabel colorSelectorLabel = label("styleForm.color.label");

        JLabel highlightColorSelectorLabel = label("styleForm.highlightColor.label");
        JLabel backgroundColorSelectorLabel = label("styleForm.bgColor.label");

        JLabel lineHeightLabel = label("styleForm.lineHeight.label");

        JLabel marginLabel = label("styleForm.margin.label");
        JLabel paddingLabel = label("styleForm.padding.label");

        JLabel animationStartDelayLabel = label("styleForm.animationStartDelay.label");
        JLabel animationPeriodLabel = label("styleForm.animationPeriod.label");

        JLabel componentAnchorLabel = label("styleForm.componentAnchor.label");
        JLabel textAlignLabel = label("styleForm.textAlign.label");

        int min = GroupLayout.PREFERRED_SIZE;
        int pref = GroupLayout.DEFAULT_SIZE;
        int max = GroupLayout.PREFERRED_SIZE;

        layout.setHorizontalGroup(
                layout.createSequentialGroup()

                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(enableStyleLabel)
                                .addComponent(fontSelectionLabel)
                                .addComponent(colorSelectorLabel)
                                .addComponent(backgroundColorSelectorLabel)
                                .addComponent(highlightColorSelectorLabel)
                                .addComponent(lineHeightLabel)
                                .addComponent(marginLabel)
                                .addComponent(paddingLabel)
                                .addComponent(animationStartDelayLabel)
                                .addComponent(animationPeriodLabel)
                                .addComponent(componentAnchorLabel)
                                .addComponent(textAlignLabel)
                        )
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(enableCustomStyleToggle, min, pref, max)
                                        .addComponent(fontPanel, min, pref, max)
                                        .addComponent(colorSelector, min, pref, max)
                                        .addComponent(backgroundColorSelector, min, pref, max)
                                        .addComponent(highlightColorSelector, min, pref, max)
                                        .addComponent(lineHeightField, min, pref, max)
                                        .addComponent(marginField, min, pref, max)
                                        .addComponent(paddingField, min, pref, max)
                                        .addComponent(animationStartDelayField, min, pref, max)
                                        .addComponent(animationPeriodField, min, pref, max)
                                        .addComponent(componentAnchorSelector, min, pref, max)
                                        .addComponent(textAlignSelector, min, pref, max)
                        )

                );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(enableStyleLabel)
                                        .addComponent(enableCustomStyleToggle, min, pref, max)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(fontSelectionLabel)
                                .addComponent(fontPanel, min, pref, max)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(colorSelectorLabel)
                                .addComponent(colorSelector, min, pref, max)
                        )
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(backgroundColorSelectorLabel)
                                        .addComponent(backgroundColorSelector, min, pref, max)
                        )
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(highlightColorSelectorLabel)
                                        .addComponent(highlightColorSelector, min, pref, max)
                        )
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lineHeightLabel)
                                        .addComponent(lineHeightField, min, pref, max)
                        )
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(marginLabel)
                                        .addComponent(marginField, min, pref, max)
                        )
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(paddingLabel)
                                        .addComponent(paddingField, min, pref, max)
                        )
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(animationStartDelayLabel)
                                        .addComponent(animationStartDelayField, min, pref, max)
                        )
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(animationPeriodLabel)
                                        .addComponent(animationPeriodField, min, pref, max)
                        )
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(componentAnchorLabel)
                                        .addComponent(componentAnchorSelector, min, pref, max)
                        )
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(textAlignLabel)
                                        .addComponent(textAlignSelector, min, pref, max)
                        )

                );

    }

    private void initLayout() {
        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        setLayout(layout);
    }

    @Action
    public void toggleCustomStyle() {
        enableFormComponents(enableCustomStyleToggle.isSelected());
        if (enableCustomStyleToggle.isSelected()) {
            if (isComponentStyleNull()) {
                setStyleForComponent(styleRecordWriter);
            }
        } else {
            if (!isComponentStyleNull()) {
                // Set the style to a new StyleRecordWriter, which will write itself out as null
                setStyleForComponent(new StyleRecordWriter<StyleRecord>());
            }
        }
    }

    private void initStyleForm() {
        enableCustomStyleToggle = new JCheckBox(null, null);
        enableCustomStyleToggle.setAction(SAFUtil.action(this, "toggleCustomStyle"));
        enableCustomStyleToggle.setSelected(!isComponentStyleNull());

        fontPanel = new FontSelectionPanel(styleRecordWriter);

        colorSelector = new ColorSelectionPanel(styleRecordWriter, "color");
        backgroundColorSelector = new ColorSelectionPanel(styleRecordWriter, "backgroundColor");
        highlightColorSelector = new ColorSelectionPanel(styleRecordWriter, "highlightColor");

        lineHeightField = new ETextField<Float>(new FloatNumberFormat(1, 1, 1), 3);
        lineHeightField.setCommitsOnValidEdit(true);
        lineHeightField.setValidateWithFormatter(true);
        lineHeightField.indicateValidInput(lineHeightField.getBackground(), errorBackgroundColor);
        DATUtil.bind(lineHeightField)
                .withConverter(new FloatConverter(1))
                .toBean(styleRecordWriter, "lineHeight");

        marginField = new SpacingEditorComponent(styleRecordWriter, "margin.top", "margin.right",
                "margin.bottom", "margin.left");

        // padding
        paddingField = new ETextField<Integer>(new IntegerNumberFormat(false, 0, 3), 3);
        paddingField.setCommitsOnValidEdit(true);
        paddingField.setValidateWithFormatter(true);
        paddingField.indicateValidInput(paddingField.getBackground(), errorBackgroundColor);
        DATUtil.bind(paddingField)
                .withConverter(new IntegerConverter())
                .toBean(styleRecordWriter, "padding");

        // animation start delay
        animationStartDelayField = new ETextField<Float>(new FloatNumberFormat(1, 1, 1), 3);
        animationStartDelayField.setCommitsOnValidEdit(true);
        animationStartDelayField.setValidateWithFormatter(true);
        animationStartDelayField.indicateValidInput(lineHeightField.getBackground(), errorBackgroundColor);
        DATUtil.bind(animationStartDelayField).
                withConverter(new FloatConverter(1))
                .toBean(styleRecordWriter, "animationStartDelay");

        // animation period
        animationPeriodField = new ETextField<Float>(new FloatNumberFormat(1, 1, 1), 3);
        animationPeriodField.setCommitsOnValidEdit(true);
        animationPeriodField.setValidateWithFormatter(true);
        animationPeriodField.indicateValidInput(lineHeightField.getBackground(), errorBackgroundColor);
        DATUtil.bind(animationPeriodField).
                withConverter(new FloatConverter(1))
                .toBean(styleRecordWriter, "animationPeriod");

        componentAnchorSelector = new AnchorSelectionPanel(Anchor.horizontalAnchors(),
                Anchor.verticalComponentAnchors());
        DATUtil.bind(componentAnchorSelector).toBean(styleRecordWriter, "anchor");

        textAlignSelector = new AnchorSelectionPanel(Anchor.horizontalAnchors(),
                Anchor.verticalTextAlignAnchors());
        DATUtil.bind(textAlignSelector).toBean(styleRecordWriter, "textAlign");

    }

    protected String defaultLabelKey() {
        return "componentEditorPanel.styleTab";
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Component getComponent() {
        return this;
    }

}
