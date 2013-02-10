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
package org.edc.sstone.dat.service;

/**
 * This tracks edits made to the state of a Project. Eventually, we can add undo/redo functionality
 * to this manager. For now, just track whether or not the project state is dirty.
 * 
 * @author Greg Orlowski
 */
public class ProjectChangeManager {

    private boolean dirty = false;

    public synchronized boolean isDirty() {
        return dirty;
    }

    public synchronized void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

}
