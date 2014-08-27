package forager.events;

import java.io.IOException;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class TaskRequest implements Event {

    public int numTasks;

    public TaskRequest(int numTasks) {
        this.numTasks = numTasks;
    }

    @Deserialize
    public TaskRequest(SerializationInputStream in)
    throws IOException {
        this.numTasks = in.readInt();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeInt(numTasks);
    }
}
