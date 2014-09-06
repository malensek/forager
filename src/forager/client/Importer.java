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
import forager.ui.Launcher;

import galileo.event.BasicEventWrapper;
import galileo.event.EventContext;
import galileo.event.EventException;
import galileo.event.EventHandler;
import galileo.event.EventReactor;
import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.NetworkDestination;

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
        if (response.successful) {
            System.out.println("Successfully imported "
                    + tasks.size() + " tasks.");
        } else {
            System.out.println("Server could not complete import request!");
        }
    }
 
    public void addTaskFile(String file)
    throws FileNotFoundException, IOException {
        FileReader reader = new FileReader(file);
        int numTasks = readTaskData(reader);
        System.out.println("Importing " + numTasks + " tasks from " + file);
    }
 
    public int readTaskData(InputStreamReader reader)
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
