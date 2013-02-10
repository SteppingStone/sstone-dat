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
package org.edc.sstone.dat.component.view.config;

import static org.edc.sstone.dat.util.DATUtil.useMonospaceFont;
import static org.edc.sstone.dat.util.SAFUtil.label;

import java.util.Arrays;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.project.Project;
import org.edc.sstone.project.ProjectProperties;
import org.edc.sstone.swing.component.ESpinner;

/**
 * @author Greg Orlowski
 */
public class ProjectConfigurationPanel extends JPanel {

    private static final long serialVersionUID = 5820802794247065495L;

    private final ProjectProperties projectProps;
    private final Project project;

    public ProjectConfigurationPanel(Project project) {
        super();
        this.project = project;
        SAFUtil.setDefaultName(this);
        this.projectProps = new ProjectProperties(project);

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        setLayout(layout);

        setBorder(new EmptyBorder(12, 12, 12, 12));

        layoutComponents();

        SAFUtil.injectComponents(this);
    }

    protected void bind(JComponent component, String beanProperty) {
        DATUtil.bind(component).toBean(projectProps, beanProperty);
    }

    protected void layoutComponents() {
        GroupLayout layout = (GroupLayout) getLayout();

        JLabel alphabetLowercase = label("projectConfigurationPanel.label.alphabetLowercase");
        JLabel alphabetUppercase = label("projectConfigurationPanel.label.alphabetUppercase");

        JLabel imageFileType = label("projectConfigurationPanel.label.imageFileType");
        JLabel audioFileType = label("projectConfigurationPanel.label.audioFileType");

        int min = GroupLayout.PREFERRED_SIZE;
        int pref = GroupLayout.DEFAULT_SIZE;
        int max = GroupLayout.PREFERRED_SIZE;

        JTextField lowerCaseField = new JTextField();
        JTextField upperCaseField = new JTextField();

        JSpinner audioTypeSel = new ESpinner("projectConfigurationPanel.audioTypeSel", Arrays.asList(new String[] {
                "any", "mp3", "wav" }), "any");
        JSpinner imageTypeSel = new ESpinner("projectConfigurationPanel.imageTypeSel", Arrays.asList(new String[] {
                "any", "png", "gif" }), "any");
        useMonospaceFont(lowerCaseField, upperCaseField, audioTypeSel, imageTypeSel);

        bind(lowerCaseField, "alphabetLowerCase");
        bind(upperCaseField, "alphabetUpperCase");

        bind(audioTypeSel, "audioFileType");
        bind(imageTypeSel, "imageFileType");

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(alphabetLowercase)
                                .addComponent(alphabetUppercase)
                                .addComponent(imageFileType)
                                .addComponent(audioFileType)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(lowerCaseField, min, pref, Short.MAX_VALUE)
                                .addComponent(upperCaseField, min, pref, Short.MAX_VALUE)
                                .addComponent(imageTypeSel, min, pref, max)
                                .addComponent(audioTypeSel, min, pref, max)
                        )
                );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(alphabetLowercase)
                                .addComponent(lowerCaseField, min, pref, max)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(alphabetUppercase)
                                .addComponent(upperCaseField, min, pref, max)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(imageFileType)
                                .addComponent(imageTypeSel, min, pref, max)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(audioFileType)
                                .addComponent(audioTypeSel, min, pref, max)
                        )
                );

    }

    public Project getProject() {
        return project;
    }
}
