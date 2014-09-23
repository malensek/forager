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

import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * Provides an executable entry point to Forager (this is the jar 'main' class).
 * Command line argument parsing is performed here, and then passed off to a
 * {@link CommandLauncher} instance for processing (usually instantiating and
 * initializing a particular class with the provided parameters).
 *
 * @author malensek
 */
public class Launcher {

    private static JCommander jc;
    private static final String appName = "forager";

    public static void main(String[] args) throws Exception {

        Launcher l = new Launcher();
        jc = new JCommander(l);
        jc.setProgramName(appName);

        Client client = new Client();
        Server server = new Server();
        Import importer = new Import();

        jc.addCommand("client", client);
        jc.addCommand("server", server);
        jc.addCommand("import", importer);

        Map<String, JCommander> cmds = jc.getCommands();

        try {
            jc.parse(args);

            JCommander command = cmds.get(jc.getParsedCommand());
            if (command == null) {
                usage();
            }

            CommandLauncher cl = (CommandLauncher) command.getObjects().get(0);
            cl.launch();

        } catch (ParameterException e) {
            if (jc.getParsedCommand() != null) {
                JCommander cmd = cmds.get(jc.getParsedCommand());
                usage(cmd, e.getMessage());
            } else {
                usage(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void usage() {
        usage("");
    }

    private static void usage(String message) {
        StringBuilder sb = new StringBuilder();
        String nl = System.lineSeparator();

        if (message.equals("") == false) {
            sb.append(message + nl);
        }

        sb.append("Usage: " + appName + " command [command options]" + nl);

        Map<String, JCommander> commands = jc.getCommands();
        for (String cmd : commands.keySet()) {
            sb.append("    " + cmd + "    ");
            sb.append(jc.getCommandDescription(cmd) + nl);
            commands.get(cmd).usage(sb, "    ");
            sb.append(nl);
        }
        System.out.print(sb.toString());
        System.exit(1);
    }

    private static void usage(JCommander jc, String message) {
        if (message.equals("") == false) {
            System.out.println(message);
        }
        jc.usage();
    }
}
