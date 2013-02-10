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

import java.io.File;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import org.edc.sstone.dat.DesktopAuthoringToolApp;
import org.edc.sstone.dat.event.ComponentModelChangeListener;
import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.file.ZipEntryFile;
import org.edc.sstone.project.ResourceType;
import org.edc.sstone.swing.Binder;
import org.edc.sstone.swing.filechooser.ZipFolderFileSystemView;
import org.jdesktop.application.Action;

/**
 * @author Greg Orlowski
 */
public class ZipResourceSelectionPanel extends Box {

    private static final long serialVersionUID = 6454190977621038539L;
    private static final Pattern regularFilePattern = Pattern.compile("^.+\\.\\w{2,8}$");

    ResourceType resourceType;
    private JTextField resourceField;

    public ZipResourceSelectionPanel(Object boundBean, String beanProperty, final ResourceType resourceType) {
        this(boundBean, beanProperty, resourceType, false);
    }

    public ZipResourceSelectionPanel(Object boundBean, String beanProperty, final ResourceType resourceType,
            boolean registerComponentModelChangeListener) {
        super(BoxLayout.X_AXIS);
        this.resourceType = resourceType;

        resourceField = new JTextField();
        resourceField.setEditable(false);
        Binder binder = DATUtil.bind(resourceField);
        if (registerComponentModelChangeListener) {
            binder = binder.withChangeListeners(new ComponentModelChangeListener());
        }
        binder.toBean(boundBean, beanProperty);

        JButton showFileChooserButton = SAFUtil.button("zipResourceSelectionPanel.showFileChooserButton",
                SAFUtil.action(this, "chooseZipResource"));
        showFileChooserButton.setBorderPainted(true);

        add(resourceField);
        add(Box.createHorizontalStrut(12));
        add(showFileChooserButton);
    }

    @Action
    public void chooseZipResource() {
        // JFileChooser fileChooser = new EFileChooser(getFileChooserDir());
        JFileChooser fileChooser = new JFileChooser(getFileChooserDir());
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSystemView(new ZipFolderFileSystemView(resourceType.directory));

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selected = fileChooser.getSelectedFile();
            /*
             * oddly, if we double-click on the file, the chooser returns our ZipEntryFile. If we
             * select the file and click OK, it instantiates a new java.io.File for the entry.
             * Because the path does not refer to something on the physical filesystem, isFile()
             * will return false for a regular java.io.File. I could subclass JFileChooser to
             * override this behavior, but for now I am just working around this by assuming that a
             * file that matches the regex is a regular file.
             */
            if (selected != null && isFile(selected)) {
                resourceField.setText(selected.getName());
            }
        }
    }

    protected static boolean isFile(File f) {
        return (((f instanceof ZipEntryFile) && f.isFile())
        || regularFilePattern.matcher(f.getName()).matches());
    }

    protected File getFileChooserDir() {
        String path = resourceType.directory;
        File zipFile = DesktopAuthoringToolApp.getInstance().getProject().getTempFile();
        return new ZipEntryFile(zipFile, path, true);
    }
}
