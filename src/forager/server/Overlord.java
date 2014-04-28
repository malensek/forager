
package forager.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.channels.SocketChannel;

import forager.events.ForagerEventType;
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
        messageRouter = new ServerMessageRouter(3333);
        messageRouter.addListener(this);
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

        System.out.println("hello world!");
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
