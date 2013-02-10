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
package org.edc.sstone.microemu;

import org.microemu.EmulatorContext;
import org.microemu.MIDletContext;
import org.microemu.app.Common;
import org.microemu.util.JadProperties;

/**
 * @author Greg Orlowski
 */
public class MicroEmulatorCommon extends Common {

    private MIDletCloseEventListener midletCloseListener;

    public MicroEmulatorCommon(EmulatorContext context, MIDletCloseEventListener midletCloseListener) {
        super(context);
        this.midletCloseListener = midletCloseListener;
    }

    public void startLauncher(MIDletContext midletContext) {
        super.startLauncher(midletContext);
    }

    protected JadProperties getJad() {
        return this.jad;
    }

    @Override
    public void notifyDestroyed(MIDletContext midletContext) {
        notifyImplementationMIDletDestroyed();
        midletCloseListener.onClose();
    }

}
