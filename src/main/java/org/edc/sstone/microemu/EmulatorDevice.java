package org.edc.sstone.microemu;

public enum EmulatorDevice {

    SMALL(128, 160, "device/128x160/device.xml"),
    DEFAULT(176, 220, "org/microemu/device/default/device.xml"),
    LARGE(240, 320, "org/microemu/device/large/device.xml");

    private EmulatorDevice(int width, int height, String devicePath) {
        this.width = width;
        this.height = height;
        this.devicePath = devicePath;
    }

    final int width;
    final int height;
    final String devicePath;

    public String toString() {
        StringBuilder sb = new StringBuilder(10);
        return sb.append(width).append(" x ").append(height).toString();
    }

}
