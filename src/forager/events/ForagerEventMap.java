
package forager.events;

import galileo.event.EventMap;

public class ForagerEventMap extends EventMap {

    public ForagerEventMap() {
        addMapping(1, JoinEvent.class);
        addMapping(2, TaskSpec.class);
    }
}
