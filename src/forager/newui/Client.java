package forager.newui;

import static java.util.Arrays.asList;

import forager.client.Forager;

import galileo.net.NetworkDestination;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

class Client implements Command {

    public void execute(String[] args) throws Exception {
        OptionParser parser = new OptionParser();

        OptionSpec<Integer> port = parser.acceptsAll(
                asList("p", "port"), "Server port")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(5555);

        int cpus = Runtime.getRuntime().availableProcessors();
        OptionSpec<Integer> threads = parser.acceptsAll(
                asList("t", "threads"),
                "Maximum number of threads the forager daemon can use")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(cpus);

        OptionSet opts = parser.parse(args);


        for (Object s : opts.nonOptionArguments()) {
            System.out.println(s);
        }

        port.value(opts);

        String host = "test";
        NetworkDestination server = new NetworkDestination(
                host, port.value(opts));
        Forager client = new Forager(server, threads.value(opts));
        client.start();
    }

    public String name() {
        return "client";
    }

}
