package forager.newui;

import static java.util.Arrays.asList;

import java.util.List;

import forager.client.Forager;

import galileo.net.NetworkDestination;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Client implements Command {

    public static int DEFAULT_PORT = 53380;

    public void execute(String[] args) throws Exception {
        OptionParser parser = new OptionParser();

        OptionSpec<Integer> port = parser.acceptsAll(
                asList("p", "port"), "Server port")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(DEFAULT_PORT);

        int cpus = Runtime.getRuntime().availableProcessors();
        OptionSpec<Integer> threads = parser.acceptsAll(
                asList("t", "threads"),
                "Maximum number of threads the forager daemon can use")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(cpus);

        parser.nonOptions("Remote Hostname");

        OptionSet opts = parser.parse(args);
        List<?> nonOpts = opts.nonOptionArguments();

        if (nonOpts.size() <= 0) {
            System.out.println("No remote hostname specified!");
            parser.printHelpOn(System.out);
            return;
        }

        if (nonOpts.size() > 1) {
            for (int i = 1; i < nonOpts.size(); ++i) {
                System.out.println("Ignoring extra hostname parameter: "
                        + nonOpts.get(i));
            }
        }

        String hostname = (String) nonOpts.get(1);
        NetworkDestination server = new NetworkDestination(
                hostname, port.value(opts));
        Forager client = new Forager(server, threads.value(opts));
        client.start();
    }

    public String name() {
        return "client";
    }
}
