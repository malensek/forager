/*
Copyright (c) 2014, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

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
 * and synced to disk.  Each of the lists being managed are created on demand.
 *
 * @author malensek
 */
public class ListManager {

    private static final Logger logger = Logger.getLogger("forager");

    private static final String COMPLETED_EXT = ".done";
    private static final String FAILED_EXT = ".fail";

    private String taskListName;
    private String completedListName;
    private String failedListName;
    private boolean append;

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
        this.completedListName = taskListName + COMPLETED_EXT;
        this.failedListName = taskListName + FAILED_EXT;
        this.append = append;
    }

    private void createTaskWriter()
    throws IOException {
        taskListOut = new FileOutputStream(taskListName, append);
        taskListWriter = new PrintWriter(
                new BufferedOutputStream(taskListOut));
    }

    private void createCompletedWriter()
    throws IOException {
        String completedName = taskListName + COMPLETED_EXT;
        completedListOut = new FileOutputStream(completedName, append);
        completedListWriter = new PrintWriter(
                new BufferedOutputStream(completedListOut));
    }

    private void createFailedWriter()
    throws IOException {
        failedListOut = new FileOutputStream(failedListName, append);
        failedListWriter = new PrintWriter(
                new BufferedOutputStream(failedListOut));
    }

    public void addTask(String command)
    throws IOException {
        if (taskListWriter == null) {
            createTaskWriter();
        }

        taskListWriter.println(command);
    }

    public void addCompletedTask(String command)
    throws IOException {
        if (completedListWriter == null) {
            createCompletedWriter();
        }

        completedListWriter.println(command);
    }

    /**
     * Adds a task string to the failed task list. This operation syncs every
     * time to ensure we don't lose any tasks that failed.
     */
    public void addFailedTask(String command)
    throws IOException {
        if (failedListWriter == null) {
            createFailedWriter();
        }

        failedListWriter.println(command);
        failedListWriter.flush();
        failedListOut.getFD().sync();
    }

    /**
     * Syncs the task list to disk. This should be called after a set of
     * additions to the task list has completed. Once this method returns, the
     * transaction is complete.
     */
    public void syncTasks()
    throws IOException {
        taskListWriter.flush();
        taskListOut.getFD().sync();
    }

    /**
     * Syncs the completed list to disk. This should be called after a set of
     * additions to the completed list has finished. Once this method returns,
     * the transaction is complete.
     */
    public void syncCompleted()
    throws IOException {
        completedListWriter.flush();
        completedListOut.getFD().sync();
    }

    /**
     * Reads the master task list and then the list of completed tasks and
     * returns pending (incomplete) tasks.
     *
     * @return a List containing task strings that have not yet been completed.
     */
    public List<String> readPendingTasks()
    throws IOException {
        File taskFile = new File(this.taskListName);
        if (taskFile.exists() == false) {
            return new ArrayList<>();
        }

        List<String> allTasks = Files.readAllLines(
                Paths.get(this.taskListName), Charset.defaultCharset());

        File completedFile = new File(this.completedListName);
        if (completedFile.exists() == false) {
            return allTasks;
        }

        List<String> completedTasks = Files.readAllLines(
                Paths.get(this.taskListName + COMPLETED_EXT),
                Charset.defaultCharset());

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

    /**
     * Closes down the output streams for this ListManager.
     */
    public void shutdown() {
        taskListWriter.close();
        completedListWriter.close();
        failedListWriter.close();
    }
}
