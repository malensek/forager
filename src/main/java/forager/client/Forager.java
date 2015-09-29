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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import forager.events.ForagerEventMap;
import forager.events.TaskCompletion;
import forager.events.TaskRequest;
import forager.events.TaskSpec;

import galileo.event.EventContext;
import galileo.event.EventHandler;
import galileo.event.EventReactor;
import galileo.net.ClientMessageRouter;
import galileo.net.NetworkDestination;

/**
 * Implements a Forager client daemon. The daemon is responsible for requesting
 * tasks when processing directives are met, monitoring their execution, and
 * notifying the server when they are complete.
 *
 * @author malensek
 */
public class Forager {

    private static final Logger logger = Logger.getLogger("forager");

    private NetworkDestination server;
    private ClientMessageRouter messageRouter;

    private ForagerEventMap eventMap = new ForagerEventMap();
    private EventReactor eventReactor = new EventReactor(this, eventMap);

    private StatusMonitor monitor;
    private Thread monitorThread;

    private int maxTasks = 0;
    private int activeTasks = 0;
    private int pendingRequests = 0;
    protected ExecutorService threadPool;

    public Forager(NetworkDestination server, int threads) {
        this.server = server;
        this.maxTasks = threads;
        this.threadPool = Executors.newFixedThreadPool(threads);
    }

    public void start()
    throws Exception {
        messageRouter = new ClientMessageRouter();
        messageRouter.addListener(eventReactor);

        /* Start the monitor thread */
        monitor = new StatusMonitor(this);
        monitorThread = new Thread(monitor);
        monitorThread.start();

        while (true) {
            eventReactor.processNextEvent();
        }
    }

    /**
     * Requests a number of tasks from the server. Note that the server is not
     * required to supply the exact number of tasks requested, and in some cases
     * may respond with no tasks.
     *
     * @param numTasks the number of individual tasks to request from the
     * server.
     */
    protected synchronized void submitTaskRequest(int numTasks)
    throws IOException {
        TaskRequest tr = new TaskRequest(numTasks);
        pendingRequests += numTasks;
        messageRouter.sendMessage(server, eventReactor.wrapEvent(tr));
    }

    /**
     * This method is called by task threads to indicate a task completion,
     * which also involves notifying the server of the task exit status.
     */
    protected synchronized void finalizeTask(TaskSpec task, int exitCode)
    throws IOException {
        if (exitCode == 0) {
            logger.log(Level.INFO,
                    "Received successful task completion notification: {0}",
                    task);
        } else {
            logger.log(Level.WARNING, "Task exited with error code: {0}",
                    exitCode);
        }
        activeTasks--;

        TaskCompletion completion = new TaskCompletion(task.taskId, exitCode);
        messageRouter.sendMessage(server, eventReactor.wrapEvent(completion));
        monitorThread.interrupt();
    }

    /**
     * Retrieves the number of active (executing) tasks.
     */
    protected synchronized int getNumActive() {
        return activeTasks;
    }

    /**
     * Retrieves the number of pending task requests that have not yet been
     * fulfilled by the server.
     */
    protected synchronized int getNumPending() {
        return pendingRequests;
    }

    protected int getMaxTasks() {
        return maxTasks;
    }

    @EventHandler
    public void processTaskSpec(TaskSpec taskSpec, EventContext context) {
        pendingRequests--;

        if (taskSpec.taskId == -1) {
            logger.log(Level.FINE, "Received idle task");
            return;
        }

        logger.log(Level.INFO, "Starting task: {0}", taskSpec);
        activeTasks++;
        TaskThread thread = new TaskThread(taskSpec, this);
        threadPool.submit(thread);
    }
}
