
package forager.events;

import java.io.IOException;

import galileo.event.Event;
import galileo.event.EventTypeMap;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class JoinEvent implements Event {

    private String resourceName = "";

    public JoinEvent() { }

    public JoinEvent(String resourceName) {
        this.resourceName = resourceName;
    }

    public EventTypeMap getTypeMap() {
        return ForagerEventType.Join;
    }

    @Deserialize
    public JoinEvent(SerializationInputStream in)
    throws IOException {
        this.resourceName = in.readString();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeString(resourceName);
    }
}
