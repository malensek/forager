package forager.client;

import java.lang.ProcessBuilder.Redirect;

public class Task implements Runnable {

    private String[] command;
    private Forager forager;

    public Task(String[] command, Forager forager) {
        this.command = command;
        this.forager = forager;
    }

    public void run() {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);

            pb.redirectOutput(Redirect.INHERIT);
            pb.redirectError(Redirect.INHERIT);

            Process p = pb.start();
            p.waitFor();
            forager.finalizeTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
