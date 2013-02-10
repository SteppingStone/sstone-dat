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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @author Greg Orlowski
 */
public class EDocument extends PlainDocument {

    private static final long serialVersionUID = 8462101076095985675L;
    protected int maxLength;
    protected int minLength = 0;

    public EDocument(int maxLength) {
        this(0, maxLength);
    }

    public EDocument(int minLength, int maxLength) {
        super();
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null)
            return;
        if ((getLength() + str.length()) <= maxLength) {
            super.insertString(offset, str, attr);
        }
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        if (len > 0 && getLength() - len >= minLength)
            super.remove(offs, len);
    }

}
