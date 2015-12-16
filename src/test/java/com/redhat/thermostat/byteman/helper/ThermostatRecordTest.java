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

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

/**
 * @author akashche
 */
public class ThermostatRecordTest {
    @Test
    public void testToJson() {
        ThermostatRecord recEmptyData = new ThermostatRecord(42, "foo", "bar", "baz", null);
        assertEquals("empty data fail",
                "{\n" +
                "    \"timestamp\": 42,\n" +
                "    \"vmId\": \"foo\",\n" +
                "    \"agentId\": \"bar\",\n" +
                "    \"marker\": \"baz\",\n" +
                "    \"data\": {\n" +
                "\n    }\n" +
                "}", recEmptyData.toJson());

        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("foo1", "ba\"r1");
        data.put("foo2", 42);
        data.put("foo3", 42.000);
        data.put("foo4", false);
        data.put("foo5", new ArrayList<String>());
        ThermostatRecord recData = new ThermostatRecord(42, "foo", "bar", "baz", data);
        assertEquals("data fail",
                "{\n" +
                "    \"timestamp\": 42,\n" +
                "    \"vmId\": \"foo\",\n" +
                "    \"agentId\": \"bar\",\n" +
                "    \"marker\": \"baz\",\n" +
                "    \"data\": {\n" +
                "        \"foo1\": \"ba\\\"r1\",\n" +
                "        \"foo2\": 42,\n" +
                "        \"foo3\": 42.0,\n" +
                "        \"foo4\": false,\n" +
                "        \"foo5\": \"[]\"\n" +
                "    }\n" +
                "}", recData.toJson());
    }
}
