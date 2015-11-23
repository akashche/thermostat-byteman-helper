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

import java.io.Closeable;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author akashche
 * Date: 11/23/15
 */
abstract class ThermostatTransport implements Closeable {
    // settings
    private final int sendThreshold;
    private final int loseThreshold;
    // state
    private AtomicBoolean sending = new AtomicBoolean(false);
    // cache
    private ArrayList<ThermostatRecord> cache = new ArrayList<>();
    private final Object cacheLock = new Object();
    private long lostCount = 0;
    // executor
    private final Executor executor = Executors.newSingleThreadExecutor();

    protected ThermostatTransport(int sendThreshold, int loseThreshold) {
        this.sendThreshold = sendThreshold;
        this.loseThreshold = loseThreshold;
    }

    protected abstract void transferToThermostat(ArrayList<ThermostatRecord> records);

    public void send(ThermostatRecord rec) {
        synchronized (cacheLock) {
            if (null != rec) {
                int size = cache.size();
                if (size < loseThreshold) {
                    cache.add(rec);
                    if (size >= sendThreshold) {
                        ArrayList<ThermostatRecord> records = cache;
                        cache = new ArrayList<>();
                        TransferTask task = new TransferTask(records);
                        executor.execute(task);
                    }
                } else {
                    lostCount += 1;
                }
            }
        }
    }

    @Override
    public void close() {
        synchronized (cacheLock) {
            ArrayList<ThermostatRecord> records = cache;
            cache = new ArrayList<>();
            TransferTask task = new TransferTask(records);
            executor.execute(task);
        }
    }

    private class TransferTask implements Runnable {
        private final ArrayList<ThermostatRecord> records;

        private TransferTask(ArrayList<ThermostatRecord> records) {
            this.records = records;
        }

        @Override
        public void run() {
            try {
                transferToThermostat(records);
            } catch (Exception e) {
                System.err.println("ERROR: Thermostat helper transfer data error:");
                e.printStackTrace();
            } finally {
                sending.set(false);
            }
        }
    }

}
