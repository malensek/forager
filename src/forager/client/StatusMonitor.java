package forager.client;

import java.io.IOException;

public class StatusMonitor implements Runnable {

    int maxActive = 4;

    boolean online;

    private Forager forager;

    public StatusMonitor(Forager forager) {
        this.forager = forager;
    }

    public void run() {
        online = true;

        while (online) {
            int active = forager.threadPool.getActiveCount();
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
                Thread.sleep(5000);
            } catch (InterruptedException e) { }
        }
    }
}
