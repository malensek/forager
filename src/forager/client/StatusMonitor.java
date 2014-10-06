package forager.client;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Monitors currently running tasks and decides when new tasks should be
 * requested based on current processing directives.
 *
 * @author malensek
 */
public class StatusMonitor implements Runnable {

    private static final Logger logger = Logger.getLogger("forager");

    /** Maximum number of concurrent tasks */
    int maxActive = 4;

    boolean online;
    private Forager clientInstance;

    public StatusMonitor(Forager clientInstance) {
        this.clientInstance = clientInstance;
        this.maxActive = clientInstance.getMaxTasks();
    }

    public void run() {
        online = true;

        while (online) {
            int active = clientInstance.getNumActive()
                + clientInstance.getNumPending();
            if (active < maxActive) {
                int numTasks = maxActive - active;
                try {
                    clientInstance.submitTaskRequest(numTasks);
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
