package forager.newui;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import forager.client.Importer;

import galileo.net.NetworkDestination;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Import implements Command {

    private OptionParser parser = new OptionParser();

    public void execute(String[] args) throws Exception {
        OptionSpec<Integer> port = parser.acceptsAll(
                Arrays.asList("p", "port"), "Server port")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(Server.DEFAULT_PORT);

        OptionSpec<String> server = parser.acceptsAll(
                Arrays.asList("s", "server"),
                "Server to submit imported tasks to")
            .withRequiredArg()
            .ofType(String.class)
            .defaultsTo("localhost");

        parser.nonOptions("files");

        OptionSet opts = parser.parse(args);
        List<?> files = opts.nonOptionArguments();

        Importer i = new Importer();
        if (files.size() > 0) {
            for (Object file : files) {
                int numTasks = i.addTaskFile((String) file);
                System.out.println("Importing " + numTasks + " tasks "
                        + "from file: " + ((String) file));
            }
        } else if (System.console() == null) {
            /* Something is being piped in */
            InputStreamReader reader = new InputStreamReader(System.in);
            int numTasks = i.addFromInputStream(reader);
            System.out.println("Importing " + numTasks + " tasks "
                    + "from stdin.");
        } else {
            throw new Exception("No input files specified or data from stdin.");
        }

        boolean result = i.submitTasks(new NetworkDestination(
                    server.value(opts), port.value(opts)));

        if (result == true) {
            System.out.println("Import completed successfully!");
        } else {
            System.out.println("Import rejected by the server.");
        }
    }

    public String name() {
        return "import";
    }

    public void printUsage()
    throws IOException {
        System.out.println("Usage: forager " + name()
                + " file1 file2 ... fileN");
        System.out.println();
        parser.printHelpOn(System.out);
    }
}
