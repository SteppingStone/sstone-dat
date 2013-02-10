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
public class FloatNumberFormat extends EmptyAllowedNumberFormat {

    private static final long serialVersionUID = 173235912559134527L;

    private final int precision;

    public FloatNumberFormat(int precision) {
        this(1, 9, 2);
    }

    public FloatNumberFormat(int minIntegerDigits, int maxIntegerDigits, int precision) {
        this.precision = precision;
        StringBuilder sb = new StringBuilder("^[0-9]");
        sb.append("{").append(minIntegerDigits).append(',').append(maxIntegerDigits).append('}');
        sb.append("$|^[0-9]");
        sb.append("{").append(minIntegerDigits).append(',').append(maxIntegerDigits).append('}');
        sb.append("\\.[0-9]");
        sb.append("{").append(1).append(',').append(precision).append('}');
        sb.append("$");
        this.pattern = Pattern.compile(sb.toString());
    }

    @Override
    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        if (RecordWriter.isNull(number)) {
            return new StringBuffer("");
        }
        return new StringBuffer(String.format("%." + precision + "f", number));
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        return format((double) number, toAppendTo, pos);
    }

    @Override
    public Number parse(String source, ParsePosition parsePosition) {
        if (source == null || source.isEmpty()) {
            return Float.valueOf((float) Constants.NUMBER_NOT_SET);
        } else if (isValid(source)) {
            return Float.parseFloat(source);
        }
        return null;
    }

}
