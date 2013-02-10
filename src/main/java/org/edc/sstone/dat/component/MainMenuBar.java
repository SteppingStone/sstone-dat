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
package org.edc.sstone.dat.component;

import static org.edc.sstone.dat.util.SAFUtil.nameComponent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.edc.sstone.dat.DesktopAuthoringToolApp;
import org.edc.sstone.dat.event.ProjectStateChangedEvent;
import org.edc.sstone.dat.event.SetProjectEvent;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.dat.util.SwingUtil;
import org.edc.sstone.project.Project;
import org.edc.sstone.project.ProjectLoadException;
import org.edc.sstone.swing.component.EFileChooser;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task.BlockingScope;

/**
 * @author Greg Orlowski
 */
public class MainMenuBar extends JMenuBar {

    private static final String SSTONE_MODULE_DIR = "SSTONE_MODULE_DIR";

    private static final long serialVersionUID = -3491442830856143035L;

    JMenu fileMenu;
    // JMenu editMenu;
    JMenu helpMenu;

    // FILE menu
    JMenuItem newProjectMenuItem;
    JMenuItem openMenuItem;
    JMenuItem saveMenuItem;
    JMenuItem saveAsMenuItem;

    JMenuItem exitMenuItem;

    MainMenuBar() {
        super();
        fileMenu = menu("fileMenu");
        // editMenu = menu("editMenu");
        helpMenu = menu("helpMenu");

        // helpMenu.setMnemonic(mnemonic)
        newProjectMenuItem = menuItem("newProjectMenuItem", "newProjectAction");
        openMenuItem = menuItem("openMenuItem", "openProjectAction");
        saveMenuItem = menuItem("saveMenuItem", "saveProjectAction");
        saveMenuItem.setEnabled(false);
        saveAsMenuItem = menuItem("saveAsMenuItem", "saveProjectAsAction");

        exitMenuItem = menuItem("exitMenuItem", "exitAction");

        JMenuItem aboutMenuItem = menuItem("aboutMenuItem", "aboutAction");

        for (JMenuItem mi : new JMenuItem[] { newProjectMenuItem, openMenuItem, saveMenuItem,
                saveAsMenuItem, exitMenuItem }) {
            fileMenu.add(mi);
        }

        helpMenu.add(aboutMenuItem);

        add(fileMenu);
        add(helpMenu);

        AnnotationProcessor.process(this);
    }

    static JMenu menu(String name) {
        return nameComponent(new JMenu(), name);
    }

    static JMenuItem menuItem(String name) {
        return nameComponent(new JMenuItem(), name);
    }

    JMenuItem menuItem(String name, String action) {
        JMenuItem ret = menuItem(name);
        // ret.setAction(SAFUtil.action(action));
        ret.setAction(SAFUtil.action(this, action));
        return ret;
    }

    @EventSubscriber(eventClass = SetProjectEvent.class)
    public void onEvent(SetProjectEvent event) {
        toggleSaveEnabled(event.project);
    }

    @EventSubscriber(eventClass = ProjectStateChangedEvent.class)
    public void onEvent(ProjectStateChangedEvent event) {
        // NOTE: for a new project, until we save-as once, we cannot
        // just "save" b/c we do not have a filename
        toggleSaveEnabled(getProject());
    }

    @Action
    public void exitAction() {
        boolean shouldContinue = checkUncommittedChanges();
        if (shouldContinue) {
            /*
             * TODO: this is a potentially fragile operation. If checkUncommittedChanges returns
             * true but does not save, we're going to delete the temp file anyway... we should
             * probably use a checked exception to guard against this possibility
             */
            Project project = getProject();
            if (project != null && project.getTempFile() != null && project.getTempFile().isFile()) {
                project.getTempFile().delete();
            }
            Application.getInstance().exit();
        }
    }

    @Action
    public void newProjectAction() {
        try {
            boolean shouldContinue = checkUncommittedChanges();
            if (shouldContinue) {
                Project project = Project.newProject();
                DesktopAuthoringToolApp.getInstance().setProject(project);
            }
        } catch (IOException e) {
            // TODO handle newProject IO exception
            e.printStackTrace();
        }
    }

    /**
     * @return true to continue, false to cancel.
     */
    protected boolean checkUncommittedChanges() {
        final Project project = getProject();
        boolean ret = true;
        if (project != null && DesktopAuthoringToolApp.getInstance().getChangeManager().isDirty()) {
            int status = showYesNoCancelDialog(
                    SAFUtil.getString("unsavedChangesPrompt.text"),
                    SAFUtil.getString("unsavedChangesPrompt.title"));
            switch (status) {
                case 0: // yes
                    if (project.getFilename() == null) {
                        ret = saveAs();
                    } else {
                        saveProjectAction();
                    }
                    break;
                case 1: // no
                    // DO NOTHING
                    break;
                case 2: // cancel
                    ret = false;
                    break;
            }
        }
        return ret;
    }

    protected int showYesNoCancelDialog(Object[] options, String msg, String title) {

        return JOptionPane.showOptionDialog(this, msg, title, JOptionPane.WARNING_MESSAGE,
                JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
    }

    protected int showYesNoCancelDialog(String msg, String title) {
        Object[] options = {
                SAFUtil.getString("option.yes"),
                SAFUtil.getString("option.no"),
                SAFUtil.getString("option.cancel")
        };
        return showYesNoCancelDialog(options, msg, title);
    }

    private void setProject(Project project) {
        DesktopAuthoringToolApp.getInstance().setProject(project);
        saveMenuItem.setEnabled(false);
    }

    @Action(block = BlockingScope.APPLICATION)
    public void openProjectAction() {
        boolean shouldContinue = checkUncommittedChanges();
        if (!shouldContinue) {
            return;
        }

        JFileChooser projectLoadDialog = new EFileChooser();

        String dir = System.getenv(SSTONE_MODULE_DIR);
        if (dir != null && !dir.isEmpty()) {
            projectLoadDialog.setCurrentDirectory(new File(dir));
        }

        int returnVal = projectLoadDialog.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File projectFile = projectLoadDialog.getSelectedFile();
            try {
                if (Project.hasUnsavedTempFile(projectFile)) {
                    attemptRecovery(projectFile);
                } else {
                    setProject(Project.load(projectFile));
                }
            } catch (ProjectLoadException e) {
                // TODO: handle project load error
                e.printStackTrace();
            }
        }
    }

    private void attemptRecovery(File projectFile) {
        File tempFile = Project.tempFileFor(projectFile);

        // TODO: datefmt
        String tempFileMTime = new Date(tempFile.lastModified()).toString();
        String projectFileMTime = new Date(projectFile.lastModified()).toString();

        Object[] options = {
                SAFUtil.getString("option.yesRecover"),
                SAFUtil.getString("option.noRecover"),
                SAFUtil.getString("option.cancel")
        };
        int status = showYesNoCancelDialog(options,
                SAFUtil.getString("attemptUnsavedRecovery.text",
                        tempFile.getName(), tempFileMTime,
                        projectFile.getName(), projectFileMTime),
                SAFUtil.getString("attemptUnsavedRecovery.title"));

        try {
            switch (status) {
                case 0: // yes
                    setProject(Project.recover(projectFile));
                    break;
                case 1: // no
                    setProject(Project.load(projectFile));
                    break;
                case 2: // cancel
                    // DO NOTHING
                    break;
            }
        } catch (ProjectLoadException e) {
            throw new RuntimeException(e);
        }
    }

    @Action
    public void aboutAction() {
        final JDialog dialog = new AboutDialog(JOptionPane.getRootFrame());
        dialog.pack();
        SwingUtil.center(dialog);
        dialog.setVisible(true);
    }

    @Action
    public void saveProjectAction() {
        try {
            getProject().save();
            DesktopAuthoringToolApp.getInstance().getChangeManager().setDirty(false);
            saveMenuItem.setEnabled(false);
        } catch (IOException e) {
            // TODO handle IOException on save
            JOptionPane.showMessageDialog(this, "Error. Project could not be saved: "
                    + e.getMessage() + ".");
            throw new RuntimeException(e);
        }
    }

    protected boolean isProjectFileDefined() {
        return getProject().getFilename() != null;
    }

    protected Project getProject() {
        return DesktopAuthoringToolApp.getInstance().getProject();
    }

    protected void toggleSaveEnabled(Project project) {
        saveMenuItem.setEnabled(project.getFilename() != null);
    }

    @Action(block = BlockingScope.APPLICATION)
    public void saveProjectAsAction() {
        saveAs();
    }

    protected boolean saveAs() {
        boolean wasSaved = false;
        JFileChooser saveAsDialog = new EFileChooser();

        String dir = System.getenv(SSTONE_MODULE_DIR);
        if (dir != null && !dir.isEmpty()) {
            saveAsDialog.setCurrentDirectory(new File(dir));
        }

        int returnVal = saveAsDialog.showSaveDialog(MainMenuBar.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File newProjectFile = saveAsDialog.getSelectedFile();
            try {
                getProject().saveAs(newProjectFile);
                DesktopAuthoringToolApp.getInstance().getChangeManager().setDirty(false);
                DesktopAuthoringToolApp.getInstance().updateWindowTitle();
                // toggleSaveEnabled(getProject());
                saveMenuItem.setEnabled(false);
                wasSaved = true;
            } catch (IOException e) {
                // implement IOexception handling for save as
                e.printStackTrace();
            }
        }
        return wasSaved;
    }

}
