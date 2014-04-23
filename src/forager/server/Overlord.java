
package forager.server;

import java.io.IOException;

import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.net.ServerMessageRouter;

public class Overlord implements MessageListener {

    private int port;
    private ServerMessageRouter messageRouter;

    public Overlord(int port) {
        this.port = port;
    }

    public void start()
    throws IOException {
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
    public void onMessage(GalileoMessage message) {
        System.out.println("Got a message: " + new String(message.getPayload()));

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
