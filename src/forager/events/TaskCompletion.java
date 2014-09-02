package forager.events;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

import java.io.IOException;

public class TaskCompletion implements Event {

    public int taskId;

    public TaskCompletion(int taskId) {
        this.taskId = taskId;
    }

    @Deserialize
    public TaskCompletion(SerializationInputStream in)
    throws IOException {
        taskId = in.readInt();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeInt(taskId);
    }

}
