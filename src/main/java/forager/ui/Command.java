package forager.ui;

import java.io.IOException;

interface Command {

    public int execute(String[] args) throws Exception;

    public String name();

    public String description();

    public void printUsage() throws IOException;
}
