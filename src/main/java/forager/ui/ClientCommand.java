package forager.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import forager.client.Forager;

import galileo.net.NetworkDestination;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class ClientCommand implements Command {

    private OptionParser parser = new OptionParser();

    private OptionSpec<Integer> port;
    private OptionSpec<Integer> threads;

    public ClientCommand() {
        port = parser.acceptsAll(
                Arrays.asList("p", "port"), "Server port")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(ServerCommand.DEFAULT_PORT);

        int cpus = Runtime.getRuntime().availableProcessors();
        threads = parser.acceptsAll(
                Arrays.asList("t", "threads"),
                "Maximum number of threads the forager daemon can use")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(cpus);

        parser.nonOptions("server_hostname");
    }

    public int execute(String[] args) throws Exception {
        OptionSet opts = parser.parse(args);
        List<?> nonOpts = opts.nonOptionArguments();

        if (nonOpts.size() <= 0) {
            System.out.println("No server hostname specified!");
            printUsage();
            return 1;
        }

        if (nonOpts.size() > 1) {
            for (int i = 1; i < nonOpts.size(); ++i) {
                System.out.println("Ignoring extra hostname parameter: "
                        + nonOpts.get(i));
            }
        }

        String hostname = (String) nonOpts.get(0);
        NetworkDestination server = new NetworkDestination(
                hostname, port.value(opts));
        Forager client = new Forager(server, threads.value(opts));
        client.start();
        return 0;
    }

    public String name() {
        return "client";
    }

    public String description() {
        return "Starts a forager client";
    }

    public void printUsage()
    throws IOException {
        System.out.println("Usage: " + Launcher.APP_NAME + " " + name()
                + " [options] server_hostname");
        System.out.println(description());
        System.out.println();
        parser.printHelpOn(System.out);
    }
}
