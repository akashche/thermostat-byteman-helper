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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;

import static com.redhat.thermostat.byteman.helper.ThermostatUtils.toMap;
import static java.lang.System.getProperty;

/**
 * Byteman helper that provides "send" method for sending records to Thermostat
 *
 * @author akashche
 */
public class ThermostatHelper {
    private static final String JVM_ID = ManagementFactory.getRuntimeMXBean().getName();
    private final String agentId;
    private final ThermostatTransport transport;

    /**
     * Constructor
     */
    public ThermostatHelper() {
        this.agentId = getProperty("thermostat.agent_id", "default");
        transport = createTransport();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                transport.close();
            }
        }));
    }

    public void send(String marker, String key, Object value) {
        send(marker, new Object[]{key, value});
    }

    public void send(String marker, String key1, Object value1, String key2, Object value2) {
        send(marker, new Object[]{key1, value1, key2, value2});
    }

    public void send(String marker, String key1, Object value1, String key2, Object value2,
                                  String key3, Object value3) {
        send(marker, new Object[]{key1, value1, key2, value2, key3, value3});
    }

    public void send(String marker, Object... dataArray) {
        LinkedHashMap<String, Object> data = toMap(dataArray);
        ThermostatRecord rec = new ThermostatRecord(System.currentTimeMillis(), JVM_ID, agentId, marker, data);
        transport.send(rec);
    }

    // todo: add support for choosing ThermostatUnixSocketTransport when its API will be stable
    private static ThermostatTransport createTransport() {
        int sendThreshold = Integer.parseInt(getProperty("thermostat.send_threshold", "2"));
        int loseThreshold = Integer.parseInt(getProperty("thermostat.lose_threshold", "1024"));
        String transport = getProperty("thermostat.transport", "json");
        File outDir = new File(getProperty("thermostat.json_out_directory", "."));
        String prefix = getProperty("thermostat.json_file_prefix", "");
        if ("json".equals(transport)) {
            return new ThermostatJsonFileTransport(sendThreshold, loseThreshold, outDir, prefix);
        } else {
            throw new UnsupportedOperationException("Invalid transport: [" + transport + "]");
        }
    }

}
