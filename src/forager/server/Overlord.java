
package forager.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import forager.events.ForagerEventMap;
import forager.events.JoinEvent;
import forager.events.TaskRequest;
import forager.events.TaskSpec;

import galileo.event.EventContext;
import galileo.event.EventHandler;
import galileo.event.EventReactor;
import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.net.ServerMessageRouter;

public class Overlord {

    private static final Logger logger = Logger.getLogger("forager");

    private int port;
    private ServerMessageRouter messageRouter;

    private ForagerEventMap eventMap = new ForagerEventMap();
    private EventReactor eventReactor = new EventReactor(this, eventMap);

    private Queue<String> taskList = new LinkedList<>();

    public Overlord(int port, int startNum) {
        for (int i = startNum; i <= 10000; ++i) {
            String cmd = "naader texas2 " + i;
            taskList.add(cmd);
        }
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
    public void processJoinEvent(JoinEvent join, EventContext context) {
        logger.log(Level.INFO, "Received join request: {0}",
                context.getSource());
    }

    @EventHandler
    public void processTaskRequest(TaskRequest request, EventContext context) {

        System.out.println(request.numTasks + " tasks requested by "
                + context.getSource());

        if (taskList.size() == 0) {
            System.out.println("All tasks are complete!");
            System.exit(0);
        }

        System.out.println(taskList.size() + " tasks remaining.");

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
        if (args.length < 2) {
            System.out.println("Usage: forager.server.Overlord <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        int startNum = Integer.parseInt(args[1]);

        Overlord overlord = new Overlord(port, startNum);
        overlord.start();
    }
}
