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
public class FloatNumberFormatTest {

    @Test
    public void testParseObject() throws Exception {
        FloatNumberFormat fmt = new FloatNumberFormat(1);

        float f = fmt.parse("1.2").floatValue();
        assertEquals(1.2, f, 0.01);
        
        f = fmt.parse("").floatValue();
        assertEquals(-1.0, f, 0.01);
    }

}
