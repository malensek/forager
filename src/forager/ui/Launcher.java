package forager.ui;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;

import forager.client.Forager;
import forager.server.Overlord;

import galileo.net.NetworkDestination;

public class Launcher {

    public static final int DEFAULT_PORT = 53380;

    public static void main(String[] args)
    throws Exception {
        CommandLineParser parser = new GnuParser();
        Options options = new Options();

        options.addOption("s", "server", false,
                "Start a server daemon");
        options.addOption("p", "port", true,
                "Specifies the port to connect to or listen on.");
        options.addOption("t", "threads", true,
                "Maximum number of threads to use (client only)");

        CommandLine cl = null;
        try {
            cl = parser.parse(options, args);
        } catch (UnrecognizedOptionException e) {
            printUsage();
        }

        if (cl.hasOption("server")) {
            Overlord server = new Overlord(getPort(cl));
            server.start();
        } else {
            Forager client;

            String[] clientArgs = cl.getArgs();
            if (args.length < 1) {
                printUsage();
            }
            NetworkDestination server = new NetworkDestination(
                    clientArgs[0], getPort(cl));

            if (cl.hasOption("threads")) {
                int threads = Integer.parseInt(cl.getOptionValue("threads"));
                client = new Forager(server, threads);
            } else {
                client = new Forager(server);
            }

            client.start();
        }
        System.out.println(cl.getArgs()[0]);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ant", options);
    }

    private static int getPort(CommandLine cl) {
        int port;
        if (cl.hasOption("port")) {
            port = Integer.parseInt(cl.getOptionValue("port"));
        } else {
            port = DEFAULT_PORT;
        }

        return port;
    }

    private static void printUsage() {
        printUsage(true);
    }

    private static void printUsage(boolean exit) {
        //TODO
        if (exit) {
            System.exit(1);
        }
    }
}
