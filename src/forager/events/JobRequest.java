package forager.events;

import java.io.IOException;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class JobRequest implements Event {

    public JobRequest() { }

    @Deserialize
    public JobRequest(SerializationInputStream in)
    throws IOException { }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException { }
}
