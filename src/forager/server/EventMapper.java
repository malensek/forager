
package forager.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import galileo.event.Event;
import galileo.event.EventHandler;
import galileo.event.EventTypeMap;

public class EventMapper {

    private static final Logger logger = Logger.getLogger("galileo");

    private Class<?> handlerClass;
    private Object handlerObject;
    private Map<Class<?>, Method> classToMethod = new HashMap<>();

    public EventMapper(Object handlerObject) {
        this.handlerClass = handlerObject.getClass();
        this.handlerObject = handlerObject;
    }

    public <T extends Event> void map(EventTypeMap typeMap, Class<T> type) {

    }

    public void linkEventHandlers() {
        for (Method m : handlerClass.getMethods()) {
            for (Annotation a : m.getAnnotations()) {
                if (a.annotationType().equals(EventHandler.class)) {
                    /* This method is an event handler */
                    Class<?>[] params = m.getParameterTypes();
                    Class<?> eventClass = extractEventClass(params);
                    logger.log(Level.INFO,
                            "Linking handler method [{0}] to class [{1}]",
                            new Object[] { m.getName(), eventClass.getName() });
                    classToMethod.put(eventClass, m);
                    break;
                }
            }
        }
    }

    private Class<?> extractEventClass(Class<?>[] parameters) {
        if (parameters.length <= 0) {
            //TODO throw new exception
        }

        List<Class<?>> interfaces
            = Arrays.asList(parameters[0].getInterfaces());
        if (interfaces.contains(Event.class) == false) {
            //TODO throw error
        }

        return parameters[0];
        //classToMethod.put(parameters[0], m);
    }
}
