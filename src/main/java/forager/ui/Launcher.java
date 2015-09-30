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

import java.util.HashMap;
import java.util.Map;

public class Launcher {

    public static final String APP_NAME = "forager";

    public static void main(String[] args) throws Exception {
        ClientCommand clientCmd = new ClientCommand();
        ServerCommand serverCmd = new ServerCommand();
        ImportCommand importCmd = new ImportCommand();
        Map<String, Command> commands = new HashMap<>();
        commands.put(clientCmd.name(), clientCmd);
        commands.put(serverCmd.name(), serverCmd);
        commands.put(importCmd.name(), importCmd);

        HelpCommand helpCmd = new HelpCommand(commands);
        commands.put(helpCmd.name(), helpCmd);

        if (args.length < 1) {
            System.out.println("Usage: " + APP_NAME
                    + " command [command options]");
            System.out.println();
            System.out.println("Commands");
            System.out.println("--------");
            for (Command c : commands.values()) {
                System.out.println(c.name() + "  -  " + c.description());
            }
            System.out.println();
            return;
        }


        String commandName = args[0].toLowerCase();

        Command cmd = commands.get(commandName);
        if (cmd != null) {
            String[] argList = new String[args.length - 1];
            for (int i = 0; i < argList.length; ++i) {
                argList[i] = args[i + 1];
            }
            try {
                cmd.execute(argList);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                cmd.printUsage();
            }
        }

    }
}
