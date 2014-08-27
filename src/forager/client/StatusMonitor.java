package forager.client;

import java.io.IOException;

public class StatusMonitor implements Runnable {

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
                Thread.sleep(10000);
            } catch (InterruptedException e) { }
        }
    }
}
