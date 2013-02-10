/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2012 EDC
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *  
 *

 *  
 */

package org.edc.sstone.microemu;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.midlet.MIDletStateChangeException;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.edc.sstone.dat.DesktopAuthoringToolApp;
import org.edc.sstone.dat.util.SAFUtil;
import org.edc.sstone.j2me.emulation.EmulatorMidlet;
import org.edc.sstone.j2me.io.EmulatorInputStreamProvider;
import org.edc.sstone.project.Project;
import org.jdesktop.application.Action;
import org.microemu.DisplayComponent;
import org.microemu.EmulatorContext;
import org.microemu.MIDletBridge;
import org.microemu.app.Config;
import org.microemu.app.capture.AnimatedGifEncoder;
import org.microemu.app.ui.Message;
import org.microemu.app.ui.StatusBarListener;
import org.microemu.app.ui.swing.DropTransferHandler;
import org.microemu.app.ui.swing.SwingDeviceComponent;
import org.microemu.app.ui.swing.SwingErrorMessageDialogPanel;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.j2se.J2SEDevice;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEFontManager;
import org.microemu.device.j2se.J2SEInputMethod;
import org.microemu.log.Logger;
import org.microemu.log.QueueAppender;

/**
 * This class was ported from {@link org.microemu.app.Main}. It encloses the microemulator UI.
 * 
 * @author Greg Orlowski
 */
public class MicroEmulatorFrame extends JFrame implements MIDletCloseEventListener {

    private static final long serialVersionUID = 1L;

    protected MicroEmulatorCommon common;

    /**
     * We need devicePanel, I believe this is where the actual device display is rendered
     */
    private SwingDeviceComponent devicePanel;

    private QueueAppender logQueueAppender;

    private AnimatedGifEncoder encoder;

    private JLabel statusBar = new JLabel("Status");

    protected EmulatorContext emulatorContext = new EmulatorContext() {

        private InputMethod inputMethod = new J2SEInputMethod();

        private DeviceDisplay deviceDisplay = new J2SEDeviceDisplay(this);

        private FontManager fontManager = new J2SEFontManager();

        public DisplayComponent getDisplayComponent() {
            return devicePanel.getDisplayComponent();
        }

        public InputMethod getDeviceInputMethod() {
            return inputMethod;
        }

        public DeviceDisplay getDeviceDisplay() {
            return deviceDisplay;
        }

        public FontManager getDeviceFontManager() {
            return fontManager;
        }

        public InputStream getResourceAsStream(String name) {
            return MIDletBridge.getCurrentMIDlet().getClass().getResourceAsStream(name);
        }

        public boolean platformRequest(final String URL) {
            new Thread(new Runnable() {
                public void run() {
                    Message.info("MIDlet requests that the device handle the following URL: " + URL);
                }
            }).start();

            return false;
        }
    };

    private ActionListener menuExitListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            synchronized (MicroEmulatorFrame.this) {
                if (encoder != null) {
                    encoder.finish();
                    encoder = null;
                }
            }

            Config.setWindow("main", new Rectangle(MicroEmulatorFrame.this.getX(), MicroEmulatorFrame.this.getY(),
                    MicroEmulatorFrame.this.getWidth(), MicroEmulatorFrame.this
                            .getHeight()), true);

            /*
             * We do NOT want to exit the JVM when the emulator window is closed
             */
            // System.exit(0);
        }
    };

    private StatusBarListener statusBarListener = new StatusBarListener() {
        public void statusBarChanged(String text) {
            FontMetrics metrics = statusBar.getFontMetrics(statusBar.getFont());
            statusBar.setPreferredSize(new Dimension(metrics.stringWidth(text), metrics.getHeight()));
            statusBar.setText(text);
        }
    };

    private WindowAdapter windowListener = new WindowAdapter() {
        public void windowClosing(WindowEvent ev) {
            onClose();
        }

        public void windowIconified(WindowEvent ev) {
            MIDletBridge.getMIDletAccess(MIDletBridge.getCurrentMIDlet()).pauseApp();
        }

        public void windowDeiconified(WindowEvent ev) {
            try {
                MIDletBridge.getMIDletAccess(MIDletBridge.getCurrentMIDlet()).startApp();
            } catch (MIDletStateChangeException ex) {
                System.err.println(ex);
            }
        }
    };

    private EmulatorDevice device;

    @Action
    public void exitEmulator() {
        menuExitListener.actionPerformed(null);

        WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);

        setVisible(false);
        dispose();
    }

    public void onClose() {
        exitEmulator();
    }

    protected void setEnableNetworkConnection(boolean enable) {
        org.microemu.cldc.http.Connection.setAllowNetworkConnection(enable);
    }

    public MicroEmulatorFrame(EmulatorDevice device) {
        SAFUtil.setDefaultName(this);
        this.device = device;

        this.logQueueAppender = new QueueAppender(1024);
        Logger.addAppender(logQueueAppender);

        // Always enable network connectivity (no need to toggle support)
        setEnableNetworkConnection(true);
        setTitle("MicroEmulator"); // TODO: i18n and set a better title for emulator window

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(
                MicroEmulatorFrame.class.getResource("/org/microemu/icon.png")));

        addWindowListener(windowListener);

        Config.loadConfig(null, emulatorContext);
        Logger.setLocationEnabled(Config.isLogConsoleLocationEnabled());

        Rectangle window = Config.getWindow("main", new Rectangle(0, 0, 160, 120));
        this.setLocation(window.x, window.y);

        getContentPane().add(createContents(getContentPane()), "Center");

        this.common = new MicroEmulatorCommon(emulatorContext, this);
        this.common.setStatusBarListener(statusBarListener);
        this.common.loadImplementationsFromConfig();

        Message.addListener(new SwingErrorMessageDialogPanel(this));

        devicePanel.setTransferHandler(new DropTransferHandler());

        SAFUtil.injectComponents(this);
        Container paneContainer = getContentPane();

        /*
         * Bind Ctrl-Q to quit the emulator when pressed in the emulator window. On MAC, this should
         * be command-Q, but we do not support that. Eventually, get the meta key from the
         * lafmanager (platform specific) and the letter key from our messagebundle
         * (language-specific). For now this is hard-coded to Ctrl-Q
         */
        if (paneContainer instanceof JPanel) {
            String exitEmulatorActionKey = "exitEmulator";
            JPanel panel = (JPanel) paneContainer;
            KeyStroke ctrlQ = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK, true);
            InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(ctrlQ, exitEmulatorActionKey);
            panel.getActionMap().put(exitEmulatorActionKey, SAFUtil.action(this, exitEmulatorActionKey));
        }
    }

    protected Component createContents(Container parent) {
        devicePanel = new SwingDeviceComponent();
        devicePanel.addKeyListener(devicePanel);
        addKeyListener(devicePanel);
        return devicePanel;
    }

    protected void updateDevice() {
        devicePanel.init();
        setResizable(false);
        pack();
        devicePanel.requestFocus();
    }

    public void launch(int[] initialScreenPath) {
        List<String> params = new ArrayList<String>();

        // GKO:
        params.add("--device");
        params.add(device.devicePath);

        // TODO: consider using a temp file with the file record store manager.
        // however, if we do, we have to override how the file record store mgr works
        // b/c ootb it expects there to be a running Launcher
        params.add("--rms");
        params.add("memory");

        params.add("--appclassloader");
        params.add("relaxed");

        common.initParams(params, null, J2SEDevice.class);
        updateDevice();

        validate();
        setVisible(true);

        /*
         * GKO: this actually starts up the midlet... maybe we can just start up MIDletContext
         * ourselves, set up midletaccess, etc, and start our midlet without starting the launcher.
         */
        // Look at this in Common protected void startLauncher(MIDletContext midletContext) {

        Project project = DesktopAuthoringToolApp.getInstance().getProject();

        EmulatorInputStreamProvider isp = new EmulatorInputStreamProvider(
                new ByteArrayInputStream(project.getIndex().toByteArray()),
                project.getPropertyStream(),
                new ProjectTempFileInputStreamProvider(project));
        EmulatorMidlet midlet = new EmulatorMidlet(isp, initialScreenPath);
        try {
            MIDletBridge.getMIDletAccess(midlet).startApp();
        } catch (Throwable e) {
            Message.error("Unable to start launcher MIDlet, " + Message.getCauseMessage(e), e);
        } finally {
            MIDletBridge.setThreadMIDletContext(null);
        }

        /*
         * If we invoke MIDletBridge.getMIDletAccess(midlet).startApp() ourselves, we don't have to
         * call app.common.initMIDlet(...). However, we likely have to do some a lot of
         * initialization first.
         */
    }

}
