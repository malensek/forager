/*
Copyright (c) 2014, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package forager.client;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Monitors currently running tasks and decides when new tasks should be
 * requested based on current processing directives.
 *
 * @author malensek
 */
public class StatusMonitor implements Runnable {

    private static final Logger logger = Logger.getLogger("forager");

    /** Maximum number of concurrent tasks */
    int maxActive = 4;

    boolean online;
    private Forager clientInstance;

    public StatusMonitor(Forager clientInstance) {
        this.clientInstance = clientInstance;
        this.maxActive = clientInstance.getMaxTasks();
    }

    public void run() {
        online = true;

        while (online) {
            int active = clientInstance.getNumActive()
                + clientInstance.getNumPending();
            if (active < maxActive) {
                int numTasks = maxActive - active;
                try {
                    clientInstance.submitTaskRequest(numTasks);
                } catch (IOException e) {
                    System.out.println("Failed to submit task request!");
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                logger.fine("Waking up StatusMonitor thread");
            }
        }
    }
}
