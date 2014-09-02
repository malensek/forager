package forager.ui;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class Launcher {

    public static void main(String[] args)
    throws Exception {

        CommandLineParser parser = new GnuParser();
        Options options = new Options();

        options.addOption("s", "server", false,
                "Start a server daemon");
        options.addOption("p", "port", true,
                "Specifies the port to connect to or listen on.");

        CommandLine cl = parser.parse(options, args);
        if (cl.hasOption("server")) {
            System.out.println("starting server");
            System.out.println(cl.getOptionValue("port"));
        }
        System.out.println(cl.getArgs()[0]);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ant", options);
    }
}
