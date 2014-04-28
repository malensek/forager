
package forager.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import forager.events.ForagerEventMap;
import forager.events.JoinEvent;

import galileo.event.Event;
import galileo.event.EventHandler;
import galileo.event.EventType;
import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.net.ServerMessageRouter;
import galileo.serialization.SerializationInputStream;

public class Overlord implements MessageListener {

    private static final Logger logger = Logger.getLogger("forager");

    private int port;
    private ServerMessageRouter messageRouter;

    private ForagerEventMap eventMap = new ForagerEventMap();
    private EventMapper eventMapper = new EventMapper(this, eventMap);

    public Overlord(int port) {
        this.port = port;
        eventMapper.linkEventHandlers();
    }

    public void start()
    throws IOException, Exception {
        messageRouter = new ServerMessageRouter(this.port);
        messageRouter.addListener(this);
        messageRouter.addListener(eventMapper);
        messageRouter.listen();
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
    public void processJoinEvent(JoinEvent join) {
        logger.log(Level.INFO, "Received join request: {0}", "xxx");
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
