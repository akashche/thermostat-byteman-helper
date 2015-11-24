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

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author akashche
 */
public class ThermostatUtilsTest {

    @Test
    public void testDefaultString() {
        assertEquals("null string fail", "", ThermostatUtils.defaultString(null));
        assertEquals("empty string fail", "", ThermostatUtils.defaultString(""));
        assertEquals("non-empty string fail", "foo", ThermostatUtils.defaultString("foo"));
    }

    @Test
    public void testSleep() {
        long start = System.currentTimeMillis();
        ThermostatUtils.sleep(100);
        assertTrue("sleep fail", System.currentTimeMillis() - start >= 100);
    }

    @Test
    public void testEscapeQuotes() {
        assertEquals("no escape fail", "foo", ThermostatUtils.escapeQuotes("foo"));
        assertEquals("single string fail", "fo\\\"o", ThermostatUtils.escapeQuotes("fo\"o"));
        assertEquals("double escape fail", "\\\"\\\"foo", ThermostatUtils.escapeQuotes("\"\"foo"));
    }

    @Test
    public void testToMap() {
        LinkedHashMap<String, Object> map = ThermostatUtils.toMap(new Object[]{"foo", "bar", "baz", 42, null, null});
        assertEquals("size fail", 3, map.size());
        assertEquals("data fail", "bar", map.get("foo"));
        assertEquals("data fail", 42, map.get("baz"));
        assertEquals("empty fail", null, map.get(""));
    }

    @Test
    public void testCreateTmpDir() throws IOException {
        File dir = null;
        try {
            dir = ThermostatUtils.createTmpDir(ThermostatUtilsTest.class);
            assertTrue("dir existence error", dir.isDirectory());
        } finally {
            if (null != dir) {
                dir.delete();
            }
        }

    }


}
