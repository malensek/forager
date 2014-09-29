package forager.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import forager.events.ForagerEventMap;
import forager.events.ImportRequest;
import forager.events.ImportResponse;

import galileo.event.EventContext;
import galileo.event.EventHandler;
import galileo.event.EventReactor;
import galileo.net.ClientMessageRouter;
import galileo.net.NetworkDestination;

/**
 * Imports task data from a task list file and submits it to a given Forager
 * server daemon.
 *
 * @author malensek
 */
public class Importer {

    private ForagerEventMap eventMap = new ForagerEventMap();
    private EventReactor eventReactor = new EventReactor(this, eventMap);

    private List<String> tasks = new ArrayList<>();

    private boolean successful;

    public boolean submitTasks(NetworkDestination server)
    throws Exception {
        ClientMessageRouter messageRouter = new ClientMessageRouter();
        messageRouter.addListener(eventReactor);
        ImportRequest request = new ImportRequest(tasks);

        messageRouter.sendMessage(server, eventReactor.wrapEvent(request));
        eventReactor.processNextEvent();
        boolean result = successful;

        successful = false;
        tasks.clear();
        messageRouter.shutdown();

        return result;
    }

    @EventHandler
    public void processResponse(ImportResponse response, EventContext context) {
        successful = response.successful;
    }

    public int addTaskFile(String file)
    throws FileNotFoundException, IOException {
        FileReader reader = new FileReader(file);
        int numTasks = addFromInputStream(reader);
        return numTasks;
    }

    public int addFromInputStream(InputStreamReader reader)
    throws IOException {
        BufferedReader br = new BufferedReader(reader);
        int added = 0;
        String line;
        while ((line = br.readLine()) != null) {
            tasks.add(line);
            added++;
        }
        br.close();
        return added;
    }
}
