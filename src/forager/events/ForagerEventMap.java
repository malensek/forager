
package forager.events;

public class ForagerEventMap extends EventMap {

    static {
        addMapping(1, forager.events.JoinEvent.class);
    }
}
