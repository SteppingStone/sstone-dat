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
package org.edc.sstone.dat.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Greg Orlowski
 */
public class IntegerNumberFormatTest {

    @Test
    public void testIsValid() throws Exception {
        IntegerNumberFormat f = new IntegerNumberFormat(false, 2, 4);

        assertTrue(f.isValid("12"));
        assertFalse(f.isValid("12345"));
        assertTrue(f.isValid(""));

        f = new IntegerNumberFormat(false, 0, 4);
        assertTrue(f.isValid(""));
    }

    @Test
    public void testParse() throws Exception {
        IntegerNumberFormat f = new IntegerNumberFormat(false, 2, 4);
        assertEquals(-1,f.parse(""));
        assertEquals(-1,f.parse(null));
        
        assertEquals(52,f.parse("52"));

    }
}
