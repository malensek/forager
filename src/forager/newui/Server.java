package forager.newui;

import java.io.IOException;
import java.util.Arrays;

import forager.server.Overlord;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Server implements Command {

    public static int DEFAULT_PORT = 53380;

    private OptionParser parser = new OptionParser();

    public void execute(String[] args) throws Exception {
        OptionSpec<Integer> port = parser.acceptsAll(
                Arrays.asList("p", "port"), "Server port")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(DEFAULT_PORT);

        OptionSpec<?> reset = parser.acceptsAll(
                Arrays.asList("r", "reset"), "Reset (clear) the task list");

        OptionSpec<String> taskList = parser.acceptsAll(
                Arrays.asList("t", "tasklist", "task-list"),
                "Path to the task list (created if it doesn't exist)")
            .withRequiredArg()
            .ofType(String.class)
            .defaultsTo("./tasklist");

        OptionSet opts = parser.parse(args);

        boolean clear = opts.hasArgument(reset);
        Overlord server = new Overlord(
                port.value(opts), taskList.value(opts), clear);
        server.start();
    }

    public String name() {
        return "server";
    }

    public void printUsage()
    throws IOException {
        System.out.println("Usage: forager " + name() + " [options]");
        System.out.println();
        parser.printHelpOn(System.out);
    }

}

