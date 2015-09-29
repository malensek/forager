package forager.ui;

import java.util.HashMap;
import java.util.Map;

public class Launcher {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            printHelp();
        }

        Client clientCmd = new Client();
        Server serverCmd = new Server();
        Import importCmd = new Import();

        Map<String, Command> commands = new HashMap<>();
        commands.put(clientCmd.name(), clientCmd);
        commands.put(serverCmd.name(), serverCmd);
        commands.put(importCmd.name(), importCmd);

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

    private static void printHelp() {
        System.out.println("usage: ...");

    }
}
