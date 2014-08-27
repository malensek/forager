
package forager.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import forager.events.ForagerEventMap;
import forager.events.Job;
import forager.events.JoinEvent;

import galileo.event.EventHandler;
import galileo.event.EventReactor;
import galileo.net.ClientMessageRouter;
import galileo.net.NetworkDestination;

public class Forager {

    private NetworkDestination server;
    private ClientMessageRouter messageRouter;

    private ForagerEventMap eventMap = new ForagerEventMap();
    private EventReactor eventReactor = new EventReactor(this, eventMap);

    private ExecutorService threadPool;

    public Forager(NetworkDestination server) {
        this(server, 4);
    }

    public Forager(NetworkDestination server, int threads) {
        this.server = server;
        this.threadPool = Executors.newFixedThreadPool(threads);
    }

    public void start()
    throws Exception {
        messageRouter = new ClientMessageRouter();
        messageRouter.addListener(eventReactor);

        /* Join the network */
        JoinEvent join = new JoinEvent();
        messageRouter.sendMessage(server, eventReactor.wrapEvent(join));

        while (true) {
            eventReactor.processNextEvent();
        }
    }

    protected synchronized void submitTaskRequest(int numTasks)
    throws IOException {
        TaskRequest tr = new TaskRequest(numTasks);
        messageRouter.sendMessage(server, eventReactor.wrapEvent(tr));
    }

    @EventHandler
    private void startJob(Job job) {
        System.out.println("Starting job: " + job);
        //threadPool.submit();
    }

    public static void main(String[] args)
    throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: forager.client.Forager <host> <port>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        NetworkDestination overlord = new NetworkDestination(host, port);

        Forager forager = new Forager(overlord);
        forager.start();
    }
}
