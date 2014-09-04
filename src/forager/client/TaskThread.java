package forager.client;

import java.lang.ProcessBuilder.Redirect;

import forager.events.TaskSpec;

public class TaskThread implements Runnable {

    private TaskSpec task;
    private Forager forager;

    public TaskThread(TaskSpec task, Forager forager) {
        this.task = task;
        this.forager = forager;
    }

    public void run() {
        try {
            String[] command = task.command.split("\\s+");
            ProcessBuilder pb = new ProcessBuilder(command);

            pb.redirectOutput(Redirect.INHERIT);
            pb.redirectError(Redirect.INHERIT);

            Process p = pb.start();
            int i = p.waitFor();

            forager.finalizeTask(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
