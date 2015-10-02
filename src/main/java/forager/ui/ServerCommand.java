/*
Copyright (c) 2015, Colorado State University
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

package forager.ui;

import java.io.IOException;
import java.util.Arrays;

import forager.server.Overlord;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * Launches a Forager master server. The server application manages task
 * execution over the cluster.
 *
 * @author malensek
 */
public class ServerCommand implements Command {

    public static int DEFAULT_PORT = 53380;

    private OptionParser parser = new OptionParser();

    private OptionSpec<Integer> port;
    private OptionSpec<?> reset;
    private OptionSpec<String> taskList;

    public ServerCommand() {
        port = parser.acceptsAll(
                Arrays.asList("p", "port"), "Server port")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(DEFAULT_PORT);

        reset = parser.acceptsAll(
                Arrays.asList("r", "reset"), "Reset (clear) the task list");

        taskList = parser.acceptsAll(
                Arrays.asList("t", "tasklist", "task-list"),
                "Path to the task list (created if it doesn't exist)")
            .withRequiredArg()
            .ofType(String.class)
            .defaultsTo("./tasklist");
    }

    @Override
    public int execute(String[] args) throws Exception {
        OptionSet opts = parser.parse(args);
        boolean clear = opts.hasArgument(reset);
        Overlord server = new Overlord(
                port.value(opts), taskList.value(opts), clear);
        server.start();
        return 0;
    }

    @Override
    public String name() {
        return "server";
    }

    @Override
    public String description() {
        return "Starts a forager master server";
    }

    @Override
    public void printUsage()
    throws IOException {
        System.out.println("Usage: " + Launcher.APP_NAME + " "
                + name() + " [options]");
        System.out.println(description());
        System.out.println();
        parser.printHelpOn(System.out);
    }
}
