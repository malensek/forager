
package forager.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the active and completed task lists and allows them to be flushed
 * and synced to disk.
 *
 * @author malensek
 */
public class ListManager {

    private static final Logger logger = Logger.getLogger("forager");

    private static final String COMPLETED_EXT = ".done";
    private static final String FAILED_EXT = ".fail";

    private String taskListName;

    private FileOutputStream taskListOut;
    private PrintWriter taskListWriter;

    private FileOutputStream completedListOut;
    private PrintWriter completedListWriter;

    private FileOutputStream failedListOut;
    private PrintWriter failedListWriter;

    public ListManager(String taskListName)
    throws IOException {
        this(taskListName, true);
    }

    public ListManager(String taskListName, boolean append)
    throws IOException {
        this.taskListName = taskListName;

        taskListOut = new FileOutputStream(taskListName, append);
        taskListWriter = new PrintWriter(
                new BufferedOutputStream(taskListOut));

        String completedName = taskListName + COMPLETED_EXT;
        completedListOut = new FileOutputStream(completedName, append);
        completedListWriter = new PrintWriter(
                new BufferedOutputStream(completedListOut));
    }

    public void addTask(String command) {
    private void createFailedWriter()
    throws IOException {
        String failedName = taskListName + FAILED_EXT;
        failedListOut = new FileOutputStream(failedName, append);
        failedListWriter = new PrintWriter(
                new BufferedOutputStream(failedListOut));
    }
        taskListWriter.println(command);
    }

    public void addCompletedTask(String command) {
        completedListWriter.println(command);
    }

    public void addFailedTask(String command)
    throws IOException {
        if (failedListWriter == null) {
            createFailedWriter();
        }

        failedListWriter.println(command);
        failedListWriter.flush();
        failedListOut.getFD().sync();
    }

    public void syncTasks()
    throws IOException {
        taskListWriter.flush();
        taskListOut.getFD().sync();
    }

    public void syncCompleted()
    throws IOException {
        completedListWriter.flush();
        completedListOut.getFD().sync();
    }

    public boolean listsExist() {
        File listFile = new File(this.taskListName);
        File doneFile = new File(this.taskListName + COMPLETED_EXT);
        return (listFile.exists() || doneFile.exists());
    }

    public List<String> readPendingTasks()
    throws IOException {
        if (listsExist() != true) {
            return new ArrayList<>();
        }

        List<String> allTasks = Files.readAllLines(
                Paths.get(this.taskListName), Charset.defaultCharset());
        List<String> completedTasks = Files.readAllLines(
                Paths.get(this.taskListName + COMPLETED_EXT));

        for (String command : completedTasks) {
            boolean result = allTasks.remove(command);
            if (result == false) {
                logger.log(Level.WARNING,
                        "Completed task not found in master task list: {0}",
                        command);
            }
        }

        return allTasks;
    }
}
