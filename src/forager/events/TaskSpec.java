package forager.events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import galileo.event.Event;
import galileo.net.NetworkDestination;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class TaskSpec implements Event {

    public long taskId;
    public String command;
    public List<NetworkDestination> assignments = new ArrayList<>();

    public TaskSpec(long taskId, String command) {
        this.taskId = taskId;
        this.command = command;
    }

    public String toString() {
        return "[" + taskId + "] " + command;
    }

    @Deserialize
    public TaskSpec(SerializationInputStream in)
    throws IOException {
        taskId = in.readLong();
        command = in.readString();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeLong(taskId);
        out.writeString(command);
    }
}
