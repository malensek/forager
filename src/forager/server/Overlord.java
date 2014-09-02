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
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import forager.events.ForagerEventMap;
import forager.events.TaskRequest;
import forager.events.TaskSpec;

import galileo.event.EventContext;
import galileo.event.EventHandler;
import galileo.event.EventReactor;
import galileo.net.ServerMessageRouter;

public class Overlord {

    private static final Logger logger = Logger.getLogger("forager");

    private int port;
    private ServerMessageRouter messageRouter;

    private ForagerEventMap eventMap = new ForagerEventMap();
    private EventReactor eventReactor = new EventReactor(this, eventMap);

    private Queue<String> taskList = new LinkedList<>();

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

    @EventHandler
    public void processTaskRequest(TaskRequest request, EventContext context) {

        logger.log(Level.INFO, "{0} tasks requested by {1}",
                new Object[] { request.numTasks, context.getSource() });

        if (taskList.size() == 0) {
            System.out.println("All tasks are complete!");
            System.out.println("Shutting down.");
            System.exit(0);
        }

        logger.log(Level.INFO, "{0} tasks remaining.", taskList.size());

        for (int i = 0; i < request.numTasks; ++i) {
            String taskString = taskList.remove();
            TaskSpec spec = new TaskSpec(taskString.split("\\s+"));
            try {
                context.sendReply(spec);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: forager.server.Overlord <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        Overlord overlord = new Overlord(port);
        overlord.start();
    }
}
