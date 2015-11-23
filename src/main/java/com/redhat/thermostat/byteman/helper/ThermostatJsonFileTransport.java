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

import java.io.*;
import java.util.ArrayList;

import static com.redhat.thermostat.byteman.helper.ThermostatUtils.defaultString;

/**
 * @author akashche
 * Date: 11/23/15
 */
class ThermostatJsonFileTransport extends ThermostatTransport {
    private final File directory;
    private final String prefix;

    protected ThermostatJsonFileTransport(int sendThreshold, int loseThreshold, File directory, String prefix) {
        super(sendThreshold, loseThreshold);
        if (!directory.isDirectory()) {
            boolean success = directory.mkdir();
            if (!success) {
                throw new ThermostatException("Cannot initialize JSON transport using output directory: [" + directory + "]");
            }
        }
        this.directory = directory;
        this.prefix = defaultString(prefix);
    }

    @Override
    protected void transferToThermostat(ArrayList<ThermostatRecord> records) {
        File file = new File(directory, prefix + System.currentTimeMillis() + ".json");
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            writer.write("[\n");
            boolean first = true;
            for (ThermostatRecord rec : records) {
                if (!first) {
                    writer.write(",\n");
                } else {
                    first = false;
                }
                writer.write(rec.toJson());
            }
            writer.write("]");
        } catch (Exception e) {
            System.err.println("ERROR: error saving Thermostat records to JSON file: [" + file.getAbsolutePath() + "]");
            e.printStackTrace();
        }
    }
}
