package forager.events;

import java.io.IOException;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class Job implements Event {

    private String command;

    public Job(String command) {
        this.command = command;
    }

    public String toString() {
        return command;
    }

    @Deserialize
    public Job(SerializationInputStream in)
    throws IOException {
        this.command = in.readString();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeString(this.command);
    }
}
