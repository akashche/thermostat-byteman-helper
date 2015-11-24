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

import java.util.Arrays;

/**
 * @author akashche
 */
public class ThermostatHelperTest {
    @Test
    public void test() throws Exception {
        ProcessBuilder builder = new ProcessBuilder(Arrays.asList(
                "java",
                "-javaagent:./target/byteman-3.0.2.jar=script:./src/test/resources/01.btm",
                "-Dorg.jboss.byteman.verbose",
                "-Dthermostat.agent_id=agent_orange",
                "-Dthermostat.send_threshold=2",
                "-Dthermostat.lose_threshold=1024",
                "-Dthermostat.transport=json",
                "-Dthermostat.json_out_directory=./target",
                "-Dthermostat.json_file_prefix=thermostat_",
//                "-Dthermostat.transport=socket",
//                "-Dthermostat.socket_path=/tmp/thermostat-socket",
                "-cp",
                "./target/test-classes:" +
                        "./target/classes:" +
                        "./target/jnr-unix-socket-test-0.0.1.jar",
//                        "./target/jnr-unixsocket-0.8.jar:" +
//                        "./target/jnr-ffi-2.0.3.jar:" +
//                        "./target/jffi-1.2.9.jar",
                "com.redhat.thermostat.byteman.helper.support.TestApp"
                ));
        builder.inheritIO();
        Process process = builder.start();
        process.waitFor();
    }
}
