/*
Copyright (c) 2014, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package forager.events;

import java.io.IOException;

import io.elssa.event.Event;
import io.elssa.serialization.SerializationInputStream;
import io.elssa.serialization.SerializationOutputStream;

public class ImportResponse implements Event  {

    public boolean successful;
    public String details;

    public ImportResponse(boolean successful) {
        this(successful, "");
    }

    public ImportResponse(boolean successful, String details) {
        this.successful = successful;
        this.details = details;
    }

    @Deserialize
    public ImportResponse(SerializationInputStream in)
    throws IOException {
        this.successful = in.readBoolean();
        this.details = in.readString();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeBoolean(this.successful);
        out.writeString(this.details);
    }
}
