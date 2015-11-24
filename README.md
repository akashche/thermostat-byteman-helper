Byteman Helper for Thermostat
=============================

This project implements a Byteman Helper that can transfer records to the Thermostat storage.

Usage example
-------------

    java -javaagent:path/to/byteman.jar=script:path/to/rules.btm \
        -Dorg.jboss.byteman.verbose \
        -Dthermostat.agent_id=agent_orange \
        -Dthermostat.send_threshold=32 \
        -Dthermostat.lose_threshold=1024 \
        -Dthermostat.transport=<socket or json> \
        -Dthermostat.socket... \ # more transport props
        -cp path/to/helper.jar \
        -jar path/to/app.jar

License information
-------------------

This project is released under the [GNU General Public License, version 2](http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html).

Changelog
---------

**2015-11-24**

 * initial version
