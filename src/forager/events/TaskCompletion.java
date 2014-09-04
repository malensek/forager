package forager.events;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

import java.io.IOException;

public class TaskCompletion implements Event {

    public long taskId;

    public TaskCompletion(long taskId) {
        this.taskId = taskId;
    }

    @Deserialize
    public TaskCompletion(SerializationInputStream in)
    throws IOException {
        taskId = in.readLong();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeLong(taskId);
    }
}
