package org.edc.sstone.microemu;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.bushe.swing.event.EventBus;
import org.edc.sstone.dat.util.SAFUtil;

public class EmulatorConfigurationPanel extends JPanel {

    private static final long serialVersionUID = -8423963009413124629L;

    public EmulatorConfigurationPanel() {
        super();
        ButtonGroup deviceGroup = new ButtonGroup();

        initLayout();

        JLabel deviceLabel = SAFUtil.label("emulator.device.label");
        add(deviceLabel);
        for (EmulatorDevice dev : EmulatorDevice.values()) {
            JRadioButton b = new JRadioButton(dev.toString());
            b.addActionListener(new SetDeviceListener(dev));
            deviceGroup.add(b);
            add(b);

            if (dev == EmulatorDevice.DEFAULT) {
                b.setSelected(true);
            }
        }

    }

    protected void initLayout() {
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        setLayout(layout);
    }

    private static class SetDeviceListener implements ActionListener {

        private SetDeviceListener(EmulatorDevice device) {
            this.device = device;
        }

        private final EmulatorDevice device;

        @Override
        public void actionPerformed(ActionEvent e) {
            EventBus.publish(new SetEmulatorDeviceEvent(device));
        }

    }

}
