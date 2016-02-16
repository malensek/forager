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

package forager.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import forager.events.ForagerEventMap;
import forager.events.ImportRequest;
import forager.events.ImportResponse;
import forager.events.TaskCompletion;
import forager.events.TaskRequest;
import forager.events.TaskSpec;

import io.elssa.event.EventContext;
import io.elssa.event.EventHandler;
import io.elssa.event.EventLinkException;
import io.elssa.event.EventReactor;
import io.elssa.net.NetworkEndpoint;
import io.elssa.net.ServerMessageRouter;

public class Overlord {

    private static final Logger logger = Logger.getLogger("forager");

    private int port;
    private ServerMessageRouter messageRouter;

    private ForagerEventMap eventMap = new ForagerEventMap();
    private EventReactor eventReactor;

    private ListManager listManager;

    private long taskSerial = 0;
    private long completedTasks = 0;
    private long permanentFailures = 0;

    private Map<Long, TaskSpec> pendingTasks = new HashMap<>();
    private Map<Long, TaskSpec> activeTasks = new HashMap<>();

    public Overlord(int port, String taskList)
    throws EventLinkException, IOException {
        this(port, taskList, false);
    }

    public Overlord(int port, String taskList, boolean resetList)
    throws EventLinkException, IOException {
        this.port = port;
        this.eventReactor = new EventReactor(this, eventMap);

        boolean append = !(resetList);
        listManager = new ListManager(taskList, append);
        if (resetList == false) {
            /* Read pending tasks from the tasklist on disk. */
            List<String> diskPending = listManager.readPendingTasks();
            if (diskPending.size() > 0) {
                logger.log(Level.INFO, "Read {0} pending tasks from disk.",
                        diskPending.size());
            }

            for (String task : diskPending) {
                addTask(task, false);
            }
        }
    }

    public void start()
    throws IOException {
        messageRouter = new ServerMessageRouter();
        messageRouter.addListener(eventReactor);
        messageRouter.listen(this.port);

        logger.info("Forager server online.");

        while (true) {
            try {
                eventReactor.processNextEvent();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Event triggered an exception", e);
            }
        }
    }

    private void addTask(String command) {
        addTask(command, true);
    }

    private void addTask(String command, boolean writeToDisk) {
        long taskId = taskSerial++;
        TaskSpec task = new TaskSpec(taskId, command);
        pendingTasks.put(taskId, task);
        if (writeToDisk) {
            try {
                listManager.addTask(command);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not write task to disk!", e);
            }
        }
    }

    private TaskSpec getNextTask() {
        Iterator<Long> idIterator = pendingTasks.keySet().iterator();

        if (idIterator.hasNext() != true) {
            /* We have no tasks to assign.  Return an 'idle' task */
            return new TaskSpec(-1, "");
        }

        Long id = idIterator.next();
        return pendingTasks.get(id);
    }

    @EventHandler
    public void processTaskRequest(TaskRequest request, EventContext context) {

        Level logLevel = Level.INFO;
        if (pendingTasks.size() == 0) {
            logLevel = Level.FINE;
        }
        logger.log(logLevel, "{0} tasks requested by {1}",
                new Object[] { request.numTasks, context.getSource() });

        for (int i = 0; i < request.numTasks; ++i) {
            TaskSpec task = getNextTask();

            try {
                context.sendReply(task);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error assigning task.", e);
                return;
            }

            /* Once we've reached this point, we'll update our data structures.
             * This doesn't necessarily mean that the job has been successfully
             * deployed to the client, but we'll assume so for now. */
            pendingTasks.remove(task.taskId);
            activeTasks.put(task.taskId, task);
            task.assignments.add(context.getSource());
        }
    }

    @EventHandler
    public void processCompletedTask(
            TaskCompletion completion, EventContext context)
    throws IOException {

        if (completion.exitCode != 0) {
            processTaskFailure(completion, context);
            return;
        }

        logger.log(Level.INFO, "Task {0} completed by {1}",
                new Object[] { completion.taskId, context.getSource() });

        completedTasks++;

        logger.log(Level.INFO, "{0} tasks remaining.", tasksRemaining());

        TaskSpec task = activeTasks.remove(completion.taskId);
        listManager.addCompletedTask(task.command);
        listManager.syncCompleted();
    }

    private void processTaskFailure(
            TaskCompletion completion, EventContext context) {

        logger.log(Level.WARNING, "Task {0} FAILED at {1}",
                new Object[] { completion.taskId, context.getSource() });

        TaskSpec task = activeTasks.remove(completion.taskId);
        if (task.assignments.size() > 3) {
            permanentFailures++;
            Iterator<NetworkEndpoint> it = task.assignments.iterator();
            logger.log(Level.SEVERE,
                    "Task {0} failed 3 times at {1}; {2}; {3}",
                    new Object[] {
                        task.taskId, it.next(), it.next(), it.next() });
            logger.log(Level.SEVERE, "Marking task {0} as permanently failed.",
                    task.taskId);

            try {
                listManager.addFailedTask(task.command);
            } catch (IOException e) {
                logger.log(Level.WARNING,
                        "Could not log failed task to disk.", e);
            }
        } else {
            pendingTasks.put(task.taskId, task);
        }
    }

    @EventHandler
    public void processImportRequest(
            ImportRequest request, EventContext context) {

        logger.log(Level.INFO, "Importing {0} tasks submitted by {1}",
                new Object[] { request.tasks.size(), context.getSource() });

        for (String command : request.tasks) {
            addTask(command);
        }

        try {
            listManager.syncTasks();
            context.sendReply(new ImportResponse(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long tasksRemaining() {
        return taskSerial - completedTasks - permanentFailures;
    }
}
