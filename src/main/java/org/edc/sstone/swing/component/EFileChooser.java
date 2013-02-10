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
package org.edc.sstone.swing.component;

import java.io.File;

import javax.swing.JFileChooser;

import org.edc.sstone.dat.DesktopAuthoringToolApp;

/**
 * @author Greg Orlowski
 */
public class EFileChooser extends JFileChooser {

    private static final long serialVersionUID = 6872544102682846398L;

    public EFileChooser(File initialDir) {
        super(initialDir);
        
        DesktopAuthoringToolApp.getInstance().getLafManager().fixFont(this);
    }

    public EFileChooser() {
        this(null);
    }

}
