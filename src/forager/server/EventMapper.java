
package forager.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import galileo.event.Event;
import galileo.event.EventHandler;
import galileo.event.EventTypeMap;
import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.Serializer;

public class EventMapper implements MessageListener {

    private static final Logger logger = Logger.getLogger("galileo");

    private Class<?> handlerClass;
    private Object handlerObject;

    private EventMap eventMap;

    private Map<Class<?>, Method> classToMethod = new HashMap<>();

    private ConcurrentLinkedQueue<GalileoMessage> messageQueue
        = new ConcurrentLinkedQueue<>();

    public EventMapper(Object handlerObject, EventMap eventMap) {
        this.handlerClass = handlerObject.getClass();
        this.handlerObject = handlerObject;
        this.eventMap = eventMap;
        System.out.println(eventMap.getClass(1));
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


    @Override
    public void onConnect(NetworkDestination endpoint) {

    }

    @Override
    public void onDisconnect(NetworkDestination endpoint) {

    }

    @Override
    public void onMessage(GalileoMessage message) {
        messageQueue.offer(message);
//        ByteArrayInputStream byteIn
//            = new ByteArrayInputStream(message.getPayload());
//        BufferedInputStream buffIn = new BufferedInputStream(byteIn);
//        SerializationInputStream in = new SerializationInputStream(buffIn);
//
//        try {
//            int type = in.readInt();
//            Class<? extends Event> clazz = eventMap.getClass(type);
//            if (clazz == null) {
//                logger.log(Level.WARNING,
//                        "Class mapping for event type {0} not found!", type);
//                in.close();
//                return;
//            }
//            Event e = Serializer.deserializeFromStream(clazz, in);
//            Method m = classToMethod.get(clazz);
//            m.invoke(handlerObject, e);
//            in.close();
//        } catch (Exception e) {
//            logger.log(Level.WARNING, "Error handling incoming message", e);
//        }
    }
}
