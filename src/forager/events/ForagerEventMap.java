
package forager.events;

import forager.server.EventMap;

public class ForagerEventMap extends EventMap {

    static {
        addMapping(1, JoinEvent.class);
    }
}
