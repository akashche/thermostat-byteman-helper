/*
 * Copyright 2012-2015 Red Hat, Inc.
 *
 * This file is part of Thermostat.
 *
 * Thermostat is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2, or (at your
 * option) any later version.
 *
 * Thermostat is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Thermostat; see the file COPYING.  If not see
 * <http://www.gnu.org/licenses/>.
 *
 * Linking this code with other modules is making a combined work
 * based on this code.  Thus, the terms and conditions of the GNU
 * General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this code give
 * you permission to link this code with independent modules to
 * produce an executable, regardless of the license terms of these
 * independent modules, and to copy and distribute the resulting
 * executable under terms of your choice, provided that you also
 * meet, for each linked independent module, the terms and conditions
 * of the license of that module.  An independent module is a module
 * which is not derived from or based on this code.  If you modify
 * this code, you may extend this exception to your version of the
 * library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */

package com.redhat.thermostat.byteman.helper;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author akashche
 * Date: 11/23/15
 */
class ThermostatUtils {
    private static final Pattern QUOTE_PATTERN = Pattern.compile("\"");

    /**
     * <p>Returns either the passed in String, or if the String is
     * <code>null</code>, the default string
     *
     * @param str  the String to check, may be null
     * @return the passed in String, or the empty string if it was <code>null</code>
     */
    static String defaultString(String str) {
        return str != null ? str : "";
    }

    static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new ThermostatException("Sleep fo millis: [" + millis +"] interrupted", e);
        }
    }

    static String escapeQuotes(String str) {
        if (null != str) {
            Matcher matcher = QUOTE_PATTERN.matcher(str);
            if (matcher.matches()) {
                return matcher.replaceAll("\\\"");
            }
        }
        return str;
    }

    static LinkedHashMap<String, Object> toMap(Object[] dataArray){
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        if (0 != dataArray.length % 2) throw new ThermostatException("Invalid odd elements count");
        for (int i = 0; i < dataArray.length; i += 2) {
            Object objKey = dataArray[i];
            if (null == objKey) {
                objKey = "";
            }
            map.put(objKey.toString(), dataArray[i + 1]);
        }
        return map;
    }
}
