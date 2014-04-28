
package forager.server;

import java.util.HashMap;
import java.util.Map;

public abstract class EventMap {

    protected static final Map<Integer, Class<?>> intToClass = new HashMap<>();
    protected static final Map<Class<?>, Integer> classToInt = new HashMap<>();

    public EventMap() { }

    protected static void addMapping(int id, Class<?> clazz) {
        intToClass.put(id, clazz);
        classToInt.put(clazz, id);
    }

    public static Class<?> getClass(int id) {
        return intToClass.get(id);
    }

    public static int getInt(Class<?> clazz) {
        return classToInt.get(clazz);
    }
}
