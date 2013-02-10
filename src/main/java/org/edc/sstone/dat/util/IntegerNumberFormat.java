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
import java.text.ParsePosition;
import java.util.regex.Pattern;

import org.edc.sstone.Constants;
import org.edc.sstone.record.writer.model.RecordWriter;

/**
 * @author Greg Orlowski
 */
public class IntegerNumberFormat extends EmptyAllowedNumberFormat {

    private static final long serialVersionUID = -3580554811592095879L;
    protected int radix = 10;

    /*
     * Integer.MAX_VALUE is 10 digits. This regex allows no more than 9 to keep validation simple.
     * This will be sufficient for stepping stone.
     */
    public IntegerNumberFormat() {
        this(false, 1, 9);
    }

    public IntegerNumberFormat(boolean allowNegative, int minDigits, int maxDigits) {
        super();
        setMinimumIntegerDigits(minDigits);
        setMaximumIntegerDigits(maxDigits);

        StringBuilder sb = new StringBuilder("^");
        if (allowNegative) {
            sb.append("[-]?");
        }
        sb.append("[0-9]");
        sb.append('{').append(minDigits).append(',').append(maxDigits).append('}');
        sb.append("$");

        pattern = Pattern.compile(sb.toString());
    }

    public IntegerNumberFormat(boolean allowNegative) {
        this(allowNegative, 1, 9);
    }

    protected String formatLong(long number) {
        return Long.valueOf(number).toString();
    }

    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        return format((long) number, toAppendTo, pos);
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        if (RecordWriter.isNull((int) number)) {
            return sbuff("");
        }
        return sbuff(formatLong(number));
    }

    @Override
    public Number parse(String source, ParsePosition parsePosition) {
        if (source == null || source.isEmpty()) {
            return Integer.valueOf(Constants.NUMBER_NOT_SET);
        } else if (source.length() > 0 && isValid(source)) {
            return Integer.parseInt(source, radix);
        }
        return null;
    }

}
