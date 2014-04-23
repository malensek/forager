
package forager.client;

import java.io.IOException;

import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;

public class Forager implements MessageListener {

    private NetworkDestination overlord;
    private ClientMessageRouter messageRouter;
    private boolean once = false;

    public Forager(NetworkDestination overlord) {
        this.overlord = overlord;
    }

    public void start()
    throws IOException {
        messageRouter = new ClientMessageRouter();
        messageRouter.addListener(this);
        System.out.println("conn");
        messageRouter.connectTo(overlord);
        System.out.println("done");
        try {
        Thread.sleep(2000);
        } catch (InterruptedException e) { }
        System.out.println("sending1");
        messageRouter.sendMessage(overlord, new GalileoMessage("hello".getBytes()));
        try {
        Thread.sleep(2000);
        } catch (InterruptedException e) { }
        System.out.println("sending2");
        messageRouter.sendMessage(overlord, new GalileoMessage("hello!".getBytes()));
    }

    @Override
    public void onConnect(NetworkDestination endpoint) {

    }

    @Override
    public void onDisconnect(NetworkDestination endpoint) {
        System.out.println("x");
        if (once == false) {
            once = true;

            System.out.println("ooo");
            try {
                Thread.sleep(1000);
                //messageRouter = new ClientMessageRouter();
                messageRouter.connectTo(overlord);
                System.out.println("y");
        messageRouter.sendMessage(overlord, new GalileoMessage("hello".getBytes()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessage(GalileoMessage message) {

    }

    public static void main(String[] args)
    throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: forager.client.Forager <host> <port>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        NetworkDestination overlord = new NetworkDestination(host, port);

        Forager forager = new Forager(overlord);
        forager.start();
        System.out.println("done");
    }
}
