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

import com.redhat.thermostat.experimental.ThermostatLocalSocketChannel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Transport implementation that sends records to the Thermostat agent over
 * Thermostat local IPC
 *
 * @author akashche
 */
class ThermostatUnixSocketTransport extends ThermostatTransport {
    private final File socket;
    private final int batchSize;
    private final int attempts;
    private final long breakIntervalMillis;
    private final ThermostatLocalSocketChannel channel;

    /**
     * Constructor
     *
     * @param sendThreshold min number of records to cache before sending
     * @param loseThreshold max number of packages to cache
     * @param socket path to socket
     * @param batchSize number of records to send at once
     * @param attempts number of send attempts to repeat in case of error
     * @param breakIntervalMillis number of milliseconds to wait between the attempts
     */
    protected ThermostatUnixSocketTransport(int sendThreshold, int loseThreshold, File socket, int batchSize, int attempts, long breakIntervalMillis) {
        super(sendThreshold, loseThreshold);
        this.batchSize = batchSize;
        this.attempts = attempts;
        this.breakIntervalMillis = breakIntervalMillis;
        if (!socket.exists()) {
            throw new ThermostatException("Specified Thermostat socket: [" + socket + "] doesn't exist");
        }
        this.socket = socket;
        try {
            this.channel = ThermostatLocalSocketChannel.open((socket));
        } catch (Exception e) {
            throw new ThermostatException("Error opening Thermostat socket: [" + socket.getAbsolutePath() + "]", e);
        }
    }

    /**
     * Sends specified records o the Thermostat agent over
     * Thermostat local IPC
     *
     * @param records records to transfer
     */
    @Override
    protected void transferToThermostat(ArrayList<ThermostatRecord> records) {
        List<Exception> exList = new ArrayList<>();
        for (int i = 0; i < attempts; i++) {
            try {
                tryToWrite(records);
                return;
            } catch (Exception e) {
                ThermostatUtils.sleep(breakIntervalMillis);
                exList.add(e);
            }
        }
        System.err.println("ERROR: Error sending data to Thermostat socket: [" + socket.getAbsolutePath() + "]," +
                " attempts count: [" + attempts + "], breakIntervalMillis: [" + breakIntervalMillis + "]");
        for (Exception ex : exList) {
            ex.printStackTrace();
        }
    }

    /**
     * Sends cached records and closes the channel
     */
    @Override
    public void close() {
        super.close();
        try {
            channel.close();
        } catch (IOException e) {
            System.err.println("WARNING: Thermostat error closing socket channel: [" + socket.getAbsolutePath() + "]");
            e.printStackTrace();
        }
    }

    private void tryToWrite(ArrayList<ThermostatRecord> records) throws IOException {
        ArrayList<ThermostatRecord> batch = new ArrayList<>(batchSize);
        for (ThermostatRecord re : records) {
            batch.add(re);
            if (batchSize == batch.size()) {
                writeBatch(batch);
                batch.clear();
            }
        }
        if (batch.size() > 0) {
            writeBatch(batch);
        }
    }

    private void writeBatch(ArrayList<ThermostatRecord> records) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        boolean first = true;
        for (ThermostatRecord rec : records) {
            if (!first) {
                sb.append(",\n");
            } else {
                first = false;
            }
            sb.append(rec.toJson());
        }
        sb.append("\n]");
        ByteBuffer envelope = ByteBuffer.wrap(sb.toString().getBytes(Charset.forName("UTF-8")));
        channel.write(envelope);
    }


}
