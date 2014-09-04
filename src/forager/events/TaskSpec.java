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

    private List<NetworkDestination> assignments = new ArrayList<>();

    public TaskSpec(long taskId, String command) {
        this.taskId = taskId;
        this.command = command;
    }

    public void addAssignment(NetworkDestination host) {
        assignments.add(host);
    }

    public void removeAssignment(NetworkDestination host) {
        assignments.remove(host);
    }

    /**
     * Determine whether the Task associated with this TaskSpec has been
     * assigned to any resources yet.
     *
     * @return true if the Task has been assigned, false otherwise.
     */
    public boolean isAssigned() {
        return assignments.isEmpty() == false;
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
