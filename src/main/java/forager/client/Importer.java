/*
Copyright (c) 2014, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

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

import io.elssa.event.EventContext;
import io.elssa.event.EventHandler;
import io.elssa.event.EventLinkException;
import io.elssa.event.EventReactor;
import io.elssa.net.ClientMessageRouter;
import io.elssa.net.NetworkEndpoint;

/**
 * Imports task data from a task list file and submits it to a given Forager
 * server daemon.
 *
 * @author malensek
 */
public class Importer {

    private ForagerEventMap eventMap = new ForagerEventMap();
    private EventReactor eventReactor;

    private List<String> tasks = new ArrayList<>();

    private boolean successful;

    public boolean submitTasks(NetworkEndpoint server)
    throws Exception {
        eventReactor = new EventReactor(this, eventMap);
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
