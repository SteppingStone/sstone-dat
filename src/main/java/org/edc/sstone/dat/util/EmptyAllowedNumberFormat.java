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

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.regex.Pattern;

import org.edc.sstone.util.StringUtil;

/**
 * @author Greg Orlowski
 */
public abstract class EmptyAllowedNumberFormat extends NumberFormat implements ValidatingFormat {

    private static final long serialVersionUID = 1L;
    protected Pattern pattern;
    protected boolean allowNull = true;

    public boolean isValid(String parseSource) {
        if (allowNull && (parseSource == null || parseSource.isEmpty())) {
            return true;
        }
        return (pattern == null || pattern.matcher(parseSource).matches());
    }

    /**
     * We override the superclass implementation because we allow empty strings
     */
    @Override
    public Object parseObject(String source) throws ParseException {
        return parseObject(source, new ParsePosition(0));
    }

    @Override
    public Number parse(String source) throws ParseException {
        return parse(source, new ParsePosition(0));
    }

    protected void verifyParsePosition(ParsePosition parsePosition) {
        if (parsePosition.getIndex() != 0) {
            throw new IllegalArgumentException("It is only valid to parse numbers from position 0");
        }
    }

    protected StringBuffer sbuff(String str) {
        return StringUtil.sbuff(str);
    }

    public boolean isAllowNull() {
        return allowNull;
    }

    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }

}
