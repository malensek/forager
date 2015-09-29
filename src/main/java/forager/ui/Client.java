package forager.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import forager.client.Forager;

import galileo.net.NetworkDestination;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Client implements Command {

    private OptionParser parser = new OptionParser();

    public void execute(String[] args) throws Exception {
        OptionSpec<Integer> port = parser.acceptsAll(
                Arrays.asList("p", "port"), "Server port")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(Server.DEFAULT_PORT);

        int cpus = Runtime.getRuntime().availableProcessors();
        OptionSpec<Integer> threads = parser.acceptsAll(
                Arrays.asList("t", "threads"),
                "Maximum number of threads the forager daemon can use")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(cpus);

        parser.nonOptions("server_hostname");

        OptionSet opts = parser.parse(args);
        List<?> nonOpts = opts.nonOptionArguments();

        if (nonOpts.size() <= 0) {
            System.out.println("No server hostname specified!");
            printUsage();
            return;
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
    }

    public String name() {
        return "client";
    }

    public void printUsage()
    throws IOException {
        System.out.println("Usage: forager " + name()
                + " [options] server_hostname");
        System.out.println();
        parser.printHelpOn(System.out);
    }
}
