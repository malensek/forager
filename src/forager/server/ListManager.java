
package forager.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private FileOutputStream taskListOut;
    private PrintWriter taskListWriter;

    private FileOutputStream completedListOut;
    private PrintWriter completedListWriter;

    public ListManager(String taskListName)
    throws IOException {
        this(taskListName, false);
    }

    public ListManager(String taskListName, boolean clearExisting)
    throws IOException {
        boolean append = !(clearExisting);

        taskListOut = new FileOutputStream(taskListName, append);
        taskListWriter = new PrintWriter(
                new BufferedOutputStream(taskListOut));

        String completedName = taskListName + ".done";
        completedListOut = new FileOutputStream(completedName, append);
        completedListWriter = new PrintWriter(
                new BufferedOutputStream(completedListOut));
    }

    public void addTask(String command) {
        taskListWriter.println(command);
    }

    public void addCompletedTask(String command) {
        completedListWriter.println(command);
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

    public static boolean listsExist(String taskListName) {
        File listFile = new File(taskListName);
        File doneFile = new File(taskListName + COMPLETED_EXT);
        return (listFile.exists() || doneFile.exists());
    }

    public static List<String> getPendingTasks(String taskListName)
    throws IOException {
        List<String> allTasks = Files.readAllLines(
                Paths.get(taskListName), Charset.defaultCharset());
        List<String> completedTasks = Files.readAllLines(
                Paths.get(taskListName + COMPLETED_EXT));

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
