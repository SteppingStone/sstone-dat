package org.edc.sstone.microemu;

public class SetEmulatorDeviceEvent {

    public SetEmulatorDeviceEvent(EmulatorDevice emulatorDevice) {
        super();
        this.emulatorDevice = emulatorDevice;
    }

    public final EmulatorDevice emulatorDevice;
}
