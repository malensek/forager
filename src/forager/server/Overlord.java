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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import forager.events.ForagerEventMap;
import forager.events.ImportRequest;
import forager.events.ImportResponse;
import forager.events.TaskCompletion;
import forager.events.TaskRequest;
import forager.events.TaskSpec;

import galileo.event.EventContext;
import galileo.event.EventHandler;
import galileo.event.EventReactor;
import galileo.net.ServerMessageRouter;

public class Overlord {

    private static final Logger logger = Logger.getLogger("forager");

    public static final int DEFAULT_PORT = 53380;

    private int port;
    private ServerMessageRouter messageRouter;

    private ForagerEventMap eventMap = new ForagerEventMap();
    private EventReactor eventReactor = new EventReactor(this, eventMap);

    private long taskSerial = 0;
    private long taskPointer = 0;
    private Map<Long, TaskSpec> tasks = new HashMap<>();

    public Overlord(int port) {
        this.port = port;
    }

    public void start()
    throws IOException, Exception {
        messageRouter = new ServerMessageRouter();
        messageRouter.addListener(eventReactor);
        messageRouter.listen(this.port);

        while (true) {
            eventReactor.processNextEvent();
        }
    }

    private void addTask(String command) {
        TaskSpec task = new TaskSpec(taskSerial++, command);
        tasks.put(task.taskId, task);
    }

    private TaskSpec getNextTask() {
        Iterator<Long> idIterator = tasks.keySet().iterator();

        if (idIterator.hasNext() != true) {
            /* We have no tasks to assign.  Return an 'idle' task */
            return new TaskSpec(-1, "");
        }

        Long id = idIterator.next();
        TaskSpec task = tasks.remove(id);
        activeTasks.put(id, task);
        return task;
    }

    @EventHandler
    public void processTaskRequest(TaskRequest request, EventContext context) {

        logger.log(Level.INFO, "{0} tasks requested by {1}",
                new Object[] { request.numTasks, context.getSource() });

        for (int i = 0; i < request.numTasks; ++i) {
            TaskSpec task = getNextTask();
            task.addAssignment(context.getSource());

            try {
                context.sendReply(task);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error assigning task.", e);
                task.removeAssignment(context.getSource());
            }
        }
    }

    @EventHandler
    public void processCompletedTask(
            TaskCompletion completedTask, EventContext context) {

        logger.log(Level.INFO, "Task {0} completed by {1}",
                new Object[] { completedTask.taskId, context.getSource() });

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
            context.sendReply(new ImportResponse(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
