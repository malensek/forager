package forager.events;

import java.io.IOException;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class TaskSpec implements Event {

    public long taskId;
    public String[] command;

    public TaskSpec(long taskId, String[] command) {
        this.taskId = taskId;
        this.command = command;
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
        taskId = in.readLong();
        int numArgs = in.readInt();
        command = new String[numArgs];
        for (int i = 0; i < numArgs; ++i) {
            command[i] = in.readString();
        }
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeLong(taskId);
        out.writeInt(command.length);
        for (String s : command) {
            out.writeString(s);
        }
    }
}
