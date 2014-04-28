
package forager.server;

import java.util.HashMap;
import java.util.Map;

import galileo.event.Event;

public abstract class EventMap {

    protected final Map<Integer, Class<? extends Event>>
        intToClass = new HashMap<>();
    protected final Map<Class<? extends Event>, Integer>
        classToInt = new HashMap<>();

    public EventMap() { }

    protected void addMapping(int id, Class<? extends Event> clazz) {
        intToClass.put(id, clazz);
        classToInt.put(clazz, id);
    }

    public Class<? extends Event> getClass(int id) {
        return intToClass.get(id);
    }

    public int getInt(Class<?> clazz) {
        return classToInt.get(clazz);
    }
}
