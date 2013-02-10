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

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.regex.Pattern;

import org.edc.sstone.util.StringUtil;

/**
 * @author Greg Orlowski
 */
public class RegexFormat extends Format implements ValidatingFormat {

    private static final long serialVersionUID = -7194383175379474577L;

    private final Pattern pattern;

    public RegexFormat(String regex) {
        super();
        this.pattern = Pattern.compile(regex);
    }

    // @Override
    // public Object stringToValue(String text) throws ParseException {
    // if (isValid(text))
    // return super.stringToValue(text);
    // throw new ParseException("Regex did not match.", 0);
    // }

    @Override
    public boolean isValid(String str) {
        return pattern.matcher(str).matches();
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        return StringUtil.sbuff(obj.toString());
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return source;
    }

    // Override so no ParseException is thrown
    @Override
    public Object parseObject(String source) throws ParseException {
        return parseObject(source, new ParsePosition(0));
    }

}
