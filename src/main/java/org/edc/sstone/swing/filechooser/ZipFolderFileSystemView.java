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
package org.edc.sstone.swing.filechooser;

import java.io.File;
import java.io.IOException;

import javax.naming.OperationNotSupportedException;
import javax.swing.filechooser.FileSystemView;

import org.edc.sstone.file.ZipEntryFile;

/**
 * @author Greg Orlowski
 */
public class ZipFolderFileSystemView extends FileSystemView {

    // private boolean enableFolderAscend = false;
    // private final File zipFile;
    private final String rootPath;

    public ZipFolderFileSystemView(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public File[] getFiles(File dir, boolean useFileHiding) {
        if (dir instanceof ZipEntryFile) {
            ZipEntryFile zef = (ZipEntryFile) dir;
            return zef.listFiles();
        }
        return new File[] {};
    }
    
    /**
     * I need to override this to fix the file label on windows
     */
    @Override
    public String getSystemDisplayName(File f) {
        return f.getName();
    }

    /*
     * TODO: do I need to replace \\ with / on windows?
     */
    @Override
    public boolean isFileSystemRoot(File dir) {
        return rootPath.equals(dir.getAbsolutePath());
    }

    /**
     * Return null. We do not want to support creating folders
     * 
     * TODO: should we instead throw a {@link OperationNotSupportedException}?
     */
    @Override
    public File createNewFolder(File containingDir) throws IOException {
        return null;
    }

    // public void setEnableFolderAscend(boolean enable) {
    // this.enableFolderAscend = enable;
    // }

}
