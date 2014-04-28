
package forager.events;

import forager.server.EventMap;

public class ForagerEventMap extends EventMap {

    public ForagerEventMap() {
        addMapping(1, JoinEvent.class);
    }
}
