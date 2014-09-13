
package forager.ui;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import forager.client.Forager;

import galileo.net.NetworkDestination;

@Parameters(separators = "=",
        commandDescription = "Starts a forager client")
public class Client implements CommandLauncher {

    @Parameter(description = "server_hostname", required = true)
    private List<String> hostname = new ArrayList<>();

    @Parameter(names = { "-p", "--port" }, description = "Server port")
    private int port = Server.DEFAULT_PORT;

    @Parameter(names = { "-t", "--threads" }, description = "Maximum number "
            + "of threads the forager daemon can use")
    private int threads = 4;

    @Override
    public void launch() throws Exception {
        String host = hostname.get(0);
        if (hostname.size() > 1) {
            for (int i = 1; i < hostname.size(); ++i) {
                System.out.println("Ignoring extra hostname parameter: "
                        + hostname.get(i));
            }
        }

        NetworkDestination server = new NetworkDestination(host, port);
        Forager client = new Forager(server, threads);
        client.start();
    }
}
