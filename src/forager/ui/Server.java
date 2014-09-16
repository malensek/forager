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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import forager.server.Overlord;

@Parameters(separators = "=",
        commandDescription = "Starts a forager master server")
public class Server implements CommandLauncher {

    public static final int DEFAULT_PORT = 53380;
    public static final String DEFAULT_TASKLIST = "./tasklist";

    @Parameter(names = { "-r", "--reset" },
            description = "Reset (clear) the task list")
    private boolean clear = false;

    @Parameter(names = { "-p", "--port" }, description = "Server port")
    private int port = DEFAULT_PORT;

    @Parameter(names = { "-t", "--task-list", "--tasklist" },
            description = "Path to the task list (created if it doesn't exist)")
    private String taskList = DEFAULT_TASKLIST;

    @Override
    public void launch() throws Exception {
        Overlord server = new Overlord(port, taskList, clear);
        server.start();
    }
}
