
package forager.events;
import java.util.HashMap;
import java.util.Map;

import galileo.event.EventTypeMap;

/**
 * Enumerates the various Forager events.
 *
 * @author malensek
 */
public enum ForagerEventType implements EventTypeMap {
    Unknown (0),
    Join (1),
    ProcessingDirectives (2);

    private final int type;

    private ForagerEventType(int type) {
        this.type = type;
    }

    @Override
    public int toInt() {
        return type;
    }

    static Map<Integer, ForagerEventType> typeMap = new HashMap<>();

    static {
        for (ForagerEventType t : ForagerEventType.values()) {
            typeMap.put(t.toInt(), t);
        }
    }

    public static ForagerEventType fromInt(int i) {
        ForagerEventType t = typeMap.get(i);
        if (t == null) {
            return ForagerEventType.Unknown;
        }

        return t;
    }
}
