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
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import forager.client.Importer;

import io.elssa.net.NetworkEndpoint;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * Handles task importing activities; users provide the importer with a list of
 * tasks that need to be run by the system.
 *
 * @author malensek
 */
public class ImportCommand implements Command {

    private OptionParser parser = new OptionParser();

    private OptionSpec<Integer> port;
    private OptionSpec<String> server;

    public ImportCommand() {
        port = parser.acceptsAll(
                Arrays.asList("p", "port"), "Server port")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(ServerCommand.DEFAULT_PORT);

        server = parser.acceptsAll(
                Arrays.asList("s", "server"),
                "Server to submit imported tasks to")
            .withRequiredArg()
            .ofType(String.class)
            .defaultsTo("localhost");

        parser.nonOptions("files");
    }

    @Override
    public int execute(String[] args) throws Exception {
        OptionSet opts = parser.parse(args);
        List<?> files = opts.nonOptionArguments();

        Importer i = new Importer();
        if (files.size() > 0) {
            for (Object file : files) {
                int numTasks = i.addTaskFile((String) file);
                System.out.println("Importing " + numTasks + " tasks "
                        + "from file: " + ((String) file));
            }
        } else if (System.console() == null) {
            /* Something is being piped in */
            InputStreamReader reader = new InputStreamReader(System.in);
            int numTasks = i.addFromInputStream(reader);
            System.out.println("Importing " + numTasks + " tasks "
                    + "from stdin.");
        } else {
            throw new Exception("No input files specified or data from stdin.");
        }

        boolean result = i.submitTasks(new NetworkEndpoint(
                    server.value(opts), port.value(opts)));

        if (result == true) {
            System.out.println("Import completed successfully!");
            return 0;
        } else {
            System.out.println("Import rejected by the server.");
            return 1;
        }
    }

    @Override
    public String name() {
        return "import";
    }

    @Override
    public String description() {
        return "Imports and submits tasks to a server (from files or stdin)";
    }

    @Override
    public void printUsage()
    throws IOException {
        System.out.println("Usage: " + Launcher.APP_NAME + " " + name()
                + " file1 file2 ... fileN");
        System.out.println(description());
        System.out.println();
        parser.printHelpOn(System.out);
    }
}
