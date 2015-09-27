package forager.newui;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import joptsimple.OptionParser;
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

        OptionSpec<File> taskList = parser.acceptsAll(
                Arrays.asList("t", "tasklist", "task-list"),
                "Path to the task list (created if it doesn't exist)")
            .withRequiredArg()
            .ofType(File.class)
            .defaultsTo(new File("./tasklist"));

        printUsage();
    }
}
