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

package forager.ui;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import forager.client.Importer;

import galileo.net.NetworkDestination;

@Parameters(separators = "=", commandDescription = "Imports and submits "
        + "tasks to a server (from files or stdin)")
public class Import implements CommandLauncher {

    @Parameter(description = "file1 file2 ... fileN")
    private List<String> files = new ArrayList<>();

    @Parameter(names = { "-p", "--port" }, description = "Server port")
    private int port = Server.DEFAULT_PORT;

    @Parameter(names = { "-s", "--server" },
            description = "Server to submit imported tasks to")
    private String server = "localhost";

    @Override
    public void launch() throws Exception {
        Importer i = new Importer();
        if (files.size() > 0) {
            for (String file : files) {
                int numTasks = i.addTaskFile(file);
                System.out.println("Importing " + numTasks + " tasks "
                        + "from file: " + file);
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
        boolean result = i.submitTasks(new NetworkDestination(server, port));
        if (result == true) {
            System.out.println("Import completed successfully!");
        } else {
            System.out.println("Import rejected by the server.");
        }
    }
}
