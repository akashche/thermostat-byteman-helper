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
import java.util.Map;

import static com.redhat.thermostat.byteman.helper.ThermostatUtils.defaultString;
import static com.redhat.thermostat.byteman.helper.ThermostatUtils.escapeQuotes;

/**
 * Generic record for data collected for Thermostat
 *
 * @author akashche
 * Date: 11/23/15
 */
class ThermostatRecord {
    private long timestamp;
    private String vmId;
    private String agentId;
    private String marker;
    private LinkedHashMap<String, Object> data;

    /**
     * Constructor
     *
     * @param timestamp timestamp
     * @param vmId JVM ID
     * @param agentId Agent ID
     * @param marker marker value
     * @param data arbitrary data
     */
    public ThermostatRecord(long timestamp, String vmId, String agentId, String marker, LinkedHashMap<String, Object> data) {
        this.timestamp = timestamp;
        this.vmId = defaultString(vmId);
        this.agentId = defaultString(agentId);
        this.marker = defaultString(marker);
        this.data = null != data ? data : new LinkedHashMap<String, Object>();
    }

    /**
     * Timestamp accessor
     *
     * @return timestamp field
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * VM ID accessor
     *
     * @return VM ID
     */
    public String getVmId() {
        return vmId;
    }

    /**
     * Agent ID accessor
     *
     * @return Agent ID
     */
    public String getAgentId() {
        return agentId;
    }

    /**
     * Marker accessor
     *
     * @return  market field
     */
    public String getMarker() {
        return marker;
    }

    /**
     * Data accessor
     *
     * @return data field
     */
    public LinkedHashMap<String, Object> getData() {
        return data;
    }

    /**
     * Converts this record to JSON string
     *
     * @return JSON string
     */
    String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("    \"timestamp\": ").append(Long.toString(timestamp)).append(",\n");
        sb.append("    \"vmId\": \"").append(agentId).append("\",\n");
        sb.append("    \"agentId\": \"").append(agentId).append("\",\n");
        sb.append("    \"marker\": \"").append(marker).append("\",\n");
        sb.append("    \"data\": {\n");
        boolean first = true;
        for (Map.Entry<String, Object> en : data.entrySet()) {
            if (!first) {
                sb.append(",\n");
            } else {
                first = false;
            }
            sb.append("        \"").append(en.getKey()).append("\": ").append(toJsonValue(en.getValue()));
        }
        sb.append("\n    }\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJsonValue(Object valObj) {
        if (null == valObj) {
            return "null";
        } else if (valObj instanceof String) {
            String valStr = (String) valObj;
            return "\"" + escapeQuotes(valStr) + "\"";
        } else if (Number.class.isAssignableFrom(valObj.getClass()) ||
                Boolean.class.isAssignableFrom(valObj.getClass())) {
            return valObj.toString();
        } else {
            return "\"" + escapeQuotes(valObj.toString()) + "\"";
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ThermostatRecord");
        sb.append("{timestamp=").append(timestamp);
        sb.append(", vmId='").append(vmId).append('\'');
        sb.append(", agentId='").append(agentId).append('\'');
        sb.append(", marker='").append(marker).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
