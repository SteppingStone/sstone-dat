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
package org.edc.sstone.dat.laf;

import javax.swing.UIManager;

import org.edc.sstone.util.OperatingSystemPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Greg Orlowski
 */
public class LafManagerFactory {

    private static final Logger logger = LoggerFactory.getLogger(LafManagerFactory.class);

    public LafManager getManager() {
        logger.debug("LAF: " + UIManager.getLookAndFeel().getName());
        if (isWindowsLaf()) {
            return new WindowsLafManager();
        } else {
            String lafName = UIManager.getLookAndFeel().getName().toLowerCase();
            if(lafName.contains("metal")) {
             return new MetalLafManager();   
            }
        }
        return new BaseLafManager();
    }
    
    

    protected boolean isWindowsLaf() {
        return (OperatingSystemPlatform.get() == OperatingSystemPlatform.WINDOWS
        && UIManager.getLookAndFeel().getName().toLowerCase().indexOf("windows") >= 0);
    }
}
