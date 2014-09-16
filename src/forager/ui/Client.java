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

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import forager.client.Forager;

import galileo.net.NetworkDestination;

@Parameters(separators = "=",
        commandDescription = "Starts a forager client")
public class Client implements CommandLauncher {

    @Parameter(description = "server_hostname", required = true)
    private List<String> hostname = new ArrayList<>();

    @Parameter(names = { "-p", "--port" }, description = "Server port")
    private int port = Server.DEFAULT_PORT;

    @Parameter(names = { "-t", "--threads" }, description = "Maximum number "
            + "of threads the forager daemon can use")
    private int threads = 4;

    @Override
    public void launch() throws Exception {
        String host = hostname.get(0);
        if (hostname.size() > 1) {
            for (int i = 1; i < hostname.size(); ++i) {
                System.out.println("Ignoring extra hostname parameter: "
                        + hostname.get(i));
            }
        }

        NetworkDestination server = new NetworkDestination(host, port);
        Forager client = new Forager(server, threads);
        client.start();
    }
}
