package forager.ui;

import java.io.IOException;

interface Command {

    public void execute(String[] args) throws Exception;

    public String name();

    public void printUsage() throws IOException;
}
