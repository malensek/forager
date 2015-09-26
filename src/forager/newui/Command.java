package forager.newui;

interface Command {

    public void execute(String[] args) throws Exception;

    public String name();

    public String usage();
}
