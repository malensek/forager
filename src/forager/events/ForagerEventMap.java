
package forager.events;

import galileo.event.EventMap;

public class ForagerEventMap extends EventMap {

    public ForagerEventMap() {
        addMapping(JoinEvent.class);
        addMapping(TaskRequest.class);
        addMapping(TaskSpec.class);
    }
}
