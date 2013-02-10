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
package org.edc.sstone.dat.event;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bushe.swing.event.EventBus;
import org.edc.sstone.record.writer.model.ComponentRecordWriter;

/**
 * @author Greg Orlowski
 */
public class ComponentModelChangeListener implements ChangeListener {
    
    @Override
    public void stateChanged(ChangeEvent e) {
        EventBus.publish(new ComponentModelChangeEvent(e.getSource()));
    }

    public static class ComponentModelChangeEvent extends ChangeEvent {

        private static final long serialVersionUID = -7179102101021081716L;

        public ComponentModelChangeEvent(Object source) {
            super(source);
        }

        public ComponentRecordWriter<?> getRecordWriter() {
            return (ComponentRecordWriter<?>) source;
        }
    }

}
