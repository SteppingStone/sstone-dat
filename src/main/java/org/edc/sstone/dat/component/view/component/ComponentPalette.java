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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.edc.sstone.dat.DesktopAuthoringToolApp;
import org.edc.sstone.dat.component.view.component.event.AddComponentNodeEvent;
import org.edc.sstone.dat.component.view.component.event.ComponentNodeSelectionEvent;
import org.edc.sstone.record.writer.model.ComponentPresentation;

/**
 * @author Greg Orlowski
 */
//public class ComponentPalette extends JPanel {
/**
 * @author Greg Orlowski
 */
public class ComponentPalette extends JToolBar {

    private static final long serialVersionUID = 4922278044386743532L;
    protected List<PaletteButton> paletteButtons = new ArrayList<PaletteButton>();

    // No need for a scroll pane... gimp, e.g., does not have one on its palette
    // private JToolBar componentToolbar;

    public ComponentPalette() {
        super();
        // initFlowButtons();
        initContent();
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ComponentNodeSelectionEvent.class)
    public void onEvent(ComponentNodeSelectionEvent event) {
        updateActiveButtons(event.getAllowedChildTypes());
    }

    private void updateActiveButtons(Set<ComponentPresentation> componentTypes) {
        if (componentTypes == null) {
            for (PaletteButton b : paletteButtons) {
                b.setEnabled(false);
            }
            
        } else {
            for (PaletteButton b : paletteButtons) {
                b.setEnabled(componentTypes.contains(b.componentType));
            }
        }
    }

    protected void initToolbar() {
        setRollover(true);
        setFloatable(false);
        setBorderPainted(false);

        setBorder(null);
        setMargin(new Insets(0, 0, 0, 0));
    }

    protected void initContent() {
        initToolbar();
        addButtons(ComponentPresentation.MenuScreen, ComponentPresentation.ScreenSeries);
        addSeparator();
        addButtons(ComponentPresentation.AudioScreen,
                ComponentPresentation.AnimatedScreen,
                ComponentPresentation.QuestionScreen,
                ComponentPresentation.ContentScreen);
        addSeparator();
        addButtons(ComponentPresentation.ImagePanel,
                ComponentPresentation.TextArea,
                ComponentPresentation.LetterReader,
                ComponentPresentation.SyllableReader,
                ComponentPresentation.WordReader,
                ComponentPresentation.Question);
    }

    protected void addButtons(ComponentPresentation... componentTypes) {
        for (ComponentPresentation componentType : componentTypes) {
            PaletteButton b = new PaletteButton(componentType);
            paletteButtons.add(b);
            add(b);
        }
    }

    protected static class PaletteButton extends JButton {

        private static final long serialVersionUID = -6386942778398173017L;

        private final ComponentPresentation componentType;

        private PaletteButton(ComponentPresentation componentType) {
            super();
            this.componentType = componentType;
            setIcon(DesktopAuthoringToolApp.getInstance().getLargeComponentIcon(componentType));
            setBorderPainted(false);
            setSelected(false);
            setText(null);
            setEnabled(false);
            
            setName(componentType.toString() + ".button");

            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EventBus.publish(new AddComponentNodeEvent(PaletteButton.this.componentType));
                }
            });
        }

    }

    // setLayout(new FlowLayout(FlowLayout.LEFT));
    // setMargin(new Insets(0, 0, 0, 0));

    // TODO Auto-generated method stub
    // componentToolbar = new JToolBar();
    // componentToolbar.setRollover(true);
    // componentToolbar.setFloatable(false);
    // componentToolbar.setBorderPainted(false);
    //
    // // componentToolbar.setBorder(new EmptyBorder(0, 0, 0, 0));
    // componentToolbar.setBorder(null);
    // componentToolbar.setMargin(new Insets(0, 0, 0, 0));

    // componentToolbar.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    // setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    // setBorder(BorderFactory.createEtchedBorder());
    // componentToolbar.setBorder(BorderFactory.createEtchedBorder());

    // JButton button = new JButton();

    // button.setIcon(DesktopAuthoringToolApp.getInstance().getLargeComponentIcon(iconKey));
    // button.setBorderPainted(false);
    // button.setSelected(false);

    // // button.setOpaque(false);
    // // button.setBackground(getBackground());
    // // button.setUI(
    // // button.setBackground(getBackground());
    // // System.err.println("color: " + UIManager.getColor("Button.background"));
    // button.setBackground(new Color(UIManager.getColor("Button.background").getRGB() -
    // 1));
    // button.setText(null);
    //
    // button.setLayout(new GridLayout(1, 1));
    // int borderX = 4;
    // int borderY = 2;
    //
    // button.setBorder(new EmptyBorder(borderY, borderX, borderY, borderX));
    //
    // // Make the disabled buttons invisible too (?)
    // // button.setVisible(false);
    //
    // // This will probperly gray-out the button
    // // button.setEnabled(false);
    //
    // componentToolbar.add(button);
    // add(button);

    // protected void initFlowButtons() {
    // for (ComponentPresentation iconKey : ComponentPresentation.values()) {
    // JButton button = new JButton();
    // button.setIcon(DesktopAuthoringToolApp.getInstance().getLargeComponentIcon(iconKey));
    // button.setBorderPainted(false);
    // button.setSelected(false);
    // // button.setOpaque(false);
    // // button.setBackground(getBackground());
    // // button.setUI(
    // // button.setBackground(getBackground());
    // // System.err.println("color: " + UIManager.getColor("Button.background"));
    // button.setBackground(new Color(UIManager.getColor("Button.background").getRGB() - 1));
    // button.setText(null);
    //
    // button.setLayout(new GridLayout(1, 1));
    // int borderX = 4;
    // int borderY = 2;
    //
    // button.setBorder(new EmptyBorder(borderY, borderX, borderY, borderX));
    //
    // // Make the disabled buttons invisible too (?)
    // // button.setVisible(false);
    //
    // // This will probperly gray-out the button
    // // button.setEnabled(false);
    //
    // add(button);
    // }
    //
    // }

}
