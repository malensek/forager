package forager.ui;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import forager.server.Overlord;

@Parameters(separators = "=",
        commandDescription = "Starts a forager master server")
public class Server implements CommandLauncher {

    @Parameter(names = { "-p", "--port" }, description = "Server port")
    private int port = Overlord.DEFAULT_PORT;

    @Override
    public void launch() throws Exception {
        Overlord server = new Overlord(port);
        server.start();
    }
}
