package forager.client;

import java.io.IOException;
import java.util.logging.Logger;

public class StatusMonitor implements Runnable {

    private static final Logger logger = Logger.getLogger("forager");

    int maxActive = 4;

    boolean online;

    private Forager forager;

    public StatusMonitor(Forager forager) {
        this.forager = forager;
        this.maxActive = forager.getMaxTasks();
    }

    public void run() {
        online = true;

        while (online) {
            int active = forager.getNumActive() + forager.getNumPending();
            if (active < maxActive) {
                int numTasks = maxActive - active;
                try {
                    forager.submitTaskRequest(numTasks);
                } catch (IOException e) {
                    System.out.println("Failed to submit task request!");
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                logger.fine("Waking up StatusMonitor thread");
            }
        }
    }
}
