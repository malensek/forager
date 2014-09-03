package forager.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

    public void submitTasks(NetworkDestination server)
    throws Exception {
        ClientMessageRouter messageRouter = new ClientMessageRouter();
        messageRouter.addListener(eventReactor);
        BasicEventWrapper wrapper
            = new BasicEventWrapper(new ForagerEventMap());
        GalileoMessage message = wrapper.wrap(new ImportRequest(tasks));
        messageRouter.sendMessage(server, message);
        eventReactor.processNextEvent();
        messageRouter.shutdown();
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
        BufferedReader br = new BufferedReader(new FileReader(file));
        int added = 0;
        String line;
        while ((line = br.readLine()) != null) {
            tasks.add(line);
            added++;
        }
        br.close();

        System.out.println("Importing " + added + " tasks from file: " + file);
    }

    public static void main(String[] args) throws Exception {
        Importer i = new Importer();
        i.addTaskFile(args[0]);
        i.submitTasks(new NetworkDestination("localhost", Launcher.DEFAULT_PORT));
    }
}
