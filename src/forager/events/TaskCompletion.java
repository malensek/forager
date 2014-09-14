package forager.events;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

import java.io.IOException;

public class TaskCompletion implements Event {

    public long taskId;
    public int exitCode;

    public TaskCompletion(long taskId) {
        this.taskId = taskId;
        this.exitCode = 0;
    }

    public TaskCompletion(long taskId, int exitCode) {
        this.taskId = taskId;
        this.exitCode = exitCode;
    }

    @Deserialize
    public TaskCompletion(SerializationInputStream in)
    throws IOException {
        taskId = in.readLong();
        exitCode = in.readInt();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeLong(taskId);
        out.writeInt(exitCode);
    }
}
