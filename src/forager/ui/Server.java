package forager.ui;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import forager.server.Overlord;

@Parameters(separators = "=",
        commandDescription = "Starts a forager master server")
public class Server implements CommandLauncher {

    public static final int DEFAULT_PORT = 53380;
    public static final String DEFAULT_TASKLIST = "./tasklist";

    @Parameter(names = { "-p", "--port" }, description = "Server port")
    private int port = DEFAULT_PORT;

    @Parameter(names = { "-t", "--task-list" },
            description = "Task list location")
    private String taskList = DEFAULT_TASKLIST;

    @Override
    public void launch() throws Exception {
        Overlord server = new Overlord(port, taskList);
        server.start();
    }
}
