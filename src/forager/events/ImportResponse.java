package forager.events;

import java.io.IOException;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;


public class ImportResponse implements Event  {

    public boolean successful;
    public String detail;

    public ImportResponse(boolean successful) {
        this(successful, "");
    }

    public ImportResponse(boolean successful, String detail) {
        this.successful = successful;
        this.detail = detail;
    }

    @Deserialize
    public ImportResponse(SerializationInputStream in)
    throws IOException {
        this.successful = in.readBoolean();
        this.detail = in.readString();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeBoolean(this.successful);
        out.writeString(this.detail);
    }
}
