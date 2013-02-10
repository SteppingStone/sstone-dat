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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;

import org.edc.sstone.beans.BooleanNotConverter;
import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.dat.util.SwingUtil;
import org.edc.sstone.record.writer.model.FontFace;
import org.edc.sstone.record.writer.model.FontSize;
import org.edc.sstone.record.writer.model.FontStyle;
import org.edc.sstone.record.writer.model.RecordWriter;
import org.edc.sstone.record.writer.model.StyleRecordWriter;
import org.edc.sstone.swing.component.ESpinner;
import org.edc.sstone.swing.component.EToggleButton;
import org.jdesktop.application.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Box {
/**
 * @author Greg Orlowski
 */
public class FontSelectionPanel extends JPanel {

    private static final long serialVersionUID = 1581866947787770393L;
    private static final Logger logger = LoggerFactory.getLogger(FontSelectionPanel.class);

    protected final StyleRecordWriter<?> styleRecordWriter;

    private static final String FONT_FACE_PROPERTY = "fontFace";
    private static final String FONT_SIZE_PROPERTY = "fontSize";
    // private static final String FONT_STYLE_PROPERTY = "fontStyle";

    private JSpinner fontFaceSpinner;
    private JSpinner fontSizeSpinner;
    private EToggleButton<Boolean> disableMagnificationButton;

    private EToggleButton<FontStyle> fontStylePlainToggle;
    private List<EToggleButton<FontStyle>> fontStyleToggles;

    private FontStyleToggleListener fontStyleToggleListener = new FontStyleToggleListener();

    private Box vBox;

    public FontSelectionPanel(StyleRecordWriter<?> styleRecordWriter) {
        super();
        this.styleRecordWriter = styleRecordWriter;

        initComponents();
        initLayout();
        packComponents();

        SAFUtil.injectComponents(this);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        SwingUtil.enableComponents(enabled,
                fontFaceSpinner,
                fontSizeSpinner,
                disableMagnificationButton,
                fontStylePlainToggle);

        SwingUtil.enableComponents(enabled, fontStyleToggles);
    };

    private void addComponent(Component c, int row) {
        JPanel panel = (JPanel) vBox.getComponent(row);
        panel.add(c);
        panel.add(Box.createHorizontalStrut(8));
    }

    private void initLayout() {
        setAlignmentX(Component.LEFT_ALIGNMENT);
        vBox = new Box(BoxLayout.Y_AXIS);
        vBox.setBorder(BorderFactory.createEmptyBorder());
        vBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (int i = 0; i < 2; i++) {
            JPanel panel = new JPanel();
            panel.setLayout(flowLayout());
            panel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.setBorder(BorderFactory.createEmptyBorder());
            vBox.add(panel);
        }
        add(vBox);

        setLayout(flowLayout());
        setBorder(BorderFactory.createEmptyBorder());
    }

    private FlowLayout flowLayout() {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        flowLayout.setHgap(0);
        flowLayout.setVgap(2);
        return flowLayout;
    }

    private void initComponents() {
        /*
         * Oddly, JSpinner is reversing the order of the list. Maybe it is just a bug in a
         * particular JDK version? For now, I am reversing the list and then setting the element as
         * a workaround.
         */
        List<FontFace> fontFaceChoices = Arrays.asList(new FontFace[] {
                FontFace.NULL,
                FontFace.PROPORTIONAL,
                FontFace.MONOSPACE,
                FontFace.SYSTEM
        });
        fontFaceSpinner = spinner("fontFace.field", fontFaceChoices, FontFace.NULL);
        fontSizeSpinner = spinner("fontSize.field", Arrays.asList(FontSize.values()), FontSize.NULL);

        DATUtil.bind(fontFaceSpinner).toBean(styleRecordWriter, FONT_FACE_PROPERTY);
        DATUtil.bind(fontSizeSpinner).toBean(styleRecordWriter, FONT_SIZE_PROPERTY);

        disableMagnificationButton = new EToggleButton<Boolean>("disableMagnification.field",
                SAFUtil.getIcon("disable.magnification.icon"));

        DATUtil.bind(disableMagnificationButton, "selected")
                .withConverter(BooleanNotConverter.getInstance(), true)
                .toBean(styleRecordWriter, "enableFontMagnification");

        // font styles
        fontStylePlainToggle = fontStyleButton(FontStyle.PLAIN);
        fontStylePlainToggle.setAction(SAFUtil.action(this, "togglePlain"));

        fontStyleToggles = new ArrayList<EToggleButton<FontStyle>>();
        for (FontStyle fs : new FontStyle[] { FontStyle.BOLD, FontStyle.ITALIC,
                FontStyle.UNDERLINED, FontStyle.STRIKETHROUGH }) {
            fontStyleToggles.add(fontStyleButton(fs));
        }

        // SAFUtil.getIcon("disable.magnification.icon"));
    }

    @Action
    public void togglePlain() {
        if (fontStylePlainToggle.isSelected()) {
            for (EToggleButton<?> b : fontStyleToggles) {
                b.setSelected(false);
            }
        }
        updateFontStyleValue();
    }

    protected void updateFontStyleValue() {
        if (fontStylePlainToggle.isSelected()) {
            styleRecordWriter.setFontStyle(FontStyle.PLAIN);
        } else {
            List<FontStyle> toggled = new ArrayList<FontStyle>();
            for (EToggleButton<FontStyle> btn : fontStyleToggles) {
                if (btn.isSelected())
                    toggled.add(btn.getValue());
            }
            styleRecordWriter.setFontStyle(toggled);
        }
        DATUtil.markDirty();
    }

    class FontStyleToggleListener implements ActionListener {
        @Override
        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() != null && e.getSource() instanceof EToggleButton<?>) {
                EToggleButton<FontStyle> btn = (EToggleButton<FontStyle>) e.getSource();
                if (btn.isSelected() && fontStylePlainToggle.isSelected()) {
                    fontStylePlainToggle.setSelected(false);
                }
            }
            updateFontStyleValue();
        }
    }

    protected EToggleButton<FontStyle> fontStyleButton(FontStyle style) {
        EToggleButton<FontStyle> btn = new EToggleButton<FontStyle>("fontStyle."
                + style.name().toLowerCase() + ".field");
        btn.setValue(style);
        configureFontStyleButton(btn, style);
        if (style == FontStyle.PLAIN) {
            btn.setSelected((styleRecordWriter.getFontStyle().getStyleForWrite() == style.getValue()));
        } else {
            if (!RecordWriter.isNull(styleRecordWriter.getFontStyle().getStyleForWrite())) {
                btn.setSelected((styleRecordWriter.getFontStyle().getStyleForWrite() & style.getValue())
                        == style.getValue());
            }
            /*
             * fontStyleToggleListener controls the toggle of PLAIN when any non-PLAIN button is
             * clicked. Therefore, we do not want to add it as an action listener on PLAIN
             */
            btn.addActionListener(fontStyleToggleListener);
        }
        return btn;
    }

    protected void configureFontStyleButton(JToggleButton btn, FontStyle style) {
        btn.setFocusable(false);
        btn.setSelected(false);
        switch (style) {
            case PLAIN:
            case UNDERLINED:
            case STRIKETHROUGH:
                btn.setFont(btn.getFont().deriveFont(Font.PLAIN));
                break;
            case BOLD:
                btn.setFont(btn.getFont().deriveFont(Font.BOLD));
                break;
            case ITALIC:
                btn.setFont(btn.getFont().deriveFont(Font.ITALIC));
                break;
        }
    }

    protected JSpinner spinner(String name, List<?> choices, Object defaultValue) {
        return new ESpinner(name, choices, defaultValue);
    }

    private void packComponents() {
        // add(fontFaceSpinner);
        // add(fontSizeSpinner);
        // add(disableMagnificationButton);
        // add(fontStylePlainToggle);

        // Top row
        addComponent(fontFaceSpinner, 0);
        addComponent(fontSizeSpinner, 0);
        addComponent(disableMagnificationButton, 0);

        // 2nd row
        addComponent(fontStylePlainToggle, 1);
        addComponent(Box.createHorizontalStrut(4), 1);
        for (EToggleButton<?> b : fontStyleToggles) {
            addComponent(b, 1);
        }
    }
}
