package forager.newui;

import java.util.HashMap;
import java.util.Map;

public class Launcher {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            printHelp();
        }

        Client clientCommand = new Client();

        Map<String, Command> commands = new HashMap<>();
        commands.put(clientCommand.name(), clientCommand);

        String commandName = args[0].toLowerCase();

        Command cmd = commands.get(commandName);
        if (cmd != null) {
            String[] argList = new String[args.length - 1];
            for (int i = 0; i < argList.length; ++i) {
                argList[i] = args[i + 1];
            }
            cmd.execute(argList);
        }

    }

    private static void printHelp() {
        System.out.println("usage: ...");

    }
}
