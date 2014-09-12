
package forager.server;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Handles the active and completed task lists and allows them to be flushed
 * and synced to disk.
 *
 * @author malensek
 */
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

    public static boolean listsExist() {
        return listsExist(DEFAULT_LIST_NAME);
    }

    public static boolean listsExist(String taskListName) {
        File listFile = new File(taskListName);
        File doneFile = new File(taskListName + ".done");
        return (listFile.exists() || doneFile.exists());
    }
}
