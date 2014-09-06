
package forager.ui;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import forager.client.Importer;
import forager.server.Overlord;

import galileo.net.NetworkDestination;

@Parameters(separators = "=", commandDescription = "Imports and submits "
        + "tasks to a server (from files or stdin)")
public class Import implements CommandLauncher {

    @Parameter(description = "file1 file2 ... fileN")
    private List<String> files = new ArrayList<>();

    @Parameter(names = { "-p", "--port" }, description = "Server port")
    private int port = Overlord.DEFAULT_PORT;

    @Parameter(names = { "-s", "--server" },
            description = "Server to submit imported tasks to")
    private String server = "localhost";

    @Override
    public void launch() throws Exception {
        Importer i = new Importer();
        if (files.size() > 0) {
            for (String file : files) {
                int numTasks = i.addTaskFile(file);
                System.out.println("Importing " + numTasks + " tasks "
                        + "from file: " + file);
            }
        } else {
            if (System.console() == null) {
                /* Something is being piped in */
                InputStreamReader reader = new InputStreamReader(System.in);
                int numTasks = i.addFromInputStream(reader);
                System.out.println("Importing " + numTasks + " tasks "
                        + "from stdin.");
            }
        }
        boolean result = i.submitTasks(new NetworkDestination(server, port));
        if (result == true) {
            System.out.println("Import completed successfully!");
        } else {
            System.out.println("Import rejected by the server.");
        }
    }
}
