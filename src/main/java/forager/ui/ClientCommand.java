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
import java.util.List;

import forager.client.Forager;

import galileo.net.NetworkDestination;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * Handles launching a Forager client from the command line interface.
 *
 * @author malensek
 */
public class ClientCommand implements Command {

    private OptionParser parser = new OptionParser();

    private OptionSpec<Integer> port;
    private OptionSpec<Integer> threads;

    public ClientCommand() {
        port = parser.acceptsAll(
                Arrays.asList("p", "port"), "Server port")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(ServerCommand.DEFAULT_PORT);

        int cpus = Runtime.getRuntime().availableProcessors();
        threads = parser.acceptsAll(
                Arrays.asList("t", "threads"),
                "Maximum number of threads the forager daemon can use")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(cpus);

        parser.nonOptions("server_hostname");
    }

    @Override
    public int execute(String[] args) throws Exception {
        OptionSet opts = parser.parse(args);
        List<?> nonOpts = opts.nonOptionArguments();

        if (nonOpts.size() <= 0) {
            System.out.println("No server hostname specified!");
            printUsage();
            return 1;
        }

        if (nonOpts.size() > 1) {
            for (int i = 1; i < nonOpts.size(); ++i) {
                System.out.println("Ignoring extra hostname parameter: "
                        + nonOpts.get(i));
            }
        }

        String hostname = (String) nonOpts.get(0);
        NetworkDestination server = new NetworkDestination(
                hostname, port.value(opts));
        Forager client = new Forager(server, threads.value(opts));
        client.start();
        return 0;
    }

    @Override
    public String name() {
        return "client";
    }

    @Override
    public String description() {
        return "Starts a forager client";
    }

    @Override
    public void printUsage()
    throws IOException {
        System.out.println("Usage: " + Launcher.APP_NAME + " " + name()
                + " [options] server_hostname");
        System.out.println(description());
        System.out.println();
        parser.printHelpOn(System.out);
    }
}
