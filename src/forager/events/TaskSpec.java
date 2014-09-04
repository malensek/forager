package forager.events;

import java.io.IOException;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class TaskSpec implements Event {

    public int taskId;
    public String[] command;

    public TaskSpec(int taskId, String[] command) {
    private List<NetworkDestination> assignments = new ArrayList<>();
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
        String str = "[" + taskId + "] ";
        for (String s : command) {
            str += s + " ";
        }
        return str;
    }

    @Deserialize
    public TaskSpec(SerializationInputStream in)
    throws IOException {
        taskId = in.readInt();
        int numArgs = in.readInt();
        command = new String[numArgs];
        for (int i = 0; i < numArgs; ++i) {
            command[i] = in.readString();
        }
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeInt(taskId);
        out.writeInt(command.length);
        for (String s : command) {
            out.writeString(s);
        }
    }
}
