
package forager.server;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import forager.events.ForagerEventMap;
import forager.events.JoinEvent;

import galileo.event.EventContext;
import galileo.event.EventHandler;
import galileo.event.EventReactor;
import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.net.ServerMessageRouter;

public class Overlord implements MessageListener {

    private static final Logger logger = Logger.getLogger("forager");

    private int port;
    private ServerMessageRouter messageRouter;

    private ForagerEventMap eventMap = new ForagerEventMap();
    private EventReactor eventReactor = new EventReactor(this, eventMap);

    public Overlord(int port) {
        this.port = port;
        eventReactor.linkEventHandlers();
    }

    public void start()
    throws IOException, Exception {
        messageRouter = new ServerMessageRouter(this.port);
        messageRouter.addListener(this);
        messageRouter.addListener(eventReactor);
        messageRouter.listen();

        while (true) {
            eventReactor.processNextEvent();
        }
    }

    @Override
    public void onConnect(NetworkDestination endpoint) {

    }

    @Override
    public void onDisconnect(NetworkDestination endpoint) {

    }

    @Override
    public void onMessage(GalileoMessage message) { }

    @EventHandler
    public void processJoinEvent(JoinEvent join, EventContext context) {
        logger.log(Level.INFO, "Received join request: {0}",
                context.getSource());
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
