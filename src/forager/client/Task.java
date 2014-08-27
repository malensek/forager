package forager.client;

import java.lang.ProcessBuilder.Redirect;

public class Task implements Runnable {

    private String[] command;

    public Task(String[] command) {
        this.command = command;
    }

    public void run() {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);

            pb.redirectOutput(Redirect.INHERIT);
            pb.redirectError(Redirect.INHERIT);

            Process p = pb.start();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
