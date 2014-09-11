
package forager.server;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ListManager {

    public static final String DEFAULT_LIST_NAME = "tasklist";

    private FileOutputStream taskListOut;
    private PrintWriter taskListWriter;

    private FileOutputStream completedListOut;
    private PrintWriter completedListWriter;

    public ListManager() throws IOException {
        this(DEFAULT_LIST_NAME);
    }

    public ListManager(String taskListName) throws IOException {
        taskListOut = new FileOutputStream(taskListName, true);
        taskListWriter = new PrintWriter(
                new BufferedOutputStream(taskListOut));

        String completedName = taskListName + ".done";
        completedListOut = new FileOutputStream(completedName, true);
        completedListWriter = new PrintWriter(
                new BufferedOutputStream(completedListOut));
    }

    public void addTask(String command) {
        taskListWriter.println(command);
    }

    public void addCompletedTask(String command) {
        completedListWriter.println(command);
    }

    public void sync() throws IOException {
        taskListWriter.flush();
        taskListOut.getFD().sync();
    }
}
