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
import java.util.Map;

import forager.client.Forager;

import galileo.net.NetworkDestination;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * This command retrieves usage information for other commands.
 *
 * @author malensek
 */
public class HelpCommand implements Command {

    Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public int execute(String[] args) throws Exception {
        if (args.length <= 0) {
            printUsage();
            return 1;
        }

        Command cmd = commands.get(args[0]);
        if (cmd == null) {
            printUsage();
            return 1;
        }

        cmd.printUsage();

        return 0;
    }

    public String name() {
    @Override
        return "help";
    }

    @Override
    public String description() {
        return "Provides command usage information";

    }

    @Override
    public void printUsage()
    throws IOException {
        System.out.println("Usage: " + Launcher.APP_NAME + " "
                + name() + " command_name");
    }
}
