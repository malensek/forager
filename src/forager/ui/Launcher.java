package forager.ui;

import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=")
public class Launcher {

    @Parameter(names = { "-h", "--help" }, description = "help")
    public String helpCommand;

    private static JCommander jc;

    private static final String appName = "forager";

    public static void main(String[] args) throws Exception {

        Launcher l = new Launcher();
        jc = new JCommander(l);
        jc.setProgramName(appName);

        Client client = new Client();
        Server server = new Server();
        Import importer = new Import();

        jc.addCommand("client", client);
        jc.addCommand("server", server);
        jc.addCommand("import", importer);

        try {
            jc.parse(args);

            Map<String, JCommander> cmds = jc.getCommands();

            if (l.helpCommand != null) {
                JCommander command = cmds.get(l.helpCommand);
                if (command != null) {
                    command.usage();
                    System.exit(1);
                }
            }

            JCommander command = cmds.get(jc.getParsedCommand());
            if (command == null) {
                usage();
            }

            CommandLauncher cl = (CommandLauncher) command.getObjects().get(0);
            cl.launch();

        } catch (Exception e) {
            usage(e.getMessage());
        }
    }

    private static void usage() {
        usage("");
    }

    private static void usage(String message) {
        StringBuilder sb = new StringBuilder();
        String nl = System.lineSeparator();

        if (message.equals("") == false) {
            sb.append(message + nl);
        }

        sb.append("Usage: " + appName + " command [command options]" + nl);
        sb.append(nl);
        sb.append("  Commands:  ");
        sb.append("('forager --help command' for specific command info)"
                + nl + nl);

        Map<String, JCommander> commands = jc.getCommands();
        for (String cmd : commands.keySet()) {
            sb.append("    " + cmd + "    ");
            sb.append(jc.getCommandDescription(cmd) + nl);
            commands.get(cmd).usage(sb, "    ");
            sb.append(nl);
        }
        System.out.println(sb.toString());
        System.exit(1);
    }
}
