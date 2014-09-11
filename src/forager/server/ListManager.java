
package forager.server;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ListManager {

    private FileOutputStream taskListOut;
    private PrintWriter taskListWriter;

    public ListManager() throws IOException {
        String taskList = "tasklist";
        taskListOut = new FileOutputStream(taskList, true);
        taskListWriter = new PrintWriter(new BufferedOutputStream(taskListOut));
    }

    public void addTask(String command) {
        taskListWriter.println(command);
    }

    public void sync() throws IOException {
        taskListWriter.flush();
        taskListOut.getFD().sync();
    }
}
