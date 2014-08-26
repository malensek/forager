package forager.client;

public class Task implements Runnable {

    private String[] command;

    public Task(String[] command) {
        this.command = command;
    }

    public void run() {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
