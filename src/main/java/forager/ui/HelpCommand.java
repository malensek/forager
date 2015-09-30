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

public class HelpCommand implements Command {

    Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    public void execute(String[] args) throws Exception {
        if (args.length <= 0) {
            printUsage();
            return;
        }

        Command cmd = commands.get(args[0]);
        if (cmd == null) {
            printUsage();
            return;
        }

        cmd.printUsage();
    }

    public String name() {
        return "help";
    }

    public void printUsage()
    throws IOException {
        System.out.println("Usage: forager " + name() + " command_name");
    }
}
