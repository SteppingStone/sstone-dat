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
package org.edc.sstone.dat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.UIManager;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.edc.sstone.dat.component.MainWindow;
import org.edc.sstone.dat.event.SetProjectEvent;
import org.edc.sstone.dat.laf.LafManager;
import org.edc.sstone.dat.laf.LafManagerFactory;
import org.edc.sstone.dat.service.FontManager;
import org.edc.sstone.dat.service.ProjectChangeManager;
import org.edc.sstone.dat.util.DATUtil;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.microemu.EmulatorDevice;
import org.edc.sstone.microemu.MicroEmulatorFrame;
import org.edc.sstone.microemu.SetEmulatorDeviceEvent;
import org.edc.sstone.project.Project;
import org.edc.sstone.project.ProjectLoadException;
import org.edc.sstone.record.writer.model.ComponentPresentation;
import org.edc.sstone.saf.ApplicationBase;
import org.jdesktop.application.Application;
import org.slf4j.Logger;

/**
 * @author Greg Orlowski
 */
public class DesktopAuthoringToolApp extends ApplicationBase {

    private LafManager lafManager;
    private FontManager fontManager;
    private ProjectChangeManager changeManager = new ProjectChangeManager();
    private Project project;
    private Map<ComponentPresentation, Icon> smallIcons = new HashMap<ComponentPresentation, Icon>();
    private Map<ComponentPresentation, Icon> largeIcons = new HashMap<ComponentPresentation, Icon>();

    private MicroEmulatorFrame emulatorFrame = null;

    protected File projectFile = null;
    private EmulatorDevice emulatorDevice = EmulatorDevice.DEFAULT;

    /*
     * TODO: add an exit listener. Integrate it with an UndoManager and/or ProjectManager to prompt
     * the user of unsaved changes (if any) before exiting.
     */
    public DesktopAuthoringToolApp() {
        super();
    }

    public static void main(String[] args) {
        launch(DesktopAuthoringToolApp.class, args);
    }

    protected void setProjectFile(String filename) {
        if (filename != null && !filename.trim().isEmpty()) {
            File f = new File(filename);
            if (f.isFile()) {
                projectFile = f;
            }
        }
    }

    @Override
    protected void startup() {
        // Try getDesktopProperty("awt.dynamicLayoutSupported") detect if dynamic layout is
        // supported
        // Toolkit.getDefaultToolkit().setDynamicLayout(false);
        // SAFUtil.normalizeResourceFolder(getContext());
        String lafClass = System.getProperty("swing.defaultlaf");
        if (lafClass != null) {
            try {
                UIManager.setLookAndFeel(lafClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        fontManager = new FontManager();
        getContext().getResourceMap().injectFields(fontManager);

        lafManager = new LafManagerFactory().getManager();
        lafManager.configureLaf();
        show(new MainWindow(this));

        if (projectFile != null && projectFile.isFile()) {
            try {
                setProject(Project.load(projectFile));
            } catch (ProjectLoadException e) {
                // TODO: i18n project load error
                getLogger().warn("Could not load project: " + projectFile.getName());
            }
        }
    }

    Logger getLogger() {
        return DATUtil.getLogger(getClass());
    }

    /*
     * 
     * Responsible for initializations that must occur before the GUI is constructed by {@code
     * startup}.
     * 
     * This is called BEFORE startup
     * 
     * This runs on the Swing EDT
     */
    @Override
    protected void initialize(String[] args) {
        super.initialize(args);
        if (args != null && args.length == 1) {
            setProjectFile(args[0]);
        }
        smallIcons = SAFUtil.buildResourceMap("smallIcon", Icon.class, ComponentPresentation.values());
        largeIcons = SAFUtil.buildResourceMap("largeIcon", Icon.class, ComponentPresentation.values());

        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = SetEmulatorDeviceEvent.class)
    public void onEvent(SetEmulatorDeviceEvent event) {
        this.emulatorDevice = event.emulatorDevice;
    }

    /*
     * ready() is called AFTER GUI is initialized. Let the GUI initialize first and then override
     * ready to prepare services that will be used by the non-GUI code.
     */
    @Override
    protected void ready() {
        super.ready();
        // configureGlobaleMessageBundle();
    }

    @Override
    protected void shutdown() {
        super.shutdown();

        /*
         * TODO: delete the temporary project file (?) would doing that here result in possible data
         * loss (?)
         * 
         * ... NO... I should always shut down the view and THEN
         */
    }

    // Masks Application.getInstance()
    public static DesktopAuthoringToolApp getInstance() {
        return Application.getInstance(DesktopAuthoringToolApp.class);
    }

    public Project getProject() {
        return project;
    }

    /*
     * TODO: change the main window title when the project
     */
    public void setProject(Project project) {
        this.project = project;
        this.changeManager = new ProjectChangeManager();
        EventBus.publish(new SetProjectEvent(project));

        // EventBus.publish(new ApplicationModeChangeEvent(ApplicationMode.Components));
        updateWindowTitle();
        // System.err.println("Set project: " + project.getIndex().getTitle());
    }

    public void updateWindowTitle() {
        String appTitle = getContext().getResourceMap().getString(KEY_APPLICATION_TITLE);
        if (project != null) {
            String filename = project.getFilename();
            if (filename == null) {
                filename = "new project"; // TODO: i18n unsaved filename
            }
            appTitle = appTitle + " - " + filename;
        }
        getMainView().getFrame().setTitle(appTitle);
    }

    // Replace this with a Size param instead of SMALL/large
    public Icon getSmallComponentIcon(ComponentPresentation componentType) {
        return smallIcons.get(componentType);
    }

    public Icon getLargeComponentIcon(ComponentPresentation componentType) {
        return largeIcons.get(componentType);
    }

    public LafManager getLafManager() {
        return lafManager;
    }

    public FontManager getFontManager() {
        return fontManager;
    }

    public ProjectChangeManager getChangeManager() {
        return changeManager;
    }

    public MicroEmulatorFrame getEmulatorFrame() {
        synchronized (this) {
            if (emulatorFrame != null) {
                if (emulatorFrame.isVisible()) {
                    emulatorFrame.dispose();
                }
            }
            emulatorFrame = new MicroEmulatorFrame(emulatorDevice);
        }
        return emulatorFrame;
    }

}
